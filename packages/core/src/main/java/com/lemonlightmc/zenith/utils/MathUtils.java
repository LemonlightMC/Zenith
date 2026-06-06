package com.lemonlightmc.zenith.utils;

import java.util.Random;

import com.lemonlightmc.zenith.exceptions.RangeException;

public class MathUtils {

  public static final double DEG_TO_RAD = 3.1415926f / 180.0f;
  public static final double RAD_TO_DEG = 180.0f / 3.1415926f;
  public static final double TAU = Math.PI * 2;
  public static final double SQPI = Math.PI * Math.PI;
  public static final Random random = new Random();

  public static double round(final double value, final int percision) {
    return Math.round(value * Math.pow(10, percision)) / Math.pow(10, percision);
  }

  public static float round(final float value, final int percision) {
    return (float) (Math.round(value * Math.pow(10, percision)) / Math.pow(10, percision));
  }

  public static double ceil(final double value, final int percision) {
    return Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision);
  }

  public static float ceil(final float value, final int percision) {
    return (float) (Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision));
  }

  public static double floor(final double value, final int percision) {
    return Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision);
  }

  public static float floor(final float value, final int percision) {
    return (float) (Math.ceil(value * Math.pow(10, percision)) / Math.pow(10, percision));
  }

  public static int clamp(final int value, final int min, final int max) {
    return Math.max(min, Math.min(max, value));
  }

  public static double clamp(final double value, final double min, final double max) {
    return Math.max(min, Math.min(max, value));
  }

  public static float clamp(final float value, final float min, final float max) {
    return Math.max(min, Math.min(max, value));
  }

  public static long clamp(final long value, final long min, final long max) {
    return value < min ? min : (value > max ? max : value);
  }

  public static int map(final int value, final int min1, final int max1, final int min2, final int max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  public static double map(final double value, final double min1, final double max1, final double min2,
      final double max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  public static float map(final float value, final float min1, final float max1, final float min2, final float max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  public static long map(final long value, final long min1, final long max1, final long min2, final long max2) {
    return (value - min1) / (max1 - min1) * (max2 - min2) + min2;
  }

  public static float dist(final float x1, final float y1, final float x2, final float y2) {
    return (float) Math.hypot(x2 - x1, y2 - y1);
  }

  public static double dist(final double x1, final double y1, final double x2, final double y2) {
    return Math.hypot(x2 - x1, y2 - y1);
  }

  public static float dist(final float x1, final float y1, final float z1, final float x2, final float y2,
      final float z2) {
    final float x = (x2 - x1);
    final float y = (y2 - y1);
    final float z = (z2 - z1);
    return (float) Math.sqrt(x * x + y * y + z * z);
  }

  public static double dist(final double x1, final double y1, final double z1, final double x2, final double y2,
      final double z2) {
    final double x = (x2 - x1);
    final double y = (y2 - y1);
    final double z = (z2 - z1);
    return Math.sqrt(x * x + y * y + z * z);
  }

  public static double deg2rad(final int degrees) {
    return degrees * DEG_TO_RAD;
  }

  public static double deg2rad(final long degrees) {
    return degrees * DEG_TO_RAD;
  }

  public static double deg2rad(final float degrees) {
    return degrees * DEG_TO_RAD;
  }

  public static double deg2rad(final double degrees) {
    return degrees * DEG_TO_RAD;
  }

  public static double rad2deg(final int radians) {
    return radians * RAD_TO_DEG;
  }

  public static double rad2deg(final long radians) {
    return radians * RAD_TO_DEG;
  }

  public static double rad2deg(final float radians) {
    return radians * RAD_TO_DEG;
  }

  public static double rad2deg(final double radians) {
    return radians * RAD_TO_DEG;
  }

  public static int lerp(final int start, final int stop, final int amount) {
    return start + (stop - start) * amount;
  }

  public static long lerp(final long start, final long stop, final long amount) {
    return start + (stop - start) * amount;
  }

  public static float lerp(final float start, final float stop, final float amount) {
    return start + (stop - start) * amount;
  }

  public static double lerp(final double start, final double stop, final double amount) {
    return start + (stop - start) * amount;
  }

  public static int getRandomInt(final int min, final int max) {
    return min + random.nextInt(max - min + 1);
  }

  public static long getRandomLong(final long min, final long max) {
    return min + (random.nextLong() * (max - min));
  }

  public static float getRandomFloat(final float min, final float max) {
    return min + random.nextFloat() * (max - min);
  }

  public static double getRandomDouble(final double min, final double max) {
    return min + random.nextDouble() * (max - min);
  }

  public static boolean getRandomBoolean() {
    return random.nextBoolean();
  }

  public static double getRandomGaussian(final double mean, final double stdDev) {
    return mean + random.nextGaussian() * stdDev;
  }

  public static float smoothStep(final float start, final float end, final float x) {
    return clamp((x - start) / (end - start), 0f, 1f);
  }

  public static double getAngle(final double x1, final double z1, final double x2, final double z2) {
    return Math.atan2(z2 - z1, x2 - x1);
  }

  public static double getAngle(final double x1, final double y1, final double z1, final double x2, final double y2,
      final double z2) {
    final double dx = x2 - x1;
    final double dz = z2 - z1;
    return Math.atan2(Math.sqrt(dx * dx + dz * dz), y2 - y1);
  }

  public static int normalizeRangeOrThrow(final int value, final int min, final int max, final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }

  public static long normalizeRangeOrThrow(final long value, final long min, final long max, final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }

  public static double normalizeRangeOrThrow(final double value, final double min, final double max,
      final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }

  public static float normalizeRangeOrThrow(final float value, final float min, final float max, final String name) {
    if (value < min || value > max) {
      throw new RangeException(
          name + " must be in range [" + min + "; " + max + "] inclusive.");
    }
    return Math.min(Math.max(value, min), max);
  }
}
