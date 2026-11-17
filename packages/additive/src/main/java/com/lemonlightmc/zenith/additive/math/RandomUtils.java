package com.lemonlightmc.zenith.additive.math;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility methods for generating random values and selecting random elements.
 */
public class RandomUtils {
  /**
   * Returns a random boolean value.
   *
   * @return a random boolean value
   */
  public static boolean getBoolean() {
    return ThreadLocalRandom.current().nextBoolean();
  }

  /**
   * Returns a random integer value.
   *
   * @return a random integer value
   */
  public static int getInt() {
    return ThreadLocalRandom.current().nextInt();
  }

  /**
   * Returns a random integer value between {@code 0} (inclusive) and {@code max} (exclusive).
   *
   * @param max the exclusive upper bound
   * @return a random integer value
   */
  public static int getInt(final int max) {
    return ThreadLocalRandom.current().nextInt(max);
  }

  /**
   * Returns a random integer value between {@code min} (inclusive) and {@code max} (exclusive).
   *
   * @param min the inclusive lower bound
   * @param max the exclusive upper bound
   * @return a random integer value
   */
  public static int getInt(final int min, final int max) {
    return ThreadLocalRandom.current().nextInt(min, max);
  }

  /**
   * Returns a random integer value based on a base value and variance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @return a randomized integer value
   */
  public static int getIntVariance(final int base, final int variance) {
    return base + getInt() * variance * 2 - variance;
  }

  /**
   * Returns a random integer value based on a base value, variance, and chance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @param chance   the probability of applying the variance
   * @return a randomized integer value, or the base value if the chance does not apply
   */
  public static int getIntVariance(final int base, final int variance, final double chance) {
    if (chance > getDouble()) {
      return base + getInt() * variance * 2 - variance;
    }
    return base;
  }

  /**
   * Returns a random {@code long} value.
   *
   * @return a random long value
   */
  public static long getLong() {
    return ThreadLocalRandom.current().nextLong();
  }

  /**
   * Returns a random {@code long} value between {@code 0} (inclusive) and {@code max} (exclusive).
   *
   * @param max the exclusive upper bound
   * @return a random long value
   */
  public static long getLong(final long max) {
    return ThreadLocalRandom.current().nextLong(max);
  }

  /**
   * Returns a random {@code long} value between {@code min} (inclusive) and {@code max} (exclusive).
   *
   * @param min the inclusive lower bound
   * @param max the exclusive upper bound
   * @return a random long value
   */
  public static long getLong(final long min, final long max) {
    return ThreadLocalRandom.current().nextLong(min, max);
  }

  /**
   * Returns a random {@code long} value based on a base value and variance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @return a randomized long value
   */
  public static long getLongVariance(final long base, final long variance) {
    return base + getLong() * variance * 2 - variance;
  }

  /**
   * Returns a random {@code long} value based on a base value, variance, and chance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @param chance   the probability of applying the variance
   * @return a randomized long value, or the base value if the chance does not apply
   */
  public static long getLongVariance(final long base, final long variance, final double chance) {
    if (chance > getDouble()) {
      return base + getLong() * variance * 2 - variance;
    }
    return base;
  }

  /**
   * Returns a random {@code float} value.
   *
   * @return a random float value
   */
  public static float getFloat() {
    return ThreadLocalRandom.current().nextFloat();
  }

  /**
   * Returns a random {@code float} value between {@code 0} (inclusive) and {@code max} (exclusive).
   *
   * @param max the exclusive upper bound
   * @return a random float value
   */
  public static float getFloat(final float max) {
    return ThreadLocalRandom.current().nextFloat(max);
  }

  /**
   * Returns a random {@code float} value between {@code min} (inclusive) and {@code max} (exclusive).
   *
   * @param min the inclusive lower bound
   * @param max the exclusive upper bound
   * @return a random float value
   */
  public static float getFloat(final float min, final float max) {
    return ThreadLocalRandom.current().nextFloat(min, max);
  }

  /**
   * Returns a random {@code float} value based on a base value and variance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @return a randomized float value
   */
  public static float getFloatVariance(final float base, final float variance) {
    return base + getFloat() * variance * 2 - variance;
  }

  /**
   * Returns a random {@code float} value based on a base value, variance, and chance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @param chance   the probability of applying the variance
   * @return a randomized float value, or the base value if the chance does not apply
   */
  public static float getFloatVariance(final float base, final float variance, final double chance) {
    if (chance > getDouble()) {
      return base + getFloat() * variance * 2 - variance;
    }
    return base;
  }

  /**
   * Returns a random {@code double} value.
   *
   * @return a random double value
   */
  public static double getDouble() {
    return ThreadLocalRandom.current().nextDouble();
  }

  /**
   * Returns a random {@code double} value between {@code 0} (inclusive) and {@code max} (exclusive).
   *
   * @param max the exclusive upper bound
   * @return a random double value
   */
  public static double getDouble(final double max) {
    return ThreadLocalRandom.current().nextDouble(max);
  }

  /**
   * Returns a random {@code double} value between {@code min} (inclusive) and {@code max} (exclusive).
   *
   * @param min the inclusive lower bound
   * @param max the exclusive upper bound
   * @return a random double value
   */
  public static double getDouble(final double min, final double max) {
    return ThreadLocalRandom.current().nextDouble(min, max);
  }

  /**
   * Returns a random {@code double} value based on a base value and variance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @return a randomized double value
   */
  public static double getDoubleVariance(final double base, final double variance) {
    return base + getDouble() * variance * 2 - variance;
  }

  /**
   * Returns a random {@code double} value based on a base value, variance, and chance.
   *
   * @param base     the base value
   * @param variance the variance to apply
   * @param chance   the probability of applying the variance
   * @return a randomized double value, or the base value if the chance does not apply
   */
  public static double getDoubleVariance(final double base, final double variance, final double chance) {
    if (chance > getDouble()) {
      return base + getDouble() * variance * 2 - variance;
    }
    return base;
  }

  /**
   * Returns an array filled with random bytes.
   *
   * @param amount the number of bytes to generate
   * @return an array containing random bytes
   */
  public static byte[] getBytes(final int amount) {
    final byte[] arr = new byte[amount];
    ThreadLocalRandom.current().nextBytes(arr);
    return arr;
  }

  /**
   * Returns a random value from an exponential distribution.
   *
   * @return a random exponentially distributed value
   */
  public static double exponential() {
    return ThreadLocalRandom.current().nextExponential();
  }

  /**
   * Returns a random value from a Gaussian distribution.
   *
   * @return a random Gaussian-distributed value
   */
  public static double gaussian() {
    return ThreadLocalRandom.current().nextGaussian();
  }

  /**
   * Returns a random value from a Gaussian distribution with the specified parameters.
   *
   * @param mean      the mean of the distribution
   * @param deviation the standard deviation of the distribution
   * @return a random Gaussian-distributed value
   */
  public static double gussian(final double mean, final double deviation) {
    return ThreadLocalRandom.current().nextGaussian(mean, deviation);
  }

  /**
   * Returns the current thread-local random instance.
   *
   * @return the current {@link ThreadLocalRandom} instance
   */
  public static ThreadLocalRandom current() {
    return ThreadLocalRandom.current();
  }

  /**
   * Picks a random element from the given collection.
   *
   * @param collection the collection to pick from
   * @param <T>        the element type
   * @return a random element, or {@code null} if the collection is {@code null} or empty
   */
  @SuppressWarnings("unchecked")
  public static <T> T pick(final Collection<T> collection) {
    if (collection == null || collection.isEmpty()) {
      return null;
    }
    final int size = collection.size();
    final T[] arr = collection.toArray((T[]) new Object[size]);
    return arr[ThreadLocalRandom.current().nextInt(size)];
  }

  /**
   * Creates a uniform random selector for the given collection.
   *
   * @param collection the collection to select from
   * @param <T>        the element type
   * @return a random selector for the collection
   */
  public static <T> RandomSelector<T> uniformSelection(final Collection<T> collection) {
    return new RandomSelector<T>(collection);
  }

  /**
   * Creates a weighted random selector for the given collection.
   *
   * @param collection the collection to select from
   * @param <T>        the weighted element type
   * @return a weighted random selector for the collection
   */
  public static <T extends Weighted> WeightedRandomSelector<T> weightedSelection(final Collection<T> collection) {
    return new WeightedRandomSelector<T>(collection);
  }

  /**
   * Picks a random weighted element from the given collection.
   *
   * @param collection the collection to pick from
   * @param <T>        the weighted element type
   * @return a random weighted element, or {@code null} if the collection is {@code null} or empty
   */
  public static <T extends Weighted> T pickWeighted(final Collection<T> collection) {
    if (collection == null || collection.isEmpty()) {
      return null;
    }
    return (new WeightedRandomSelector<T>(collection)).pick();
  }

  /**
   * Selects elements randomly based on their associated weights.
   *
   * @param <T> the weighted element type
   */
  public static class WeightedRandomSelector<T extends Weighted> {

    private T[] arr;
    private double totalWeight;
    private final double[] probabilities;
    private final int len;

    /**
     * Creates a weighted random selector from the given array.
     *
     * @param arr the array of weighted elements
     * @throws IllegalArgumentException if the array is {@code null} or empty
     */
    public WeightedRandomSelector(final T[] arr) {
      if (arr == null || arr.length == 0) {
        throw new IllegalArgumentException("Array cannot be null or empty");
      }
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

    /**
     * Creates a weighted random selector from the given collection.
     *
     * @param collection the collection of weighted elements
     * @throws IllegalArgumentException if the collection is {@code null} or empty
     */
    @SuppressWarnings("unchecked")
    public WeightedRandomSelector(final Collection<T> collection) {
      this(collection.toArray((T[]) new Object[collection.size()]));
    }

    /**
     * Picks a random element based on weight.
     *
     * @return a randomly selected element
     */
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

  /**
   * Selects elements randomly with uniform probability.
   *
   * @param <T> the element type
   */
  public static class RandomSelector<T> {

    private T[] arr;
    private final int len;

    /**
     * Creates a random selector from the given array.
     *
     * @param arr the array of elements
     * @throws IllegalArgumentException if the array is {@code null} or empty
     */
    public RandomSelector(final T[] arr) {
      if (arr == null || arr.length == 0) {
        throw new IllegalArgumentException("Array cannot be null or empty");
      }
      this.arr = arr;
      this.len = arr.length;
    }

    /**
     * Creates a random selector from the given collection.
     *
     * @param collection the collection of elements
     * @throws IllegalArgumentException if the collection is {@code null} or empty
     */
    @SuppressWarnings("unchecked")
    public RandomSelector(final Collection<T> collection) {
      this(collection.toArray((T[]) new Object[collection.size()]));
    }

    /**
     * Picks a random element from the selector.
     *
     * @return a randomly selected element
     */
    public T pick() {

      return arr[ThreadLocalRandom.current().nextInt(len)];
    }
  }

  /**
   * Represents a value that can participate in weighted random selection.
   */
  public static interface Weighted {
    /**
     * Returns the weight associated with this element.
     *
     * @return the element weight
     */
    public double getWeight();
  }
}