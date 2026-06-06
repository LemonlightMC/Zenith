package com.lemonlightmc.zenith.utils;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {
  public static boolean getBoolean() {
    return ThreadLocalRandom.current().nextBoolean();
  }

  public static int getInt() {
    return ThreadLocalRandom.current().nextInt();
  }

  public static int getInt(final int max) {
    return ThreadLocalRandom.current().nextInt(max);
  }

  public static int getInt(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max);
  }

  public static int getIntVariance(final int base, final int variance) {
    return base + getInt() * variance * 2 - variance;
  }

  public static int getIntVariance(final int base, final int variance, final double chance) {
    if (chance > getDouble()) {
      return base + getInt() * variance * 2 - variance;
    }
    return base;
  }

  public static long getLong() {
    return ThreadLocalRandom.current().nextLong();
  }

  public static long getLong(final long max) {
    return ThreadLocalRandom.current().nextLong(max);
  }

  public static long getLong(final long min, final long max) {
    return ThreadLocalRandom.current().nextLong(min, max);
  }

  public static long getLongVariance(final long base, final long variance) {
    return base + getLong() * variance * 2 - variance;
  }

  public static long getLongVariance(final long base, final long variance, final double chance) {
    if (chance > getDouble()) {
      return base + getLong() * variance * 2 - variance;
    }
    return base;
  }

  public static float getFloat() {
    return ThreadLocalRandom.current().nextFloat();
  }

  public static float getFloat(final float max) {
    return ThreadLocalRandom.current().nextFloat(max);
  }

  public static float getFloat(final float min, final float max) {
    return ThreadLocalRandom.current().nextFloat(min, max);
  }

  public static float getFloatVariance(final float base, final float variance) {
    return base + getFloat() * variance * 2 - variance;
  }

  public static float getFloatVariance(final float base, final float variance, final double chance) {
    if (chance > getDouble()) {
      return base + getFloat() * variance * 2 - variance;
    }
    return base;
  }

  public static double getDouble() {
    return ThreadLocalRandom.current().nextDouble();
  }

  public static double getDouble(final double max) {
    return ThreadLocalRandom.current().nextDouble(max);
  }

  public static double getDouble(final double min, final double max) {
    return ThreadLocalRandom.current().nextDouble(min, max);
  }

  public static double getDoubleVariance(final double base, final double variance) {
    return base + getDouble() * variance * 2 - variance;
  }

  public static double getDoubleVariance(final double base, final double variance, final double chance) {
    if (chance > getDouble()) {
      return base + getDouble() * variance * 2 - variance;
    }
    return base;
  }

  public static byte[] getBytes(final int amount) {
    final byte[] arr = new byte[amount];
    ThreadLocalRandom.current().nextBytes(arr);
    return arr;
  }

  public static double getExponential() {
    return ThreadLocalRandom.current().nextExponential();
  }

  public static double getGaussian() {
    return ThreadLocalRandom.current().nextGaussian();
  }

  public static double getGaussian(final double mean, final double deviation) {
    return ThreadLocalRandom.current().nextGaussian(mean, deviation);
  }

  public static ThreadLocalRandom current() {
    return ThreadLocalRandom.current();
  }

  @SuppressWarnings("unchecked")
  public static <T> T pick(final Collection<T> collection) {
    if (collection == null || collection.isEmpty()) {
      return null;
    }
    final int size = collection.size();
    final T[] arr = collection.toArray((T[]) new Object[size]);
    return arr[ThreadLocalRandom.current().nextInt(size)];
  }

  public static <T> RandomSelector<T> createSelector(final Collection<T> collection) {
    return new RandomSelector<T>(collection);
  }

  public static <T extends Weighted> T pickWeighted(final Collection<T> collection) {
    if (collection == null || collection.isEmpty()) {
      return null;
    }
    return (new WeightedRandomSelector<T>(collection)).pick();
  }

  public static <T extends Weighted> WeightedRandomSelector<T> createWeightedSelector(final Collection<T> collection) {
    return new WeightedRandomSelector<T>(collection);
  }

  public static class WeightedRandomSelector<T extends Weighted> {

    private T[] arr;
    private double totalWeight;
    private final double[] probabilities;
    private final int len;

    public WeightedRandomSelector(final T[] arr) {
      this.arr = arr;
      this.len = arr.length;
      this.totalWeight = 0d;
      this.probabilities = new double[arr.length];

      for (int i = 0; i < len; i++) {
        final double weight = arr[i].getWeight();
        this.probabilities[i] = weight;
        this.totalWeight += weight;
      }
    }

    @SuppressWarnings("unchecked")
    public WeightedRandomSelector(final Collection<T> collection) {
      this(collection.toArray((T[]) new Object[collection.size()]));
    }

    public T pick() {
      double r = Math.random() * totalWeight;
      int idx = 0;
      for (; idx < len - 1; idx++) {
        r -= probabilities[idx];
        if (r <= 0.0) {
          return arr[idx];
        }
      }
      return arr[idx];
    }
  }

  public static class RandomSelector<T> {

    private T[] arr;
    private final int len;

    public RandomSelector(final T[] arr) {
      this.arr = arr;
      this.len = arr.length;
    }

    @SuppressWarnings("unchecked")
    public RandomSelector(final Collection<T> collection) {
      this(collection.toArray((T[]) new Object[collection.size()]));
    }

    public T pick() {
      return arr[ThreadLocalRandom.current().nextInt(len)];
    }
  }

  public static interface Weighted {
    public double getWeight();
  }
}
