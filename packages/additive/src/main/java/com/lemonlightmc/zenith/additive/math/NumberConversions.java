package com.lemonlightmc.zenith.additive.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

public class NumberConversions {
  private NumberConversions() {
  }

  public static int floor(final double num) {
    final int floor = (int) num;
    return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
  }

  public static int ceil(final double num) {
    final int floor = (int) num;
    return floor == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
  }

  public static int round(final double num) {
    return floor(num + 0.5d);
  }

  public static boolean isFinite(final double d) {
    return d >= Double.MIN_VALUE && d <= Double.MAX_VALUE;
  }

  public static boolean isFinite(final float f) {
    return f >= Float.MIN_VALUE && f <= Float.MAX_VALUE;
  }

  public static void checkFinite(final double d, final String message) {
    if (!isFinite(d)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkFinite(final float d, final String message) {
    if (!isFinite(d)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static boolean isDecimal(final String str) {
    return str != null && (str.indexOf('.') >= 0 || str.indexOf('e') >= 0 || str.indexOf('E') >= 0);
  }

  public static boolean isNumeric(String str) {
    if (str == null || str.isEmpty()) {
      return false;
    }
    str = str.trim();
    if (str.isEmpty()) {
      return false;
    }
    try {
      Double.parseDouble(str);
      return true;
    } catch (final NumberFormatException ex) {
      return false;
    }
  }

  public static boolean fitsInt(final String s) {
    if (s == null || s.isBlank()) {
      return false;
    }
    try {
      final long v = Long.parseLong(s);
      return v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE;
    } catch (final Exception e) {
      return false;
    }
  }

  public static boolean fitsLong(final String s) {
    if (s == null || s.isBlank()) {
      return false;
    }
    try {
      final long v = parseLong(s, 10);
      return v >= Long.MIN_VALUE && v <= Long.MAX_VALUE;
    } catch (final Exception e) {
      return false;
    }
  }

  public static Integer toInt(final String str) {
    return toInt(str, 10);
  }

  public static Integer toInt(final String str, final int radix) {
    return parseInt(str, radix);
  }

  public static Long toLong(final String str) {
    return toLong(str, 10);
  }

  public static Long toLong(final String str, final int radix) {
    return Long.valueOf(parseLong(str, radix));
  }

  public static Short toShort(final String str) {
    return Short.valueOf(parseShort(str));
  }

  public static Float toFloat(final String str) {
    return Float.valueOf(parseFloat(str));
  }

  public static Double toDouble(final String str) {
    return Double.valueOf(parseDouble(str));
  }

  public static Byte toByte(final String str) {
    return Byte.valueOf(parseByte(str));
  }

  public static int parseInt(final String str) {
    return parseInt(str, 10);
  }

  public static int parseInt(final String str, int radix) {
    if (str == null || str.isEmpty()) {
      return 0;
    }

    int index = 0;
    boolean negative = false;
    final char firstChar = str.charAt(0);
    // Handle sign, if present
    if (firstChar == '-') {
      negative = true;
      index++;
    } else if (firstChar == '+')
      index++;

    // Handle radix specifier, if present
    if (str.startsWith("0x", index) || str.startsWith("0X", index)) {
      index += 2;
      radix = 16;
    } else if (str.startsWith("#", index)) {
      index++;
      radix = 16;
    } else if (str.startsWith("0", index) && str.length() > 1 + index) {
      index++;
      radix = 8;
    }

    if (str.startsWith("-", index) || str.startsWith("+", index)) {
      throw new NumberFormatException("Sign character in wrong position");
    }
    try {
      final int result = Integer.parseInt(str, index, str.length(), radix);
      return negative ? -result : result;
    } catch (final NumberFormatException e) {
      final String constant = negative ? ("-" + str.substring(index))
          : str.substring(index);
      return Integer.parseInt(constant, radix);
    }
  }

  public static long parseLong(final String str) {
    return parseLong(str, 10);
  }

  public static long parseLong(final String str, int radix) {
    if (str == null || str.isEmpty()) {
      return 0;
    }

    int index = 0;
    boolean negative = false;
    final char firstChar = str.charAt(0);
    // Handle sign, if present
    if (firstChar == '-') {
      negative = true;
      index++;
    } else if (firstChar == '+')
      index++;

    // Handle radix specifier, if present
    if (str.startsWith("0x", index) || str.startsWith("0X", index)) {
      index += 2;
      radix = 16;
    } else if (str.startsWith("#", index)) {
      index++;
      radix = 16;
    } else if (str.startsWith("0", index) && str.length() > 1 + index) {
      index++;
      radix = 8;
    }

    if (str.startsWith("-", index) || str.startsWith("+", index)) {
      throw new NumberFormatException("Sign character in wrong position");
    }
    try {
      final int result = Integer.parseInt(str, index, str.length(), radix);
      return negative ? -result : result;
    } catch (final NumberFormatException e) {
      final String constant = negative ? ("-" + str.substring(index))
          : str.substring(index);
      return Integer.parseInt(constant, radix);
    }
  }

  public static double parseDouble(final String str) {
    if (str == null || str.isEmpty()) {
      return 0;
    }
    try {
      return Double.parseDouble(str);
    } catch (final NumberFormatException e) {
      throw e;
    } catch (final Exception e) {
      throw new NumberFormatException("For input string: \"" + str + "\"");
    }
  }

  public static float parseFloat(final String str) {
    if (str == null || str.isEmpty()) {
      return 0;
    }
    try {
      return Float.parseFloat(str);
    } catch (final NumberFormatException e) {
      throw e;
    } catch (final Exception e) {
      throw new NumberFormatException("For input string: \"" + str + "\"");
    }
  }

  public static short parseShort(final String str) {
    return parseShort(str, 10);
  }

  public static short parseShort(final String str, final int radix) {
    if (str == null || str.isEmpty()) {
      return 0;
    }
    final int i = parseInt(str, radix);
    if (i < Short.MIN_VALUE || i > Short.MAX_VALUE) {
      throw new NumberFormatException(
          "Value out of range. Value:\"" + str + "\" Radix:" + radix);
    }
    return (short) i;
  }

  public static byte parseByte(final String str) {
    return parseByte(str, 10);
  }

  public static byte parseByte(final String str, final int radix) {
    if (str == null || str.isEmpty()) {
      return 0;
    }
    final int i = parseInt(str, radix);
    if (i < Byte.MIN_VALUE || i > Byte.MAX_VALUE) {
      throw new NumberFormatException(
          "Value out of range. Value:\"" + str + "\" Radix:" + radix);
    }
    return (byte) i;
  }

  public static BigInteger parseBigInteger(final String str) {
    if (str == null || str.isEmpty()) {
      return BigInteger.ZERO;
    }
    return new BigInteger(str, 10);
  }

  public static BigInteger parseBigInteger(final String str, final int radix) {
    if (str == null || str.isEmpty()) {
      return BigInteger.ZERO;
    }
    return new BigInteger(str, radix);
  }

  public static BigDecimal parseBigDecimal(final String str) {
    if (str == null || str.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(str.toCharArray(), 0, str.length(), MathContext.UNLIMITED);
  }
}
