package com.lemonlightmc.zenith.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.events.BaseEvent;
import com.lemonlightmc.zenith.events.EventsAPI;
import com.lemonlightmc.zenith.interfaces.Cloneable;

public class Registry<K, T extends Registrable<K>>
    implements Iterable<Map.Entry<K, T>>, Cloneable<Registry<K, T>>, Comparable<Registry<K, T>> {

  private final Map<K, T> registry;
  private boolean isLocked = false;
  private Object locker = null;

  public Registry() {
    registry = new HashMap<>();
  }

  public Registry(final Map<K, T> map) {
    registry = new HashMap<>(map);
  }

  public static <K, T extends Registrable<K>> Registry<K, T> of() {
    return new Registry<>();
  }

  public static <K, T extends Registrable<K>> Registry<K, T> of(final Class<T> map) {
    return new Registry<>();
  }

  public static <K, T extends Registrable<K>> Registry<K, T> of(final Map<K, T> map) {
    return new Registry<>(map);
  }

  public boolean isEmpty() {
    return registry.isEmpty();
  }

  public int size() {
    return registry.size();
  }

  public boolean isRegistered(final K key) {
    checkKey(key);
    return registry.containsKey(key);
  }

  public boolean isRegistered(final T element) {
    return registry.containsKey(checkKey(element));
  }

  public T register(final T element) {
    final K key = checkKey(element);
    if (this.isLocked) {
      throw new IllegalStateException(
          "Cannot add to locked registry! (ID: " + key + ")");
    }

    registry.put(key, element);
    element.onRegister();
    EventsAPI.call(new AddRegistryEvent<K, T>(element));
    return element;
  }

  public T remove(final T element) {
    final K key = checkKey(element);
    if (this.isLocked) {
      throw new IllegalStateException(
          "Cannot remove from locked registry! (ID: " + key + ")");
    }

    element.onRemove();
    EventsAPI.call(new RemoveRegistryEvent<K, T>(element));
    registry.remove(key);
    return element;
  }

  public T remove(final K key) {
    checkKey(key);
    if (this.isLocked) {
      throw new IllegalStateException(
          "Cannot remove from locked registry! (ID: " + key + ")");
    }

    final T element = registry.get(key);
    if (element != null) {
      return remove(element);
    }
    return element;
  }

  public T get(final K key) {
    checkKey(key);
    return registry.get(key);
  }

  public void clear() {
    if (this.isLocked) {
      throw new IllegalStateException(
          "Cannot clear a locked registry!");
    }
    EventsAPI.call(new ClearRegistryEvent());
    for (final Map.Entry<K, T> entry : registry.entrySet()) {
      entry.getValue().onRemove();
      registry.remove(entry.getKey());
    }
  }

  public Set<T> values() {
    return Set.copyOf(registry.values());
  }

  public boolean isLocked() {
    return isLocked;
  }

  public void lock(final Object locker) {
    if (locker == null) {
      throw new IllegalArgumentException("Locker cannot be null!");
    }
    if (this.isLocked && this.locker != locker) {
      throw new IllegalArgumentException(
          "Registry is already locked with a different locker!");
    }

    this.locker = locker;
    isLocked = true;
    EventsAPI.call(new LockRegistryEvent());
  }

  public void unlock(final Object locker) {
    if (locker == null) {
      throw new IllegalArgumentException("Locker cannot be null!");
    }
    if (this.locker != locker) {
      throw new IllegalArgumentException("Cannot unlock registry!");
    }

    this.locker = null;
    isLocked = false;
    EventsAPI.call(new UnlockRegistryEvent());
  }

  @Override
  public Registry<K, T> clone() {
    return new Registry<>(this.registry);
  }

  @Override
  public int compareTo(final Registry<K, T> o) {
    if (this == o) {
      return 0;
    }
    if (o == null) {
      return 1;
    }
    return Integer.compare(this.registry.size(), o.registry.size());
  }

  @Override
  public Iterator<Map.Entry<K, T>> iterator() {
    return Set.copyOf(registry.entrySet()).iterator();
  }

  public Stream<Map.Entry<K, T>> stream() {
    return registry.entrySet().stream();
  }

  @Override
  public int hashCode() {
    return 31 * (31 + registry.hashCode()) + (isLocked ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Registry<?, ?> other = (Registry<?, ?>) obj;
    return isLocked == other.isLocked && registry.equals(other.registry);
  }

  @Override
  public String toString() {
    return "Registry [registry=" + registry + ", isLocked=" + isLocked + "]";
  }

  private K checkKey(final T element) {
    if (element == null) {
      throw new IllegalArgumentException("Cannot register null element!");
    }
    final K key = element.getKey();
    if (key == null) {
      throw new IllegalArgumentException("Registry Key cannot be null!");
    }
    return key;
  }

  private void checkKey(final K key) {
    if (key == null) {
      throw new IllegalArgumentException("Registry Key cannot be null!");
    }
  }

  public static class AddRegistryEvent<K, T extends Registrable<K>> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T element;

    public AddRegistryEvent(final T element) {
      this.element = element;
    }

    public T getElement() {
      return element;
    }

    public K getKey() {
      return element.getKey();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class RemoveRegistryEvent<K, T extends Registrable<K>> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T element;

    public RemoveRegistryEvent(final T element) {
      this.element = element;
    }

    public T getElement() {
      return element;
    }

    public K getKey() {
      return element.getKey();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class ClearRegistryEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    public ClearRegistryEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class LockRegistryEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    public LockRegistryEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  public static class UnlockRegistryEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    public UnlockRegistryEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    public static HandlerList getHandlerList() {
      return handlers;
    }
  }
}
