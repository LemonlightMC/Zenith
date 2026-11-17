package com.lemonlightmc.zenith.additive.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Utility methods for number conversion, parsing, and related numeric checks.
 */
public class NumberConversions {
  private NumberConversions() {
  }

  /**
   * Returns the largest integer value less than or equal to the given number.
   *
   * @param num the number to floor
   * @return the floored integer value
   */
  public static int floor(final double num) {
    final int floor = (int) num;
    return floor == num ? floor : floor - (int) (Double.doubleToRawLongBits(num) >>> 63);
  }

  /**
   * Returns the smallest integer value greater than or equal to the given number.
   *
   * @param num the number to ceil
   * @return the ceiled integer value
   */
  public static int ceil(final double num) {
    final int floor = (int) num;
    return floor == num ? floor : floor + (int) (~Double.doubleToRawLongBits(num) >>> 63);
  }

  /**
   * Rounds the given number to the nearest integer value.
   *
   * @param num the number to round
   * @return the rounded integer value
   */
  public static int round(final double num) {
    return floor(num + 0.5d);
  }

  /**
   * Checks whether a {@code double} value is within the finite value range.
   *
   * @param d the value to check
   * @return {@code true} if the value is finite; otherwise {@code false}
   */
  public static boolean isFinite(final double d) {
    return d >= Double.MIN_VALUE && d <= Double.MAX_VALUE;
  }

  /**
   * Checks whether a {@code float} value is within the finite value range.
   *
   * @param f the value to check
   * @return {@code true} if the value is finite; otherwise {@code false}
   */
  public static boolean isFinite(final float f) {
    return f >= Float.MIN_VALUE && f <= Float.MAX_VALUE;
  }

  /**
   * Ensures that a {@code double} value is finite.
   *
   * @param d       the value to check
   * @param message the exception message used when the value is not finite
   * @throws IllegalArgumentException if the value is not finite
   */
  public static void checkFinite(final double d, final String message) {
    if (!isFinite(d)) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Ensures that a {@code float} value is finite.
   *
   * @param d       the value to check
   * @param message the exception message used when the value is not finite
   * @throws IllegalArgumentException if the value is not finite
   */
  public static void checkFinite(final float d, final String message) {
    if (!isFinite(d)) {
      throw new IllegalArgumentException(message);
    }
  }

  /**
   * Checks whether a string appears to represent a decimal number.
   *
   * @param str the string to check
   * @return {@code true} if the string contains a decimal or exponent marker; otherwise {@code false}
   */
  public static boolean isDecimal(final String str) {
    return str != null && (str.indexOf('.') >= 0 || str.indexOf('e') >= 0 || str.indexOf('E') >= 0);
  }

  /**
   * Checks whether a string can be parsed as a numeric value.
   *
   * @param str the string to check
   * @return {@code true} if the string is numeric; otherwise {@code false}
   */
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

  /**
   * Checks whether a string represents a value that fits within the {@code int} range.
   *
   * @param s the string to check
   * @return {@code true} if the value fits within an {@code int}; otherwise {@code false}
   */
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

  /**
   * Checks whether a string represents a value that fits within the {@code long} range.
   *
   * @param s the string to check
   * @return {@code true} if the value fits within a {@code long}; otherwise {@code false}
   */
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

  /**
   * Converts a string to an {@link Integer} using base 10.
   *
   * @param str the string to convert
   * @return the parsed integer value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Integer toInt(final String str) {
    return toInt(str, 10);
  }

  /**
   * Converts a string to an {@link Integer} using the specified radix.
   *
   * @param str   the string to convert
   * @param radix the radix to use during parsing
   * @return the parsed integer value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Integer toInt(final String str, final int radix) {
    return parseInt(str, radix);
  }

  /**
   * Converts a string to a {@link Long} using base 10.
   *
   * @param str the string to convert
   * @return the parsed long value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Long toLong(final String str) {
    return toLong(str, 10);
  }

  /**
   * Converts a string to a {@link Long} using the specified radix.
   *
   * @param str   the string to convert
   * @param radix the radix to use during parsing
   * @return the parsed long value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Long toLong(final String str, final int radix) {
    return Long.valueOf(parseLong(str, radix));
  }

  /**
   * Converts a string to a {@link Short}.
   *
   * @param str the string to convert
   * @return the parsed short value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Short toShort(final String str) {
    return Short.valueOf(parseShort(str));
  }

  /**
   * Converts a string to a {@link Float}.
   *
   * @param str the string to convert
   * @return the parsed float value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Float toFloat(final String str) {
    return Float.valueOf(parseFloat(str));
  }

  /**
   * Converts a string to a {@link Double}.
   *
   * @param str the string to convert
   * @return the parsed double value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Double toDouble(final String str) {
    return Double.valueOf(parseDouble(str));
  }

  /**
   * Converts a string to a {@link Byte}.
   *
   * @param str the string to convert
   * @return the parsed byte value
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static Byte toByte(final String str) {
    return Byte.valueOf(parseByte(str));
  }

  /**
   * Parses a string as an {@code int} using base 10.
   *
   * @param str the string to parse
   * @return the parsed integer value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static int parseInt(final String str) {
    return parseInt(str, 10);
  }

  /**
   * Parses a string as an {@code int} using the specified radix.
   *
   * @param str   the string to parse
   * @param radix the radix to use during parsing
   * @return the parsed integer value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
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

  /**
   * Parses a string as a {@code long} using base 10.
   *
   * @param str the string to parse
   * @return the parsed long value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static long parseLong(final String str) {
    return parseLong(str, 10);
  }

  /**
   * Parses a string as a {@code long} using the specified radix.
   *
   * @param str   the string to parse
   * @param radix the radix to use during parsing
   * @return the parsed long value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
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

  /**
   * Parses a string as a {@code double}.
   *
   * @param str the string to parse
   * @return the parsed double value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
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

  /**
   * Parses a string as a {@code float}.
   *
   * @param str the string to parse
   * @return the parsed float value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
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

  /**
   * Parses a string as a {@code short} using base 10.
   *
   * @param str the string to parse
   * @return the parsed short value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed or is out of range
   */
  public static short parseShort(final String str) {
    return parseShort(str, 10);
  }

  /**
   * Parses a string as a {@code short} using the specified radix.
   *
   * @param str   the string to parse
   * @param radix the radix to use during parsing
   * @return the parsed short value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed or is out of range
   */
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

  /**
   * Parses a string as a {@code byte} using base 10.
   *
   * @param str the string to parse
   * @return the parsed byte value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed or is out of range
   */
  public static byte parseByte(final String str) {
    return parseByte(str, 10);
  }

  /**
   * Parses a string as a {@code byte} using the specified radix.
   *
   * @param str   the string to parse
   * @param radix the radix to use during parsing
   * @return the parsed byte value, or {@code 0} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed or is out of range
   */
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

  /**
   * Parses a string as a {@link BigInteger} using base 10.
   *
   * @param str the string to parse
   * @return the parsed big integer value, or {@link BigInteger#ZERO} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static BigInteger parseBigInteger(final String str) {
    if (str == null || str.isEmpty()) {
      return BigInteger.ZERO;
    }
    return new BigInteger(str, 10);
  }

  /**
   * Parses a string as a {@link BigInteger} using the specified radix.
   *
   * @param str   the string to parse
   * @param radix the radix to use during parsing
   * @return the parsed big integer value, or {@link BigInteger#ZERO} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static BigInteger parseBigInteger(final String str, final int radix) {
    if (str == null || str.isEmpty()) {
      return BigInteger.ZERO;
    }
    return new BigInteger(str, radix);
  }

  /**
   * Parses a string as a {@link BigDecimal}.
   *
   * @param str the string to parse
   * @return the parsed big decimal value, or {@link BigDecimal#ZERO} if the string is {@code null} or empty
   * @throws NumberFormatException if the string cannot be parsed
   */
  public static BigDecimal parseBigDecimal(final String str) {
    if (str == null || str.isEmpty()) {
      return BigDecimal.ZERO;
    }
    return new BigDecimal(str.toCharArray(), 0, str.length(), MathContext.UNLIMITED);
  }
}