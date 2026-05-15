package com.lemonlightmc.zenith.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.NamespacedKey;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.events.BaseEvent;
import com.lemonlightmc.zenith.events.EventsAPI;
import com.lemonlightmc.zenith.interfaces.Cloneable;

public class Registry<T extends Registrable>
    implements Iterable<Map.Entry<NamespacedKey, T>>, Cloneable<Registry<T>>, Comparable<Registry<T>> {

  private final Map<NamespacedKey, T> registry;
  private boolean isLocked = false;
  private Object locker = null;

  public Registry() {
    registry = new HashMap<>();
  }

  public Registry(final Map<NamespacedKey, T> map) {
    registry = new HashMap<>(map);
  }

  public static <T extends Registrable> Registry<T> of() {
    return new Registry<>();
  }

  public static <T extends Registrable> Registry<T> of(final Class<T> map) {
    return new Registry<>();
  }

  public static <T extends Registrable> Registry<T> of(final Map<NamespacedKey, T> map) {
    return new Registry<>(map);
  }

  public T register(final T element) {
    final NamespacedKey key = checkKey(element);
    if (this.isLocked) {
      throw new IllegalStateException(
          "Cannot add to locked registry! (ID: " + key + ")");
    }

    registry.put(key, element);
    element.onRegister();
    EventsAPI.call(new AddRegistryEvent<T>(element));
    return element;
  }

  public T remove(final T element) {
    final NamespacedKey key = checkKey(element);
    if (this.isLocked) {
      throw new IllegalStateException(
          "Cannot remove from locked registry! (ID: " + key + ")");
    }

    element.onRemove();
    EventsAPI.call(new RemoveRegistryEvent<T>(element));
    registry.remove(key);
    return element;
  }

  public T remove(final NamespacedKey key) {
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

  public T get(final NamespacedKey key) {
    checkKey(key);
    return registry.get(key);
  }

  public void clear() {
    if (this.isLocked) {
      throw new IllegalStateException(
          "Cannot clear a locked registry!");
    }
    EventsAPI.call(new ClearRegistryEvent());
    for (final Map.Entry<NamespacedKey, T> entry : registry.entrySet()) {
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

  public boolean isEmpty() {
    return registry.isEmpty();
  }

  public boolean isNotEmpty() {
    return !registry.isEmpty();
  }

  @Override
  public Registry<T> clone() {
    return new Registry<>(this.registry);
  }

  @Override
  public int compareTo(final Registry<T> o) {
    if (this == o) {
      return 0;
    }
    if (o == null) {
      return 1;
    }
    return Integer.compare(this.registry.size(), o.registry.size());
  }

  @Override
  public Iterator<Map.Entry<NamespacedKey, T>> iterator() {
    return Set.copyOf(registry.entrySet()).iterator();
  }

  public Stream<Map.Entry<NamespacedKey, T>> stream() {
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
    final Registry<?> other = (Registry<?>) obj;
    return isLocked != other.isLocked && registry.equals(other.registry);
  }

  @Override
  public String toString() {
    return "Registry [registry=" + registry + ", isLocked=" + isLocked + ", locker=" + locker + "]";
  }

  private NamespacedKey checkKey(final T element) {
    if (element == null) {
      throw new IllegalArgumentException("Cannot register null element!");
    }
    final NamespacedKey key = element.getKey();
    if (key == null) {
      throw new IllegalArgumentException("Registry Key cannot be null!");
    }
    return key;
  }

  private static void checkKey(final NamespacedKey key) {
    if (key == null) {
      throw new IllegalArgumentException("Registry Key cannot be null!");
    }
  }

  public static class AddRegistryEvent<T extends Registrable> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T element;

    public AddRegistryEvent(final T element) {
      this.element = element;
    }

    public T getElement() {
      return element;
    }

    public NamespacedKey getKey() {
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

  public static class RemoveRegistryEvent<T extends Registrable> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T element;

    public RemoveRegistryEvent(final T element) {
      this.element = element;
    }

    public T getElement() {
      return element;
    }

    public NamespacedKey getKey() {
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
