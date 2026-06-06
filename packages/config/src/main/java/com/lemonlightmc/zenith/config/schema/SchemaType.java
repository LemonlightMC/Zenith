package com.lemonlightmc.zenith.config.schema;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SchemaType<T> {
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

  public static final SchemaType<String> STRING = new SchemaType<>("string",
      obj -> (obj instanceof String) || isPrimitiveWrapper(obj) ? String.valueOf(obj) : null);

  public static final SchemaType<List<?>> LIST = new SchemaType<>("list", obj -> {
    return obj instanceof final List<?> list ? list : null;
  });

  public static final SchemaType<Map<?, ?>> MAP = new SchemaType<>("map", obj -> {
    return obj instanceof final Map<?, ?> map ? map : null;
  });

  public static final SchemaType<List<SchemaNode>> SECTION = new SchemaType<>("section", obj -> List.of());

  public static final SchemaType<?> CUSTOM = new SchemaType<>("custom", obj -> obj);

  private final String name;
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

  public static <T> SchemaType<T> create(final String name, final Function<Object, T> parser) {
    return new SchemaType<>(name, parser);
  }

  public String getName() {
    return name;
  }

  public T parse(final Object obj) {
    return parser.apply(obj);
  }

  public Function<Object, T> getParser() {
    return parser;
  }

  @Override
  public int hashCode() {
    return 31 * (31 + name.hashCode()) + parser.hashCode();
  }

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
