package com.lemonlightmc.zenith.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.version.MCVersion;

public class Reflect {

  public static boolean hasClass(final String className) {
    if (className == null || className.isEmpty()) {
      return false;
    }
    try {
      Class.forName(className);
      return true;
    } catch (final ClassNotFoundException var2) {
      return false;
    }
  }

  public static Class<?> getMinecraftClass(final String className) {
    return getClass("net.minecraft." + className);
  }

  public static Class<?> getNMSClass(final String clazz) {
    return getClass("net.minecraft.server." + MCVersion.current() + clazz);
  }

  public static Class<?> getBukkitClass(final String className) {
    return getClass("org.bukkit.craftbukkit." + MCVersion.current() + className);
  }

  public static Object getConnection(final Player player) {
    if (player == null) {
      return null;
    }
    final Object entityPlayer = invokeMethod(player.getClass(), player, "getHandle", Player.class, player);
    return getObject(entityPlayer.getClass(), entityPlayer, "playerConnection");
  }

  public static void sendPacket(final Object packet, final Player player) {
    if (packet == null || player == null) {
      return;
    }

    final Class<?> packetClazz = getMinecraftClass("Packet");
    final Object conn = getConnection(player);
    invokeMethod(conn.getClass(), conn, "sendPacket", packetClazz, packet);
  }

  public static Class<?> getClass(final String className) {
    if (className == null || className.isEmpty()) {
      return null;
    }
    try {
      return Class.forName(className);
    } catch (final ClassNotFoundException e) {
      return null;
    }
  }

  public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Class<?>... args) {
    if (clazz == null) {
      return null;
    }
    try {
      final Constructor<T> ctor = args == null || args.length == 0 ? clazz.getDeclaredConstructor()
          : clazz.getDeclaredConstructor(args);
      ctor.setAccessible(true);
      return ctor;
    } catch (final Exception e) {
      return null;
    }
  }

  public static <T> Constructor<T> getConstructor(final Class<T> clazz, final Object... args) {
    if (clazz == null) {
      return null;
    }
    if (args != null && args.length > 0) {
      final Class<?>[] argClses = new Class[args.length];
      Arrays.setAll(argClses, i -> args[i].getClass());
      return getConstructor(clazz, argClses);
    }

    return getConstructor(clazz, (Class<?>) null);
  }

  public static <T> T newInstance(final Class<T> clazz, final Object... args) {
    if (clazz == null) {
      return null;
    }
    final Constructor<T> ctor = getConstructor(clazz, args);
    try {
      return ctor == null ? null : ctor.newInstance(args);
    } catch (final Exception e) {
      return null;
    }
  }

  public static <T> T newInstance(final Class<T> clazz, final Class<?>[] argTypes, final Object... args) {
    if (clazz == null) {
      return null;
    }
    final Constructor<T> ctor = getConstructor(clazz, argTypes);
    try {
      return ctor == null ? null : ctor.newInstance(args);
    } catch (final Exception e) {
      return null;
    }
  }

  public static Enum<?> getEnum(final Class<?> clazz, final String constant) {
    if (clazz == null || constant == null || constant.isEmpty()) {
      return null;
    }
    final Enum<?>[] constants = (Enum<?>[]) clazz.getEnumConstants();
    if (constants != null && constants.length != 0) {
      for (final Enum<?> e : constants) {
        if (e.name().equalsIgnoreCase(constant)) {
          return e;
        }
      }
    }
    return null;
  }

  public static Enum<?> getEnum(final Class<?> clazz, final String enumname, final String constant)
      throws ClassNotFoundException {
    if (clazz == null || enumname == null || enumname.isEmpty() || constant == null || constant.isEmpty()) {
      return null;
    }
    try {
      final Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
      return getEnum(c, constant);
    } catch (final Exception e) {
      return null;
    }
  }

  public static Field getField(final Class<?> clazz, final String fieldName) {
    if (clazz == null || fieldName == null || fieldName.isEmpty()) {
      return null;
    }
    try {
      final Field field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      return field;
    } catch (final Exception e) {
      return null;
    }
  }

  public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... args) {
    if (clazz == null || methodName == null || methodName.isEmpty()) {
      return null;
    }
    try {
      final Method m = clazz.getDeclaredMethod(methodName, args);
      m.setAccessible(true);
      return m;
    } catch (final Exception e) {
      return null;
    }
  }

  public static Object invokeMethod(final Object obj, final String methodName) {
    if (obj == null) {
      return null;
    }
    return invokeMethod(obj.getClass(), obj, methodName);
  }

  public static Object invokeMethod(final Object obj, final String methodName, final Object[] args) {
    if (obj == null) {
      return null;
    }
    return invokeMethod(obj.getClass(), obj, methodName, args);
  }

  public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] argTypes,
      final Object[] args) {
    if (obj == null) {
      return null;
    }
    return invokeMethod(obj.getClass(), obj, methodName, argTypes, args);
  }

  public static Object invokeMethod(final Class<?> clazz, final Object obj, final String methodName,
      final Object... args) {
    if (args == null || args.length == 0) {
      return invokeMethod(clazz, obj, methodName, (Class<?>[]) null, (Object[]) null);
    }

    final Class<?>[] argTypes = new Class<?>[args.length];
    for (int i = 0; i < args.length; i++) {
      argTypes[i] = args[i].getClass();
    }
    return invokeMethod(clazz, obj, methodName, argTypes, args);
  }

  public static Object invokeMethod(final Class<?> clazz, final Object obj, final String methodName,
      final Class<?>[] argTypes, final Object[] args) {
    final Method m = getMethod(clazz, methodName, argTypes);
    if (m == null) {
      return null;
    }
    try {
      return m.invoke(obj, args);
    } catch (final Exception e) {
      return null;
    }
  }

  public static Object getObject(final Object obj, final String name) {
    if (obj == null) {
      return null;
    }
    return getObject(obj.getClass(), obj, name);
  }

  public static Object getObject(final Class<?> clazz, final Object obj, final String name) {
    try {
      final Field f = getField(clazz, name);
      return f == null ? null : f.get(obj);
    } catch (final Exception e) {
      return null;
    }
  }

}
