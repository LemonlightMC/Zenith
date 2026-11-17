package com.lemonlightmc.zenith.messages;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;

/**
 * Defines a mutable audience of viewers that can be filtered, queried, and combined.
 *
 * @param <V> viewer type contained in the audience
 */
public interface IAudience<V> extends Iterable<V>, Comparable<IAudience<V>>, Cloneable<IAudience<V>> {

  /**
   * Adds a single viewer to this audience.
   *
   * @param viewer viewer to add
   * @return this audience for chaining
   */
  public IAudience<V> addViewers(V viewer);

  /**
   * Adds multiple viewers to this audience.
   *
   * @param viewer viewers to add
   * @return this audience for chaining
   */
  @SuppressWarnings("unchecked")
  public IAudience<V> addViewers(V... viewer);

  /**
   * Adds a collection of viewers to this audience.
   *
   * @param viewer viewers to add
   * @return this audience for chaining
   */
  public IAudience<V> addViewers(Collection<V> viewer);

  /**
   * Removes a single viewer from this audience.
   *
   * @param viewer viewer to remove
   * @return this audience for chaining
   */
  public IAudience<V> removeViewers(V viewer);

  /**
   * Removes multiple viewers from this audience.
   *
   * @param viewer viewers to remove
   * @return this audience for chaining
   */
  @SuppressWarnings("unchecked")
  public IAudience<V> removeViewers(V... viewer);

  /**
   * Removes a collection of viewers from this audience.
   *
   * @param viewer viewers to remove
   * @return this audience for chaining
   */
  public IAudience<V> removeViewers(Collection<V> viewer);

  /**
   * Checks whether this audience contains the given viewer.
   *
   * @param viewer viewer to check
   * @return {@code true} if the viewer is present; otherwise {@code false}
   */
  public boolean hasViewer(V viewer);

  /**
   * Removes all viewers from this audience.
   *
   * @return this audience for chaining
   */
  public IAudience<V> clearViewers();

  /**
   * Gets the current viewer set backing this audience.
   *
   * @return the audience viewers
   */
  public Set<V> viewers();

  /**
   * Adds a permission requirement for this audience.
   *
   * @param permission permission node to require
   * @return this audience for chaining
   */
  public IAudience<V> withPermissions(String permission);

  /**
   * Checks whether this audience tracks the given permission requirement.
   *
   * @param permission permission node to check
   * @return {@code true} if the permission requirement is present; otherwise {@code false}
   */
  public boolean hasPermissions(String permission);

  /**
   * Removes a permission requirement from this audience.
   *
   * @param permission permission node to remove
   * @return this audience for chaining
   */
  public IAudience<V> removePermissions(String permission);

  /**
   * Clears all permission requirements from this audience.
   *
   * @param permission ignored parameter kept for API compatibility
   * @return this audience for chaining
   */
  public IAudience<V> clearPermissions(String permission);

  /**
   * Adds a predicate requirement for this audience.
   *
   * @param requirement predicate that viewers must satisfy
   * @return this audience for chaining
   */
  public IAudience<V> withRequirements(Predicate<V> requirement);

  /**
   * Checks whether this audience tracks the given requirement.
   *
   * @param requirement requirement to check
   * @return {@code true} if the requirement is present; otherwise {@code false}
   */
  public boolean hasRequirements(Predicate<V> requirement);

  /**
   * Removes a predicate requirement from this audience.
   *
   * @param requirement requirement to remove
   * @return this audience for chaining
   */
  public IAudience<V> removeRequirements(Predicate<V> requirement);

  /**
   * Clears all predicate requirements from this audience.
   *
   * @param requirement ignored parameter kept for API compatibility
   * @return this audience for chaining
   */
  public IAudience<V> clearRequirements(Predicate<V> requirement);

  /**
   * Removes viewers matching the given predicate.
   *
   * @param consumer predicate identifying viewers to remove
   * @return this audience for chaining
   */
  public IAudience<V> filter(Predicate<V> consumer);

  /**
   * Replaces this audience with the viewers not present in the given audience.
   *
   * @param audience audience to compare against
   * @return this audience for chaining
   */
  public IAudience<V> difference(IAudience<V> audience);

  /**
   * Replaces this audience with only the viewers shared with the given audience.
   *
   * @param audience audience to intersect with
   * @return this audience for chaining
   */
  public IAudience<V> intersection(IAudience<V> audience);

  /**
   * Adds all viewers from the given audience to this audience.
   *
   * @param audience audience to merge in
   * @return this audience for chaining
   */
  public IAudience<V> union(IAudience<V> audience);

  /**
   * Returns the viewers as a set.
   *
   * @return a set of viewers
   */
  public Set<V> toSet();

  /**
   * Returns the viewers as a list.
   *
   * @return a list of viewers
   */
  public List<V> toList();

  /**
   * Returns the viewers as an array.
   *
   * @return an array of viewers
   */
  public V[] toArray();

  /**
   * Creates a copy of this audience.
   *
   * @return a cloned audience
   */
  @Override
  public IAudience<V> clone();
}