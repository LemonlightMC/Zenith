package com.lemonlightmc.zenith.additive;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Reflection helpers for accessing classes, constructors, methods, fields, and caller metadata.
 */
public class Reflect {
  /** Cached {@link VarHandle} for accessing field modifiers when available. */
  public static VarHandle modifiersVarHandle;
  /** Cached reflective {@link Field} for the {@code modifiers} field when available. */
  public static Field modifiersField;
  /** Cached reflective access to {@code Class#getDeclaredFields0(boolean)} when available. */
  public static Method getDeclaredFields0;

  /**
   * Returns a {@link VarHandle} for the {@code modifiers} field of {@link Field} when supported by the runtime.
   *
   * @return the modifiers {@link VarHandle}, or {@code null} if unavailable
   */
  public static VarHandle modifiersVarHandle() {
    if (modifiersVarHandle != null) {
      return modifiersVarHandle;
    }
    try {
      VarHandle.class.getName(); // Makes this method fail-fast on JDK 8
      final Object temp = MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup())
          .findVarHandle(Field.class, "modifiers", int.class);
      modifiersVarHandle = temp == null ? null : (VarHandle) temp;
    } catch (IllegalAccessException | NoClassDefFoundError | NoSuchFieldException e) {
    }
    return modifiersVarHandle;
  }

  /**
   * Returns the reflective {@link Field} representing {@link Field#modifiers} when accessible.
   *
   * @return the modifiers field, or {@code null} if unavailable
   */
  public static Field modifiersField() {
    if (modifiersField != null) {
      return modifiersField;
    }
    try {
      modifiersField = Field.class.getDeclaredField("modifiers");
      modifiersField.setAccessible(true);
    } catch (NoClassDefFoundError | NoSuchFieldException ignored) {
    }
    return modifiersField;
  }

  /**
   * Returns the declared fields of the given class using reflective access to internal APIs when available.
   *
   * @param cls class whose declared fields should be retrieved
   * @return the declared fields, or {@code null} if they cannot be retrieved
   */
  public static Field[] declaredFields(final Class<?> cls) {
    if (getDeclaredFields0 == null) {
      try {
        getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0",
            boolean.class);
        getDeclaredFields0.setAccessible(true);
      } catch (NoClassDefFoundError | NoSuchMethodException | SecurityException e) {
        return null;
      }
    }
    try {
      return (Field[]) getDeclaredFields0.invoke(cls, false);
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Checks whether a class with the given name can be loaded.
   *
   * @param className fully qualified class name to check
   * @return {@code true} if the class exists and can be loaded; otherwise {@code false}
   */
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

  /**
   * Loads and returns the class with the given name.
   *
   * @param className fully qualified class name to load
   * @return the loaded {@link Class}, or {@code null} if it cannot be found
   */
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

  /**
   * Finds the first class in the given JAR file that is assignable to the specified type.
   *
   * @param file jar file to scan
   * @param clazz target type to search for
   * @param <T> target type parameter
   * @return the first matching class, or {@code null} if none is found
   */
  public static <T> Class<? extends T> findClass(final File file, final Class<T> clazz) {
    if (!file.exists()) {
      return null;
    }
    try {
      final URL jar = file.toURI().toURL();
      final URLClassLoader loader = new URLClassLoader(new URL[] { jar }, clazz.getClassLoader());
      final List<String> matches = new ArrayList<>();
      final List<Class<? extends T>> classes = new ArrayList<>();

      try (final JarInputStream stream = new JarInputStream(jar.openStream())) {
        JarEntry entry;
        while ((entry = stream.getNextJarEntry()) != null) {
          final String name = entry.getName();
          if (name.isEmpty() || !name.endsWith(".class")) {
            continue;
          }

          matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
        }

        for (final String match : matches) {
          try {
            final Class<?> loaded = loader.loadClass(match);
            if (clazz.isAssignableFrom(loaded)) {
              classes.add(loaded.asSubclass(clazz));
            }
          } catch (final NoClassDefFoundError ignored) {
          }
        }
      }
      loader.close();
      return classes.isEmpty() ? null : classes.get(0);
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Returns an accessible constructor for the given class and parameter types.
   *
   * @param clazz class whose constructor should be retrieved
   * @param args constructor parameter types
   * @param <T> class type parameter
   * @return the accessible constructor, or {@code null} if it cannot be found
   */
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

  /**
   * Returns an accessible constructor for the given class matching the runtime types of the provided arguments.
   *
   * @param clazz class whose constructor should be retrieved
   * @param args constructor arguments whose runtime types are used for lookup
   * @param <T> class type parameter
   * @return the accessible constructor, or {@code null} if it cannot be found
   */
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

  /**
   * Creates a new instance of the given class using constructor arguments matched by their runtime types.
   *
   * @param clazz class to instantiate
   * @param args constructor arguments
   * @param <T> class type parameter
   * @return the new instance, or {@code null} if instantiation fails
   */
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

  /**
   * Creates a new instance of the given class using the specified constructor parameter types.
   *
   * @param clazz class to instantiate
   * @param argTypes constructor parameter types
   * @param args constructor arguments
   * @param <T> class type parameter
   * @return the new instance, or {@code null} if instantiation fails
   */
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

  /**
   * Returns the enum constant with the given name from the specified enum class, ignoring case.
   *
   * @param clazz enum class to search
   * @param constant enum constant name
   * @return the matching enum constant, or {@code null} if not found
   */
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

  /**
   * Returns the enum constant with the given name from a nested enum type of the specified class.
   *
   * @param clazz outer class containing the nested enum type
   * @param enumname nested enum type name
   * @param constant enum constant name
   * @return the matching enum constant, or {@code null} if not found
   * @throws ClassNotFoundException if the nested enum type cannot be resolved
   */
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

  /**
   * Returns an accessible declared field with the given name from the specified class.
   *
   * @param clazz class declaring the field
   * @param fieldName name of the field to retrieve
   * @return the accessible field, or {@code null} if it cannot be found
   */
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

  /**
   * Returns an accessible declared method with the given name and parameter types from the specified class.
   *
   * @param clazz class declaring the method
   * @param methodName name of the method to retrieve
   * @param args method parameter types
   * @return the accessible method, or {@code null} if it cannot be found
   */
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

  /**
   * Invokes a no-argument method on the given object.
   *
   * @param obj object on which to invoke the method
   * @param methodName name of the method to invoke
   * @return the invocation result, or {@code null} if invocation fails
   */
  public static Object invokeMethod(final Object obj, final String methodName) {
    if (obj == null) {
      return null;
    }
    return invokeMethod(obj.getClass(), obj, methodName, (Class<?>[]) null, (Object[]) null);
  }

  /**
   * Invokes a method on the given object using argument runtime types for lookup.
   *
   * @param obj object on which to invoke the method
   * @param methodName name of the method to invoke
   * @param args method arguments
   * @return the invocation result, or {@code null} if invocation fails
   */
  public static Object invokeMethod(final Object obj, final String methodName, final Object[] args) {
    if (obj == null) {
      return null;
    }
    return invokeMethod(obj.getClass(), obj, methodName, args);
  }

  /**
   * Invokes a method on the given object using explicit parameter types.
   *
   * @param obj object on which to invoke the method
   * @param methodName name of the method to invoke
   * @param argTypes method parameter types
   * @param args method arguments
   * @return the invocation result, or {@code null} if invocation fails
   */
  public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] argTypes,
      final Object[] args) {
    if (obj == null) {
      return null;
    }
    return invokeMethod(obj.getClass(), obj, methodName, argTypes, args);
  }

  /**
   * Invokes a method declared by the given class using argument runtime types for lookup.
   *
   * @param clazz class declaring the method
   * @param obj object on which to invoke the method
   * @param methodName name of the method to invoke
   * @param args method arguments
   * @return the invocation result, or {@code null} if invocation fails
   */
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

  /**
   * Invokes a method declared by the given class using explicit parameter types.
   *
   * @param clazz class declaring the method
   * @param obj object on which to invoke the method
   * @param methodName name of the method to invoke
   * @param argTypes method parameter types
   * @param args method arguments
   * @return the invocation result, or {@code null} if invocation fails
   */
  public static Object invokeMethod(final Class<?> clazz, final Object obj, final String methodName,
      final Class<?>[] argTypes, final Object[] args) {
    if (clazz == null || methodName == null || methodName.isEmpty()) {
      return null;
    }
    try {
      final Method m = clazz.getDeclaredMethod(methodName, argTypes);
      if (m == null) {
        return null;
      }
      m.setAccessible(true);
      return m.invoke(obj, args);
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Invokes the given method on the provided object with no arguments.
   *
   * @param method method to invoke
   * @param obj object on which to invoke the method
   * @return the invocation result, or {@code null} if invocation fails
   */
  public static Object invokeMethod(final Method method, final Object obj) {
    if (method == null) {
      return null;
    }
    try {
      return method.invoke(obj, (Object[]) null);
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Invokes the given method on the provided object with the specified arguments.
   *
   * @param method method to invoke
   * @param obj object on which to invoke the method
   * @param args method arguments
   * @return the invocation result, or {@code null} if invocation fails
   */
  public static Object invokeMethod(final Method method, final Object obj, final Object[] args) {
    if (method == null) {
      return null;
    }
    try {
      return method.invoke(obj, args);
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Gets the value of a field from the given object.
   *
   * @param obj object containing the field
   * @param name name of the field to read
   * @return the field value, or {@code null} if it cannot be read
   */
  public static Object getObject(final Object obj, final String name) {
    if (obj == null) {
      return null;
    }
    return getObject(obj.getClass(), obj, name);
  }

  /**
   * Gets the value of a field declared by the given class from the provided object.
   *
   * @param clazz class declaring the field
   * @param obj object containing the field
   * @param name name of the field to read
   * @return the field value, or {@code null} if it cannot be read
   */
  public static Object getObject(final Class<?> clazz, final Object obj, final String name) {
    try {
      final Field f = getField(clazz, name);
      return f == null ? null : f.get(obj);
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Makes the given field accessible.
   *
   * @param field field to make accessible
   * @return the provided field
   */
  public static Field setAccessible(final Field field) {
    try {
      field.setAccessible(true);
    } catch (final Exception e) {
    }
    return field;
  }

  /**
   * Makes the given method accessible.
   *
   * @param method method to make accessible
   * @return the provided method
   * @throws ReflectiveOperationException if reflective access fails in an unexpected way
   */
  public static Method setAccessible(final Method method) throws ReflectiveOperationException {
    try {
      method.setAccessible(true);
    } catch (final Exception e) {
    }
    return method;
  }

  /**
   * Makes the given constructor accessible.
   *
   * @param constructor constructor to make accessible
   * @param <T> constructor type parameter
   * @return the provided constructor
   * @throws ReflectiveOperationException if reflective access fails in an unexpected way
   */
  public static <T> Constructor<T> setAccessible(final Constructor<T> constructor) throws ReflectiveOperationException {
    try {
      constructor.setAccessible(true);
    } catch (final Exception e) {
    }
    return constructor;
  }

  /**
   * Attempts to remove the {@code final} modifier from the given field.
   *
   * @param field field whose final modifier should be removed
   */
  public static void removeFinal(final Field field) {
    final int modifiers = field.getModifiers();
    if (!Modifier.isFinal(modifiers)) {
      return;
    }
    final int newModifiers = modifiers & ~Modifier.FINAL;
    try {
      modifiersField.setInt(field, newModifiers);
    } catch (final Exception e1) {
      try {
        if (modifiersVarHandle() != null) {
          modifiersVarHandle().set(field, newModifiers);
        } else {
          modifiersField.setInt(field, newModifiers);
        }
      } catch (final Exception e2) {
        try {
          // https://github.com/ViaVersion/ViaVersion/blob/e07c994ddc50e00b53b728d08ab044e66c35c30f/bungee/src/main/java/us/myles/ViaVersion/bungee/platform/BungeeViaInjector.java
          // Java 12 compatibility
          for (final Field classField : declaredFields(Field.class)) {
            if ("modifiers".equals(classField.getName())) {
              classField.setAccessible(true);
              classField.set(field, newModifiers);
              break;
            }
          }
        } catch (final Exception e3) {
        }
      }
    }
  }

  /**
   * Returns the caller class at the specified stack depth, skipping reflection-related frames.
   *
   * @param depth caller depth relative to the direct caller of this method
   * @return the caller class, or {@code null} if it cannot be resolved
   */
  public static Class<?> getCallerClass(final int depth) {
    try {
      final StackTraceElement element = getCallerElement(depth + 1);
      return element == null ? null : Class.forName(element.getClassName());
    } catch (final Exception e) {
      return null;
    }
  }

  /**
   * Returns the stack trace element for the caller at the specified depth, skipping reflection-related frames.
   *
   * @param depth caller depth relative to the direct caller of this method
   * @return the caller stack trace element
   * @throws IndexOutOfBoundsException if the requested depth is outside the available stack
   */
  // from org.apache.logging.log4j.util.StackLocatorUtil
  private static StackTraceElement getCallerElement(final int depth) {
    final StackTraceElement[] stack = new Throwable().getStackTrace();
    if (stack == null) {
      return null;
    }
    int i = 0;
    for (final StackTraceElement element : stack) {
      // ignore native methods (oftentimes are repeated frames)
      if (element.isNativeMethod()) {
        continue;
      }
      final String cn = element.getClassName();
      // ignore OpenJDK internal classes involved with reflective invocation
      if (cn.startsWith("sun.reflect.")) {
        continue;
      }
      final String mn = element.getMethodName();
      // ignore use of reflection including:
      // Method.invoke
      // InvocationHandler.invoke
      // Constructor.newInstance
      if (cn.startsWith("java.lang.reflect.") && (mn.equals("invoke") || mn.equals("newInstance"))) {
        continue;
      }
      // ignore use of Java 1.9+ reflection classes
      if (cn.startsWith("jdk.internal.reflect.")) {
        continue;
      }
      // ignore Class.newInstance
      if (cn.equals("java.lang.Class") && mn.equals("newInstance")) {
        continue;
      }
      // ignore use of Java 1.7+ MethodHandle.invokeFoo() methods
      if (cn.equals("java.lang.invoke.MethodHandle") && mn.startsWith("invoke")) {
        continue;
      }
      // any others?
      if (i == depth) {
        return element;
      }
      ++i;
    }
    throw new IndexOutOfBoundsException(Integer.toString(depth));
  }
}
