package com.lemonlightmc.zenith.dependency.coordination;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.lemonlightmc.zenith.dependency.Dependency;
import com.lemonlightmc.zenith.dependency.UpdatePolicy;
import com.lemonlightmc.zenith.version.Version;
import com.lemonlightmc.zenith.version.VersionConstraint;

public class DependencyRegistration {
  final String name;
  final Set<String> requestedBy = new LinkedHashSet<>();
  final List<PluginConstraint> constraints = new ArrayList<>();

  DependencyRegistration(String name) {
    this.name = name;
  }

  public void addConstraint(String pluginName, Dependency dep) {
    requestedBy.add(pluginName);

    // Remove old constraint from this plugin if exists
    constraints.removeIf(c -> c.plugin().equals(pluginName));

    PluginConstraint pc = new PluginConstraint(pluginName, dep.constraint(), dep.updatePolicy());
    constraints.add(pc);
  }

  public VersionConstraint getMergedConstraint() {
    if (constraints.isEmpty()) {
      return null;
    }

    // Start with the first constraint
    VersionConstraint merged = constraints.get(0).constraint();
    if (merged == null) {
      merged = VersionConstraint.latest();
    }

    // Merge with remaining constraints
    for (int i = 1; i < constraints.size(); i++) {
      VersionConstraint other = constraints.get(i).constraint();
      if (other == null) {
        continue;
      }

      VersionConstraint newMerged = merged.merge(other);
      if (newMerged != null) {
        merged = newMerged;
        continue;
      }

      // Incompatible constraints - use higher minimum
      // This is the "warn and use higher" strategy
      Version thisMin = getMinimumVersion(merged);
      Version otherMin = getMinimumVersion(other);

      if (thisMin != null && otherMin != null) {
        merged = thisMin.compareTo(otherMin) > 0
            ? VersionConstraint.atleast(thisMin)
            : VersionConstraint.atleast(otherMin);
      } else if (otherMin != null) {
        merged = VersionConstraint.atleast(otherMin);
      }
      // If thisMin != null but otherMin is null, keep merged
    }
    return merged;
  }

  private static Version getMinimumVersion(final VersionConstraint constraint) {
    if (constraint == null || constraint.isLatest()) {
      return null;
    }
    if (constraint.isExact()) {
      return constraint.asExact().version();
    }
    if (constraint.isMinimum()) {
      return constraint.asMinimum().min();
    }
    if (constraint.isRange()) {
      return constraint.asRange().range().getMin();
    }
    throw new IllegalArgumentException("Unknown VersionConstraint type: " + constraint.getClass().getName());
  }

  public static record PluginConstraint(String plugin,
      VersionConstraint constraint,
      UpdatePolicy updatePolicy) {
  }
}
