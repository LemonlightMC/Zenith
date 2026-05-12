package com.lemonlightmc.zenith.messages;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import com.lemonlightmc.zenith.interfaces.Cloneable;

public interface IAudience<V> extends Iterable<V>, Comparable<IAudience<V>>, Cloneable<IAudience<V>> {

  public IAudience<V> addViewers(V viewer);

  @SuppressWarnings("unchecked")
  public IAudience<V> addViewers(V... viewer);

  public IAudience<V> addViewers(Collection<V> viewer);

  public IAudience<V> removeViewers(V viewer);

  @SuppressWarnings("unchecked")
  public IAudience<V> removeViewers(V... viewer);

  public IAudience<V> removeViewers(Collection<V> viewer);

  public boolean hasViewer(V viewer);

  public IAudience<V> clearViewers();

  public Set<V> viewers();

  public IAudience<V> withPermissions(String permission);

  public boolean hasPermissions(String permission);

  public IAudience<V> removePermissions(String permission);

  public IAudience<V> clearPermissions(String permission);

  public IAudience<V> withRequirements(Predicate<V> requirement);

  public boolean hasRequirements(Predicate<V> requirement);

  public IAudience<V> removeRequirements(Predicate<V> requirement);

  public IAudience<V> clearRequirements(Predicate<V> requirement);

  public IAudience<V> filter(Predicate<V> consumer);

  public IAudience<V> difference(IAudience<V> audience);

  public IAudience<V> intersection(IAudience<V> audience);

  public IAudience<V> union(IAudience<V> audience);

  public Set<V> toSet();

  public List<V> toList();

  public V[] toArray();

  @Override
  public IAudience<V> clone();

  @Override
  public String toString();

  @Override
  public int hashCode();

  @Override
  public boolean equals(Object audience);
}