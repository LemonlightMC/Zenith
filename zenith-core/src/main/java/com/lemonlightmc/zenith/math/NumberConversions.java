package com.lemonlightmc.zenith.math;

public class NumberConversions {
  private NumberConversions() {
  }

  public static int floor(double num) {
    final int floor = (int) num;
    return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
  }

  public static int ceil(final double num) {
    final int floor = (int) num;
    return floor == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
  }

  public static int round(double num) {
    return floor(num + 0.5d);
  }

  public static double square(double num) {
    return num * num;
  }

  public static int toInt(Object object) {
    if (object instanceof Number) {
      return ((Number) object).intValue();
    }

    try {
      return Integer.parseInt(object.toString());
    } catch (NumberFormatException e) {
    } catch (NullPointerException e) {
    }
    return 0;
  }

  public static float toFloat(Object object) {
    if (object instanceof Number) {
      return ((Number) object).floatValue();
    }

    try {
      return Float.parseFloat(object.toString());
    } catch (NumberFormatException e) {
    } catch (NullPointerException e) {
    }
    return 0;
  }

  public static double toDouble(Object object) {
    if (object instanceof Number) {
      return ((Number) object).doubleValue();
    }

    try {
      return Double.parseDouble(object.toString());
    } catch (NumberFormatException e) {
    } catch (NullPointerException e) {
    }
    return 0;
  }

  public static long toLong(Object object) {
    if (object instanceof Number) {
      return ((Number) object).longValue();
    }

    try {
      return Long.parseLong(object.toString());
    } catch (NumberFormatException e) {
    } catch (NullPointerException e) {
    }
    return 0;
  }

  public static short toShort(Object object) {
    if (object instanceof Number) {
      return ((Number) object).shortValue();
    }

    try {
      return Short.parseShort(object.toString());
    } catch (NumberFormatException e) {
    } catch (NullPointerException e) {
    }
    return 0;
  }

  public static byte toByte(Object object) {
    if (object instanceof Number) {
      return ((Number) object).byteValue();
    }

    try {
      return Byte.parseByte(object.toString());
    } catch (NumberFormatException e) {
    } catch (NullPointerException e) {
    }
    return 0;
  }

  public static boolean isFinite(double d) {
    return d >= Double.MIN_VALUE && d <= Double.MAX_VALUE;
  }

  public static boolean isFinite(float f) {
    return f >= Float.MIN_VALUE && f <= Float.MAX_VALUE;
  }

  public static void checkFinite(double d, String message) {
    if (!isFinite(d)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkFinite(float d, String message) {
    if (!isFinite(d)) {
      throw new IllegalArgumentException(message);
    }
  }

  // Parsing methods that throw exceptions (matching Java API)
  public static int decodeInt(String str) {
    return Integer.decode(str);
  }

  public static long decodeLong(String str) {
    return Long.decode(str);
  }

  public static double parseDouble(String str) {
    return Double.parseDouble(str);
  }

  public static float parseFloat(String str) {
    return Float.parseFloat(str);
  }

  public static short parseShort(String str) {
    return Short.parseShort(str);
  }

  public static byte parseByte(String str) {
    return Byte.parseByte(str);
  }

  public static java.math.BigInteger parseBigInteger(String str) {
    return new java.math.BigInteger(str);
  }

  public static java.math.BigDecimal parseBigDecimal(String str) {
    return new java.math.BigDecimal(str);
  }
}
