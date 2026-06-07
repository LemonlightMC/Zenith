package com.lemonlightmc.zenith.version;

import java.util.List;
import java.util.stream.Collectors;

import com.lemonlightmc.zenith.version.VersionConstraintsHolder.CaretConstraint;
import com.lemonlightmc.zenith.version.VersionConstraintsHolder.CompositeConstraint;
import com.lemonlightmc.zenith.version.VersionConstraintsHolder.ExactVersionConstraint;
import com.lemonlightmc.zenith.version.VersionConstraintsHolder.OrConstraint;
import com.lemonlightmc.zenith.version.VersionConstraintsHolder.RangeConstraint;
import com.lemonlightmc.zenith.version.VersionConstraintsHolder.TildeConstraint;
import com.lemonlightmc.zenith.version.VersionConstraintsHolder.VersionConstraintParser;

/**
 * Represents a version constraint that can evaluate whether specific versions
 * satisfy
 * its defined rules and conditions.
 * <p>
 * This interface allows diverse implementations for different types
 * of version constraints, such as ranges, exact matches, or semantic versioning
 * rules.
 * <p>
 * The constraints may define minimum and/or maximum boundary conditions,
 * equality rules,
 * or complex logical combinations of multiple constraints.
 */
public interface VersionConstraint2 {
  /**
   * Determines if the given version satisfies the constraints defined by this
   * implementation.
   *
   * @param version the version to be checked against the constraints
   * @return true if the version satisfies the constraints, false otherwise
   */
  boolean satisfies(Version version);

  /**
   * Retrieves the original constraint string as it was input or defined.
   *
   * @return the original version constraint string that was used to create this
   *         constraint.
   */
  String original();

  /**
   * Retrieves a list of version constraints represented as strings.
   * <p>
   * This method aggregates all conditions that define valid versions and returns
   * them
   * in a standardized string format for further interpretation or display.
   *
   * @return a list of strings where each string specifies a version constraint.
   */
  List<String> getVersions();

  /**
   * Retrieves the lowest version that satisfies the version constraint.
   * <p>
   * The result depends on how the specific implementation computes the lowest
   * version.
   * It may return null if the constraint defines no minimum version.
   * <p>
   *
   * @return the lowest version as a string
   */
  Version getLowVersion();

  /**
   * Retrieves the highest version defined by the version constraint.
   * <p>
   * Depending on the implementing class, this may involve calculating the
   * maximum version based on one or more underlying version constraints.
   *
   * @return the maximum version as a string
   */
  Version getMaxVersion();

  public static ExactVersionConstraint exact(final Version version) {
    if (version == null) {
      throw new IllegalArgumentException("VersionConstraint Exact Version cannot be null");
    }
    return new ExactVersionConstraint(version);
  }

  public static TildeConstraint tilde(final Version version) {
    if (version == null) {
      throw new IllegalArgumentException("VersionConstraint Exact Version cannot be null");
    }
    return new TildeConstraint(version, version.toString());
  }

  public static CaretConstraint caret(final Version version) {
    if (version == null) {
      throw new IllegalArgumentException("VersionConstraint Exact Version cannot be null");
    }
    return new CaretConstraint(version, version.toString());
  }

  public static RangeConstraint range(final VersionRange range) {
    if (range == null) {
      throw new IllegalArgumentException("VersionConstraint Version Range cannot be null");
    }
    return new RangeConstraint(range);
  }

  public static RangeConstraint range(final Version min, final Version max) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Min Version cannot be null");
    }
    if (max == null) {
      throw new IllegalArgumentException("VersionConstraint Max Version cannot be null");
    }
    return new RangeConstraint(min, max, true, true, min + "-" + max);
  }

  public static RangeConstraint range(final Version min, final boolean minInclusive, final Version max,
      final boolean maxInclusive) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Min Version cannot be null");
    }
    if (max == null) {
      throw new IllegalArgumentException("VersionConstraint Max Version cannot be null");
    }
    return new RangeConstraint(min, max, minInclusive, maxInclusive,
        (minInclusive ? ">=" : ">") + min + " " + (maxInclusive ? "<=" : "<") + max);
  }

  public static RangeConstraint atleast(final Version min) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Version Range cannot be null");
    }
    return new RangeConstraint(min, null, true, false, ">=" + min);
  }

  public static RangeConstraint atleast(final Version min, final boolean inclusive) {
    if (min == null) {
      throw new IllegalArgumentException("VersionConstraint Version Range cannot be null");
    }
    return new RangeConstraint(min, null, inclusive, false, (inclusive ? ">=" : ">") + min);
  }

  public static OrConstraint or(final List<VersionConstraint2> constraints) {
    if (constraints == null || constraints.isEmpty()) {
      throw new IllegalArgumentException("VersionConstraint Or Constraints cannot be null or empty");
    }
    return new OrConstraint(constraints, constraints.stream()
        .map(VersionConstraint2::original)
        .collect(Collectors.joining(" || ")));
  }

  public static CompositeConstraint composite(final List<VersionConstraint2> constraints) {
    if (constraints == null || constraints.isEmpty()) {
      throw new IllegalArgumentException("VersionConstraint And Constraints cannot be null or empty");
    }
    return new CompositeConstraint(constraints, constraints.stream()
        .map(VersionConstraint2::original)
        .collect(Collectors.joining(" ")));
  }

  public static VersionConstraint2 parse(final String constraintSt) {
    return VersionConstraintParser.parse(constraintSt);
  }
}