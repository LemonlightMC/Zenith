package com.lemonlightmc.zenith.additive.math;

import java.util.Random;

import com.lemonlightmc.zenith.additive.exceptions.RangeException;

/**
 * Common mathematical utility methods for rounding, clamping, interpolation, geometry, and random values.
 */
public class MathUtils {

  /** Conversion factor from degrees to radians. */
  public static final double DEG_TO_RAD = 3.1415926f / 180.0f;
  /** Conversion factor from radians to degrees. */
  public static final double RAD_TO_DEG = 180.0f / 3.1415926f;
  /** The mathematical constant tau ({@code 2 * PI}). */
  public static final double TAU = Math.PI * 2;
  /** The mathematical constant pi squared. */
  public static final double SQPI = Math.PI * Math.PI;
  /** Shared random instance used by random helper methods. */
  public static final Random random = new Random();

  /**
   * Rounds a {@code double} value to the specified number of decimal places.
   *
   * @param value     the value to round
   * @param percision the number of decimal places to keep
   * @return the rounded value
   */
  public static double round(final double value, final int percision) {
    return Math.round(value * Math.pow(10, percision)) / Math.pow(10, percision);
  }

  /**
   * Rounds a {@code float} value to the specified number of decimal places.
   *
   * @param value     the value to round
   * @param percision the number of decimal places to keep
   * @return the rounded value
   */
  public static float round(final float value, final int percision) {
    return (float) (Math.round(value * Math.pow(10, percision)) / Math.pow(10, percision));
  }

  /**
   * Rounds a {@code double} value up to the specified number of decimal places.
   *
   * @param value     the value to round up
   * @param percision the number of decimal places to keep
   * @return the ceiled value
   */
  public static double ceil(final double value, final int percision) {
    return Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision);
  }

  /**
   * Rounds a {@code float} value up to the specified number of decimal places.
   *
   * @param value     the value to round up
   * @param percision the number of decimal places to keep
   * @return the ceiled value
   */
  public static float ceil(final float value, final int percision) {
    return (float) (Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision));
  }

  /**
   * Rounds a {@code double} value down to the specified number of decimal places.
   *
   * @param value     the value to round down
   * @param percision the number of decimal places to keep
   * @return the floored value
   */
  public static double floor(final double value, final int percision) {
    return Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision);
  }

  /**
   * Rounds a {@code float} value down to the specified number of decimal places.
   *
   * @param value     the value to round down
   * @param percision the number of decimal places to keep
   * @return the floored value
   */
  public static float floor(final float value, final int percision) {
    return (float) (Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision));
  }

  /**
   * Clamps an integer value to the inclusive range defined by {@code min} and {@code max}.
   *
   * @param value the value to clamp
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @return the clamped value
   */
  public static int clamp(final int value, final int min, final int max) {
    return Math.max(min, Math.min(max, value));
  }

  /**
   * Clamps a {@code double} value to the inclusive range defined by {@code min} and {@code max}.
   *
   * @param value the value to clamp
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @return the clamped value
   */
  public static double clamp(final double value, final double min, final double max) {
    return Math.max(min, Math.min(max, value));
  }

  /**
   * Clamps a {@code float} value to the inclusive range defined by {@code min} and {@code max}.
   *
   * @param value the value to clamp
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @return the clamped value
   */
  public static float clamp(final float value, final float min, final float max) {
    return Math.max(min, Math.min(max, value));
  }

  /**
   * Clamps a {@code long} value to the inclusive range defined by {@code min} and {@code max}.
   *
   * @param value the value to clamp
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @return the clamped value
   */
  public static long clamp(final long value, final long min, final long max) {
    return value < min ? min : (value > max ? max : value);
  }

  /**
   * Maps an integer value from one range to another.
   *
   * @param value the value to map
   * @param min1  the lower bound of the source range
   * @param max1  the upper bound of the source range
   * @param min2  the lower bound of the target range
   * @param max2  the upper bound of the target range
   * @return the mapped value
   */
  public static int map(final int value, final int min1, final int max1, final int min2, final int max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  /**
   * Maps a {@code double} value from one range to another.
   *
   * @param value the value to map
   * @param min1  the lower bound of the source range
   * @param max1  the upper bound of the source range
   * @param min2  the lower bound of the target range
   * @param max2  the upper bound of the target range
   * @return the mapped value
   */
  public static double map(final double value, final double min1, final double max1, final double min2,
      final double max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  /**
   * Maps a {@code float} value from one range to another.
   *
   * @param value the value to map
   * @param min1  the lower bound of the source range
   * @param max1  the upper bound of the source range
   * @param min2  the lower bound of the target range
   * @param max2  the upper bound of the target range
   * @return the mapped value
   */
  public static float map(final float value, final float min1, final float max1, final float min2, final float max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  /**
   * Maps a {@code long} value from one range to another.
   *
   * @param value the value to map
   * @param min1  the lower bound of the source range
   * @param max1  the upper bound of the source range
   * @param min2  the lower bound of the target range
   * @param max2  the upper bound of the target range
   * @return the mapped value
   */
  public static long map(final long value, final long min1, final long max1, final long min2, final long max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  /**
   * Calculates the distance between two 2D points.
   *
   * @param x1 the x coordinate of the first point
   * @param y1 the y coordinate of the first point
   * @param x2 the x coordinate of the second point
   * @param y2 the y coordinate of the second point
   * @return the distance between the two points
   */
  public static float dist(final float x1, final float y1, final float x2, final float y2) {
    return (float) Math.hypot(x2 - x1, y2 - y1);
  }

  /**
   * Calculates the distance between two 2D points.
   *
   * @param x1 the x coordinate of the first point
   * @param y1 the y coordinate of the first point
   * @param x2 the x coordinate of the second point
   * @param y2 the y coordinate of the second point
   * @return the distance between the two points
   */
  public static double dist(final double x1, final double y1, final double x2, final double y2) {
    return Math.hypot(x2 - x1, y2 - y1);
  }

  /**
   * Calculates the distance between two 3D points.
   *
   * @param x1 the x coordinate of the first point
   * @param y1 the y coordinate of the first point
   * @param z1 the z coordinate of the first point
   * @param x2 the x coordinate of the second point
   * @param y2 the y coordinate of the second point
   * @param z2 the z coordinate of the second point
   * @return the distance between the two points
   */
  public static float dist(final float x1, final float y1, final float z1, final float x2, final float y2,
      final float z2) {
    final float x = (x2 - x1);
    final float y = (y2 - y1);
    final float z = (z2 - z1);
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * Calculates the distance between two 3D points.
   *
   * @param x1 the x coordinate of the first point
   * @param y1 the y coordinate of the first point
   * @param z1 the z coordinate of the first point
   * @param x2 the x coordinate of the second point
   * @param y2 the y coordinate of the second point
   * @param z2 the z coordinate of the second point
   * @return the distance between the two points
   */
  public static double dist(final double x1, final double y1, final double z1, final double x2, final double y2,
      final double z2) {
    final double x = (x2 - x1);
    final double y = (y2 - y1);
    final double z = (z2 - z1);
    return Math.sqrt(x * x + y * y + z * z);
  }

  /**
   * Converts degrees to radians.
   *
   * @param degrees the angle in degrees
   * @return the angle in radians
   */
  public static double deg2rad(final int degrees) {
    return degrees * DEG_TO_RAD;
  }

  /**
   * Converts degrees to radians.
   *
   * @param degrees the angle in degrees
   * @return the angle in radians
   */
  public static double deg2rad(final long degrees) {
    return degrees * DEG_TO_RAD;
  }

  /**
   * Converts degrees to radians.
   *
   * @param degrees the angle in degrees
   * @return the angle in radians
   */
  public static double deg2rad(final float degrees) {
    return degrees * DEG_TO_RAD;
  }

  /**
   * Converts degrees to radians.
   *
   * @param degrees the angle in degrees
   * @return the angle in radians
   */
  public static double deg2rad(final double degrees) {
    return degrees * DEG_TO_RAD;
  }

  /**
   * Converts radians to degrees.
   *
   * @param radians the angle in radians
   * @return the angle in degrees
   */
  public static double rad2deg(final int radians) {
    return radians * RAD_TO_DEG;
  }

  /**
   * Converts radians to degrees.
   *
   * @param radians the angle in radians
   * @return the angle in degrees
   */
  public static double rad2deg(final long radians) {
    return radians * RAD_TO_DEG;
  }

  /**
   * Converts radians to degrees.
   *
   * @param radians the angle in radians
   * @return the angle in degrees
   */
  public static double rad2deg(final float radians) {
    return radians * RAD_TO_DEG;
  }

  /**
   * Converts radians to degrees.
   *
   * @param radians the angle in radians
   * @return the angle in degrees
   */
  public static double rad2deg(final double radians) {
    return radians * RAD_TO_DEG;
  }

  /**
   * Linearly interpolates between two integer values.
   *
   * @param start  the start value
   * @param stop   the end value
   * @param amount the interpolation amount
   * @return the interpolated value
   */
  public static int lerp(final int start, final int stop, final int amount) {
    return start + (stop - start) * amount;
  }

  /**
   * Linearly interpolates between two {@code long} values.
   *
   * @param start  the start value
   * @param stop   the end value
   * @param amount the interpolation amount
   * @return the interpolated value
   */
  public static long lerp(final long start, final long stop, final long amount) {
    return start + (stop - start) * amount;
  }

  /**
   * Linearly interpolates between two {@code float} values.
   *
   * @param start  the start value
   * @param stop   the end value
   * @param amount the interpolation amount
   * @return the interpolated value
   */
  public static float lerp(final float start, final float stop, final float amount) {
    return start + (stop - start) * amount;
  }

  /**
   * Linearly interpolates between two {@code double} values.
   *
   * @param start  the start value
   * @param stop   the end value
   * @param amount the interpolation amount
   * @return the interpolated value
   */
  public static double lerp(final double start, final double stop, final double amount) {
    return start + (stop - start) * amount;
  }

  /**
   * Returns a random integer within the inclusive range defined by {@code min} and {@code max}.
   *
   * @param min the minimum value
   * @param max the maximum value
   * @return a random integer in the given range
   */
  public static int getRandomInt(final int min, final int max) {
    return min + random.nextInt(max - min + 1);
  }

  /**
   * Returns a random {@code long} within the range defined by {@code min} and {@code max}.
   *
   * @param min the minimum value
   * @param max the maximum value
   * @return a random long value in the given range
   */
  public static long getRandomLong(final long min, final long max) {
    return min + (random.nextLong() * (max - min));
  }

  /**
   * Returns a random {@code float} within the range defined by {@code min} and {@code max}.
   *
   * @param min the minimum value
   * @param max the maximum value
   * @return a random float value in the given range
   */
  public static float getRandomFloat(final float min, final float max) {
    return min + random.nextFloat() * (max - min);
  }

  /**
   * Returns a random {@code double} within the range defined by {@code min} and {@code max}.
   *
   * @param min the minimum value
   * @param max the maximum value
   * @return a random double value in the given range
   */
  public static double getRandomDouble(final double min, final double max) {
    return min + random.nextDouble() * (max - min);
  }

  /**
   * Returns a random boolean value.
   *
   * @return a random boolean value
   */
  public static boolean getRandomBoolean() {
    return random.nextBoolean();
  }

  /**
   * Returns a random Gaussian-distributed value.
   *
   * @param mean   the mean of the distribution
   * @param stdDev the standard deviation of the distribution
   * @return a random Gaussian-distributed value
   */
  public static double getRandomGaussian(final double mean, final double stdDev) {
    return mean + random.nextGaussian() * stdDev;
  }

  /**
   * Calculates a clamped smooth-step interpolation factor.
   *
   * @param start the lower edge of the transition
   * @param end   the upper edge of the transition
   * @param x     the input value
   * @return the clamped interpolation factor in the range {@code [0, 1]}
   */
  public static float smoothStep(final float start, final float end, final float x) {
    return clamp((x - start) / (end - start), 0f, 1f);
  }

  /**
   * Calculates the angle between two 2D points.
   *
   * @param x1 the x coordinate of the first point
   * @param z1 the z coordinate of the first point
   * @param x2 the x coordinate of the second point
   * @param z2 the z coordinate of the second point
   * @return the angle in radians
   */
  public static double getAngle(final double x1, final double z1, final double x2, final double z2) {
    return Math.atan2(z2 - z1, x2 - x1);
  }

  /**
   * Calculates the angle between two 3D points.
   *
   * @param x1 the x coordinate of the first point
   * @param y1 the y coordinate of the first point
   * @param z1 the z coordinate of the first point
   * @param x2 the x coordinate of the second point
   * @param y2 the y coordinate of the second point
   * @param z2 the z coordinate of the second point
   * @return the angle in radians
   */
  public static double getAngle(final double x1, final double y1, final double z1, final double x2, final double y2,
      final double z2) {
    final double dx = x2 - x1;
    final double dz = z2 - z1;
    return Math.atan2(Math.sqrt(dx * dx + dz * dz), y2 - y1);
  }

  /**
   * Ensures that an integer value lies within the specified inclusive range.
   *
   * @param value the value to validate
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @param name  the name of the value used in error messages
   * @return the normalized value within the specified range
   * @throws RangeException if the value is outside the specified range
   */
  public static int normalizeRangeOrThrow(final int value, final int min, final int max, final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }

  /**
   * Ensures that a {@code long} value lies within the specified inclusive range.
   *
   * @param value the value to validate
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @param name  the name of the value used in error messages
   * @return the normalized value within the specified range
   * @throws RangeException if the value is outside the specified range
   */
  public static long normalizeRangeOrThrow(final long value, final long min, final long max, final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }

  /**
   * Ensures that a {@code double} value lies within the specified inclusive range.
   *
   * @param value the value to validate
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @param name  the name of the value used in error messages
   * @return the normalized value within the specified range
   * @throws RangeException if the value is outside the specified range
   */
  public static double normalizeRangeOrThrow(final double value, final double min, final double max,
      final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }

  /**
   * Ensures that a {@code float} value lies within the specified inclusive range.
   *
   * @param value the value to validate
   * @param min   the minimum allowed value
   * @param max   the maximum allowed value
   * @param name  the name of the value used in error messages
   * @return the normalized value within the specified range
   * @throws RangeException if the value is outside the specified range
   */
  public static float normalizeRangeOrThrow(final float value, final float min, final float max, final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }
}