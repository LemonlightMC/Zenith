package com.lemonlightmc.zenith.version;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class VersionConstraint {
  private static final Pattern CONSTRAINT_PART = Pattern.compile(
      "([<>=!]+)?\\s*([^\\s<>=!]+)");

  public static VersionConstraintExact exact(final Version version) {
    if (version == null) {
      throw new IllegalArgumentException("VersionConstraint Exact Version cannot be null");
    }
    return new VersionConstraintExact(version);
  }

  public static VersionConstraintRange range(final VersionRange range) {
    if (range == null) {
      throw new IllegalArgumentException("VersionConstraint Version Range cannot be null");
    }
    return new VersionConstraintRange(range);
  }

  public static VersionConstraintRange range(final Version min, final Version max) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Min Version cannot be null");
    }
    if (max == null) {
      throw new IllegalArgumentException("VersionConstraint Max Version cannot be null");
    }
    return new VersionConstraintRange(new VersionRange(min, max));
  }

  public static VersionConstraintRange range(final Version min, final boolean minInclusive, final Version max,
      final boolean maxInclusive) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Min Version cannot be null");
    }
    if (max == null) {
      throw new IllegalArgumentException("VersionConstraint Max Version cannot be null");
    }
    return new VersionConstraintRange(new VersionRange(min, minInclusive, max, maxInclusive));
  }

  public static VersionConstraintMinimum atleast(final Version min) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Version Range cannot be null");
    }
    return new VersionConstraintMinimum(min, true);
  }

  public static VersionConstraintMinimum atleast(final Version min, final boolean inclusive) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Version Range cannot be null");
    }
    return new VersionConstraintMinimum(min, inclusive);
  }

  public static VersionConstraintLatest latest() {
    return new VersionConstraintLatest();
  }

  public static VersionConstraint parse(final String expression) {
    if (expression == null || expression.isBlank() || expression.equals("*")) {
      return latest();
    }

    final String trimmed = expression.trim();

    // Check for operators
    if (!trimmed.contains(">") && !trimmed.contains("<") && !trimmed.contains("=")) {
      // Simple version string - exact match
      return exact(Version.semver(trimmed));
    }

    Version min = null;
    boolean minInclusive = false;
    Version max = null;
    boolean maxInclusive = false;

    final Matcher matcher = CONSTRAINT_PART.matcher(trimmed);
    while (matcher.find()) {
      final String op = matcher.group(1);
      final Version version = Version.semver(matcher.group(2));

      if (op == null || op.isEmpty() || op.equals("=") || op.equals("==")) {
        // Exact match
        return exact(version);
      }

      switch (op) {
        case ">=":
          min = version;
          minInclusive = true;
          break;
        case ">":
          min = version;
          minInclusive = false;
          break;
        case "<=":
          max = version;
          maxInclusive = true;
          break;
        case "<":
          max = version;
          maxInclusive = false;
          break;
        default:
          throw new IllegalArgumentException("Unknown operator: " + op);
      }
    }

    if (min == null && max == null) {
      throw new IllegalArgumentException("Could not parse constraint: " + expression);
    }

    return range(min, minInclusive, max, maxInclusive);
  }

  public static VersionConstraint fromPolicy(final Version baseline, final UpdatePolicy policy) {
    switch (policy) {
      case NONE:
        return exact(baseline);
      case PATCH:
        // Allow X.Y.* where X.Y matches baseline
        final Version patchMax = Version.semver(baseline.major() + "." + (baseline.minor() + 1) + ".0");
        return range(baseline, true, patchMax, false);
      case MINOR:
        // Allow X.*.* where X matches baseline
        final Version minorMax = Version.semver((baseline.major() + 1) + ".0.0");
        return range(baseline, true, minorMax, false);
      case MAJOR:
      default:
        return atleast(baseline);
    }
  }

  public abstract boolean isSatisfiedBy(Version version);

  public abstract VersionConstraint merge(VersionConstraint other);

  public Version selectBest(final List<Version> available) {
    if (available == null || available.isEmpty()) {
      return null;
    }

    final List<Version> sorted = new ArrayList<>(available);
    Collections.sort(sorted, Collections.reverseOrder()); // Highest first

    for (final Version v : sorted) {
      if (isSatisfiedBy(v)) {
        return v;
      }
    }

    return null;
  }

  public boolean isExact() {
    return this instanceof VersionConstraintExact;
  }

  public boolean isLatest() {
    return this instanceof VersionConstraintLatest;
  }

  public boolean isRange() {
    return this instanceof VersionConstraintRange;
  }

  public boolean isMinimum() {
    return this instanceof VersionConstraintMinimum;
  }

  private static class VersionConstraintExact extends VersionConstraint {

    private final Version version;

    public VersionConstraintExact(final Version version) {
      this.version = version;
    }

    @Override
    public boolean isSatisfiedBy(final Version version) {
      return this.version.equals(version);
    }

    @Override
    public VersionConstraint merge(final VersionConstraint other) {
      return other == null || other.isSatisfiedBy(this.version) ? this : null;
    }
  }

  private static class VersionConstraintLatest extends VersionConstraint {

    public VersionConstraintLatest() {
    }

    @Override
    public boolean isSatisfiedBy(final Version version) {
      return true;
    }

    @Override
    public VersionConstraint merge(final VersionConstraint other) {
      if (other == null || other instanceof VersionConstraintLatest) {
        return this;
      }
      return other;
    }
  }

  private static class VersionConstraintMinimum extends VersionConstraint {

    private final Version min;
    private final boolean minInclusive;

    public VersionConstraintMinimum(final Version min, final boolean minInclusive) {
      this.min = min;
      this.minInclusive = minInclusive;
    }

    @Override
    public boolean isSatisfiedBy(final Version version) {
      return minInclusive ? version.isAtLeast(min) : version.isNewerThan(min);
    }

    @Override
    public VersionConstraint merge(final VersionConstraint other) {
      if (other instanceof VersionConstraintLatest) {
        return this;
      }
      if (other instanceof final VersionConstraintExact exactConstraint) {
        return isSatisfiedBy(exactConstraint.version) ? this : null;
      }
      if (!(other instanceof final VersionConstraintMinimum minimumConstraint)) {
        return other.merge(this);
      }
      return minimumConstraint.min.isNewerThan(this.min) ? minimumConstraint : this;
    }
  }

  private static class VersionConstraintRange extends VersionConstraint {

    private final VersionRange range;

    public VersionConstraintRange(final VersionRange range) {
      this.range = range;
    }

    @Override
    public boolean isSatisfiedBy(final Version version) {
      return range.isInRange(version);
    }

    @Override
    public VersionConstraint merge(final VersionConstraint other) {
      if (other instanceof VersionConstraintLatest) {
        return this;
      }
      if (other instanceof final VersionConstraintExact exactConstraint) {
        return range.isInRange(exactConstraint.version) ? this : null;
      }
      Version otherMin = null;
      boolean otherMinInclusive = true;
      Version otherMax = null;
      boolean otherMaxInclusive = false;
      if (other instanceof final VersionConstraintMinimum minConstraint) {
        otherMin = minConstraint.min;
        otherMinInclusive = minConstraint.minInclusive;
      } else if (other instanceof final VersionConstraintRange rangeConstraint) {
        otherMin = rangeConstraint.range.getMin();
        otherMinInclusive = rangeConstraint.range.isMinInclusive();
        otherMax = rangeConstraint.range.getMax();
        otherMaxInclusive = rangeConstraint.range.isMaxInclusive();
      } else {
        throw new IllegalArgumentException("Cannot merge VersionConstraint of type " + other.getClass().getName());
      }

      // Merge ranges - take highest min and lowest max
      Version newMin = null;
      boolean newMinInclusive = false;
      if (otherMin != null) {
        final int cmp = range.getMin().compareTo(otherMin);
        if (cmp > 0) {
          newMin = range.getMin();
          newMinInclusive = range.isMinInclusive();
        } else if (cmp < 0) {
          newMin = otherMin;
          newMinInclusive = otherMinInclusive;
        } else {
          newMin = range.getMin();
          newMinInclusive = range.isMinInclusive() && otherMinInclusive;
        }
      } else {
        newMin = range.getMin();
        newMinInclusive = range.isMinInclusive();
      }

      Version newMax = null;
      boolean newMaxInclusive = false;

      if (otherMax != null) {
        final int cmp = range.getMax().compareTo(otherMax);
        if (cmp < 0) {
          newMax = range.getMax();
          newMaxInclusive = range.isMaxInclusive();
        } else if (cmp > 0) {
          newMax = otherMax;
          newMaxInclusive = otherMaxInclusive;
        } else {
          newMax = range.getMax();
          newMaxInclusive = range.isMaxInclusive() && otherMaxInclusive;
        }
      } else {
        newMax = range.getMax();
        newMaxInclusive = range.isMaxInclusive();
      }

      // Check if the merged range is valid
      if (newMin != null && newMax != null) {
        final int cmp = newMin.compareTo(newMax);
        if (cmp > 0 || (cmp == 0 && !(newMinInclusive && newMaxInclusive))) {
          return null; // Invalid range
        }
      }

      return range(newMin, newMinInclusive, newMax, newMaxInclusive);
    }
  }
}
