package com.lemonlightmc.zenith.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;
import com.lemonlightmc.zenith.events.BaseEvent;
import com.lemonlightmc.zenith.events.EventsAPI;

/**
 * A keyed registry for {@link Registrable} elements with optional locking and lifecycle events.
 *
 * @param <K> the key type
 * @param <T> the registrable element type
 */
public class Registry<K, T extends Registrable<K>>
    implements Iterable<Map.Entry<K, T>>, Cloneable<Registry<K, T>>, Comparable<Registry<K, T>> {

  private final Map<K, T> registry;
  private boolean isLocked = false;
  private Object locker = null;

  /**
   * Creates an empty registry.
   */
  public Registry() {
    registry = new HashMap<>();
  }

  /**
   * Creates a registry initialized with the entries from the given map.
   *
   * @param map the initial entries
   */
  public Registry(final Map<K, T> map) {
    registry = new HashMap<>(map);
  }

  /**
   * Creates a new empty registry.
   *
   * @param <K> the key type
   * @param <T> the registrable element type
   * @return a new empty registry
   */
  public static <K, T extends Registrable<K>> Registry<K, T> of() {
    return new Registry<>();
  }

  /**
   * Creates a new empty registry for the specified element type.
   *
   * @param map the element type marker
   * @param <K> the key type
   * @param <T> the registrable element type
   * @return a new empty registry
   */
  public static <K, T extends Registrable<K>> Registry<K, T> of(final Class<T> map) {
    return new Registry<>();
  }

  /**
   * Creates a new registry initialized with the entries from the given map.
   *
   * @param map the initial entries
   * @param <K> the key type
   * @param <T> the registrable element type
   * @return a new registry containing the provided entries
   */
  public static <K, T extends Registrable<K>> Registry<K, T> of(final Map<K, T> map) {
    return new Registry<>(map);
  }

  /**
   * Returns whether this registry contains no elements.
   *
   * @return {@code true} if the registry is empty; {@code false} otherwise
   */
  public boolean isEmpty() {
    return registry.isEmpty();
  }

  /**
   * Returns the number of registered elements.
   *
   * @return the registry size
   */
  public int size() {
    return registry.size();
  }

  /**
   * Returns whether an element with the given key is registered.
   *
   * @param key the key to check
   * @return {@code true} if the key is registered; {@code false} otherwise
   */
  public boolean isRegistered(final K key) {
    checkKey(key);
    return registry.containsKey(key);
  }

  /**
   * Returns whether the given element is registered.
   *
   * @param element the element to check
   * @return {@code true} if the element's key is registered; {@code false} otherwise
   */
  public boolean isRegistered(final T element) {
    return registry.containsKey(checkKey(element));
  }

  /**
   * Registers an element in this registry.
   *
   * @param element the element to register
   * @return the registered element
   * @throws IllegalStateException if the registry is locked
   * @throws IllegalArgumentException if the element or its key is {@code null}
   */
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

  /**
   * Removes an element from this registry.
   *
   * @param element the element to remove
   * @return the removed element
   * @throws IllegalStateException if the registry is locked
   * @throws IllegalArgumentException if the element or its key is {@code null}
   */
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

  /**
   * Removes the element associated with the given key from this registry.
   *
   * @param key the key of the element to remove
   * @return the removed element, or {@code null} if no element was registered for the key
   * @throws IllegalStateException if the registry is locked
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
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

  /**
   * Returns the element registered for the given key.
   *
   * @param key the key to look up
   * @return the registered element, or {@code null} if none is found
   * @throws IllegalArgumentException if {@code key} is {@code null}
   */
  public T get(final K key) {
    checkKey(key);
    return registry.get(key);
  }

  /**
   * Removes all elements from this registry.
   *
   * @throws IllegalStateException if the registry is locked
   */
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

  /**
   * Returns an unmodifiable snapshot of the registered values.
   *
   * @return a set containing the registered values
   */
  public Set<T> values() {
    return Set.copyOf(registry.values());
  }

  /**
   * Returns whether this registry is currently locked.
   *
   * @return {@code true} if the registry is locked; {@code false} otherwise
   */
  public boolean isLocked() {
    return isLocked;
  }

  /**
   * Locks this registry to prevent modifications.
   *
   * @param locker the object used to identify the lock owner
   * @throws IllegalArgumentException if {@code locker} is {@code null} or the registry is locked by a different owner
   */
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

  /**
   * Unlocks this registry if the provided locker matches the current lock owner.
   *
   * @param locker the object used to identify the lock owner
   * @throws IllegalArgumentException if {@code locker} is {@code null} or does not match the current lock owner
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public Registry<K, T> clone() {
    return new Registry<>(this.registry);
  }

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<Map.Entry<K, T>> iterator() {
    return Set.copyOf(registry.entrySet()).iterator();
  }

  /**
   * Returns a stream over the registry entries.
   *
   * @return a stream of registry entries
   */
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
    return isLocked != other.isLocked && registry.equals(other.registry);
  }

  @Override
  public String toString() {
    return "Registry [registry=" + registry + ", isLocked=" + isLocked + ", locker=" + locker + "]";
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

  /**
   * Event fired when an element is added to a registry.
   *
   * @param <K> the key type
   * @param <T> the registrable element type
   */
  public static class AddRegistryEvent<K, T extends Registrable<K>> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T element;

    /**
     * Creates a new add-registry event.
     *
     * @param element the added element
     */
    public AddRegistryEvent(final T element) {
      this.element = element;
    }

    /**
     * Returns the added element.
     *
     * @return the added element
     */
    public T getElement() {
      return element;
    }

    /**
     * Returns the key of the added element.
     *
     * @return the element key
     */
    public K getKey() {
      return element.getKey();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when an element is removed from a registry.
   *
   * @param <K> the key type
   * @param <T> the registrable element type
   */
  public static class RemoveRegistryEvent<K, T extends Registrable<K>> extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();
    private final T element;

    /**
     * Creates a new remove-registry event.
     *
     * @param element the removed element
     */
    public RemoveRegistryEvent(final T element) {
      this.element = element;
    }

    /**
     * Returns the removed element.
     *
     * @return the removed element
     */
    public T getElement() {
      return element;
    }

    /**
     * Returns the key of the removed element.
     *
     * @return the element key
     */
    public K getKey() {
      return element.getKey();
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a registry is cleared.
   */
  public static class ClearRegistryEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Creates a new clear-registry event.
     */
    public ClearRegistryEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a registry is locked.
   */
  public static class LockRegistryEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Creates a new lock-registry event.
     */
    public LockRegistryEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }

  /**
   * Event fired when a registry is unlocked.
   */
  public static class UnlockRegistryEvent extends BaseEvent {
    private static final HandlerList handlers = new HandlerList();

    /**
     * Creates a new unlock-registry event.
     */
    public UnlockRegistryEvent() {
    }

    @Override
    public HandlerList getHandlers() {
      return handlers;
    }

    /**
     * Returns the handler list for this event type.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
      return handlers;
    }
  }
}
