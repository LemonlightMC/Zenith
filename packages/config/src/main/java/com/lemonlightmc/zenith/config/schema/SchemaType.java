package com.lemonlightmc.zenith.config.schema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Describes a supported configuration value type and how to parse it.
 *
 * @param <T> the Java type represented by this schema type
 */
public class SchemaType<T> {
  /** Boolean schema type. */
  public static final SchemaType<Boolean> BOOL = new SchemaType<>("boolean", obj -> {
    if (obj instanceof Boolean) {
      return (Boolean) obj;
    } else if (obj instanceof String) {
      if (Boolean.TRUE.toString().equals(obj)) {
        return true;
      } else if (Boolean.FALSE.toString().equals(obj)) {
        return false;
      }
    }
    return null;
  });

  /** Integer schema type. */
  public static final SchemaType<Integer> INT = new SchemaType<>("int", obj -> {
    if (obj instanceof Integer) {
      return (Integer) obj;
    } else if (obj instanceof String) {
      try {
        return Integer.valueOf((String) obj);
      } catch (final Exception ex) {
      }
    } else if (obj instanceof Character) {
      return (int) ((Character) obj).charValue();
    } else if (obj instanceof Number) {
      return ((Number) obj).intValue();
    }
    return null;
  });

  /** Long schema type. */
  public static final SchemaType<Long> LONG = new SchemaType<>("long", obj -> {
    if (obj instanceof Long) {
      return (Long) obj;
    } else if (obj instanceof String) {
      try {
        return Long.valueOf((String) obj);
      } catch (final Exception ex) {
      }
    } else if (obj instanceof Character) {
      return (long) ((Character) obj).charValue();
    } else if (obj instanceof Number) {
      return ((Number) obj).longValue();
    }
    return null;
  });

  /** Float schema type. */
  public static final SchemaType<Float> FLOAT = new SchemaType<>("float", obj -> {
    if (obj instanceof Float) {
      return (Float) obj;
    } else if (obj instanceof String) {
      try {
        return Float.valueOf((String) obj);
      } catch (final Exception ex) {
      }
    } else if (obj instanceof Character) {
      return (float) ((Character) obj).charValue();
    } else if (obj instanceof Number) {
      return ((Number) obj).floatValue();
    }
    return null;
  });

  /** Double schema type. */
  public static final SchemaType<Double> DOUBLE = new SchemaType<>("double", obj -> {
    if (obj instanceof Double) {
      return (Double) obj;
    } else if (obj instanceof String) {
      try {
        return Double.valueOf((String) obj);
      } catch (final Exception ex) {
      }
    } else if (obj instanceof Character) {
      return (double) ((Character) obj).charValue();
    } else if (obj instanceof Number) {
      return ((Number) obj).doubleValue();
    }
    return null;
  });

  /** Byte schema type. */
  public static final SchemaType<Byte> BYTE = new SchemaType<>("byte", obj -> {
    if (obj instanceof Byte) {
      return (Byte) obj;
    } else if (obj instanceof String) {
      try {
        return Byte.valueOf((String) obj);
      } catch (final Exception ex) {
      }
    } else if (obj instanceof Character) {
      return (byte) ((Character) obj).charValue();
    } else if (obj instanceof Number) {
      return ((Number) obj).byteValue();
    }
    return null;
  });

  /** Character schema type. */
  public static final SchemaType<Character> CHAR = new SchemaType<>("char", obj -> {
    if (obj instanceof Character) {
      return (Character) obj;
    } else if (obj instanceof String) {
      final String str = (String) obj;

      if (str.length() == 1) {
        return str.charAt(0);
      }
    } else if (obj instanceof Number) {
      return (char) ((Number) obj).intValue();
    }
    return null;
  });

  /** Short schema type. */
  public static final SchemaType<Short> SHORT = new SchemaType<>("short", obj -> {
    if (obj instanceof Short) {
      return (Short) obj;
    } else if (obj instanceof String) {
      try {
        return Short.valueOf((String) obj);
      } catch (final Exception ex) {
      }
    } else if (obj instanceof Character) {
      return (short) ((Character) obj).charValue();
    } else if (obj instanceof Number) {
      return ((Number) obj).shortValue();
    }
    return null;
  });

  /** String schema type. */
  public static final SchemaType<String> STRING = new SchemaType<>("string",
      obj -> (obj instanceof String) || isPrimitiveWrapper(obj) ? String.valueOf(obj) : null);

  /** List schema type. */
  public static final SchemaType<List<?>> LIST = new SchemaType<>("list", obj -> {
    return obj instanceof final List<?> list ? list : null;
  });

  /** Map schema type. */
  public static final SchemaType<Map<?, ?>> MAP = new SchemaType<>("map", obj -> {
    return obj instanceof final Map<?, ?> map ? map : null;
  });

  /** Section schema type placeholder. */
  public static final SchemaType<List<SchemaNode>> SECTION = new SchemaType<>("section", obj -> List.of());

  /** Custom schema type that accepts any value. */
  public static final SchemaType<?> CUSTOM = new SchemaType<>("custom", obj -> obj);

  private final String name;
  /** The parser function used to convert raw values into this type. */
  public final Function<Object, T> parser;

  private SchemaType(final String name, final Function<Object, T> parser) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("SchemaType Name cannot be null or empty");
    }
    if (parser == null) {
      throw new IllegalArgumentException("SchemaType Parser cannot be null");
    }
    this.name = name;
    this.parser = parser;
  }

  /**
   * Creates a new schema type with the given name and parser.
   *
   * @param <T> the Java type represented by the schema type
   * @param name the schema type name
   * @param parser the parser used to convert raw values
   * @return a new {@link SchemaType} instance
   */
  public static <T> SchemaType<T> create(final String name, final Function<Object, T> parser) {
    return new SchemaType<>(name, parser);
  }

  /**
   * Returns the name of this schema type.
   *
   * @return the schema type name
   */
  public String getName() {
    return name;
  }

  /**
   * Parses the given object into this schema type.
   *
   * @param obj the object to parse
   * @return the parsed value, or {@code null} if parsing fails
   */
  public T parse(final Object obj) {
    return parser.apply(obj);
  }

  /**
   * Returns the parser function used by this schema type.
   *
   * @return the parser function
   */
  public Function<Object, T> getParser() {
    return parser;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return 31 * (31 + name.hashCode()) + parser.hashCode();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SchemaType<?> other = (SchemaType<?>) obj;
    return name.equals(other.name) && parser.equals(other.parser);
  }

  /**
   * Detects a built-in schema type matching the given value.
   *
   * @param value the value to inspect
   * @return the detected schema type, or {@code null} if no built-in type matches
   */
  public static SchemaType<?> detect(final Object value) {
    if (value instanceof Boolean) {
      return BOOL;
    } else if (value instanceof Integer) {
      return INT;
    } else if (value instanceof Long) {
      return LONG;
    } else if (value instanceof Float) {
      return FLOAT;
    } else if (value instanceof Double) {
      return DOUBLE;
    } else if (value instanceof Byte) {
      return BYTE;
    } else if (value instanceof Character) {
      return CHAR;
    } else if (value instanceof Short) {
      return SHORT;
    } else if (value instanceof String) {
      return STRING;
    } else if (value instanceof List<?>) {
      return LIST;
    } else if (value instanceof Map<?, ?>) {
      return MAP;
    } else {
      return null;
    }
  }

  private static boolean isPrimitiveWrapper(final Object input) {
    return input instanceof Integer || input instanceof Boolean
        || input instanceof Character || input instanceof Byte
        || input instanceof Short || input instanceof Double
        || input instanceof Long || input instanceof Float;
  }

  /**
   * Resolves a built-in schema type by name.
   *
   * @param name the schema type name
   * @return the matching built-in schema type, or {@link #CUSTOM} if none matches
   */
  public static SchemaType<?> valueOf(String name) {
    if (name == null || name.isEmpty()) {
      return SchemaType.CUSTOM;
    }
    if (SchemaType.BOOL.getName().equals(name)) {
      return SchemaType.BOOL;
    }
    if (SchemaType.INT.getName().equals(name)) {
      return SchemaType.INT;
    }
    if (SchemaType.LONG.getName().equals(name)) {
      return SchemaType.LONG;
    }
    if (SchemaType.FLOAT.getName().equals(name)) {
      return SchemaType.FLOAT;
    }
    if (SchemaType.DOUBLE.getName().equals(name)) {
      return SchemaType.DOUBLE;
    }
    if (SchemaType.BYTE.getName().equals(name)) {
      return SchemaType.BYTE;
    }
    if (SchemaType.CHAR.getName().equals(name)) {
      return SchemaType.CHAR;
    }
    if (SchemaType.SHORT.getName().equals(name)) {
      return SchemaType.SHORT;
    }
    if (SchemaType.STRING.getName().equals(name)) {
      return SchemaType.STRING;
    }
    if (SchemaType.LIST.getName().equals(name)) {
      return SchemaType.LIST;
    }
    if (SchemaType.MAP.getName().equals(name)) {
      return SchemaType.MAP;
    }
    if (SchemaType.SECTION.getName().equals(name)) {
      return SchemaType.SECTION;
    }
    return SchemaType.CUSTOM;
  }
}
