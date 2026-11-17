package com.lemonlightmc.zenith.json;

import com.google.gson.JsonElement;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents an object that can be serialized to and deserialized from JSON.
 *
 * <p>Implementations should provide a {@code public static} {@code deserialize(JsonElement)} method so helper methods
 * on this interface can reflectively construct instances.
 */
public interface GsonSerializable {

  /**
   * Deserializes a JSON element into a {@link GsonSerializable} instance.
   *
   * @param <T> the serializable type
   * @param clazz the class to deserialize into
   * @param element the JSON element to deserialize
   * @return the deserialized instance
   * @throws IllegalStateException if the class does not expose an accessible deserialization method
   */
  @SuppressWarnings("unchecked")
  @Nonnull
  static <T extends GsonSerializable> T deserialize(@Nonnull Class<T> clazz, @Nonnull JsonElement element) {
    Method deserializeMethod = getDeserializeMethod(clazz);
    if (deserializeMethod == null) {
      throw new IllegalStateException("Class does not have a deserialize method accessible.");
    }

    try {
      return (T) deserializeMethod.invoke(null, element);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Deserializes a JSON element into a raw {@link GsonSerializable} instance.
   *
   * @param clazz the class to deserialize into
   * @param element the JSON element to deserialize
   * @return the deserialized instance
   * @throws IllegalStateException if the class does not expose an accessible deserialization method
   */
  @Nonnull
  static GsonSerializable deserializeRaw(@Nonnull Class<?> clazz, @Nonnull JsonElement element) {
    Class<? extends GsonSerializable> typeCastedClass = clazz.asSubclass(GsonSerializable.class);
    return deserialize(typeCastedClass, element);
  }

  /**
   * Returns the static deserialization method declared by a serializable class, if available.
   *
   * @param clazz the class to inspect
   * @return the deserialization method, or {@code null} if none is available
   */
  @Nullable
  static Method getDeserializeMethod(@Nonnull Class<?> clazz) {
    if (!GsonSerializable.class.isAssignableFrom(clazz)) {
      return null;
    }

    Method deserializeMethod;
    try {
      // noinspection JavaReflectionMemberAccess
      deserializeMethod = clazz.getDeclaredMethod("deserialize", JsonElement.class);
      deserializeMethod.setAccessible(true);
    } catch (Exception e) {
      return null;
    }

    if (!Modifier.isStatic(deserializeMethod.getModifiers())) {
      return null;
    }

    return deserializeMethod;
  }

  /**
   * Serializes this object into a JSON element.
   *
   * @return the serialized JSON representation
   */
  @Nonnull
  JsonElement serialize();

}