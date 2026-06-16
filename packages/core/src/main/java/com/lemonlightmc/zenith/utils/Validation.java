package com.lemonlightmc.zenith.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.Nonnegative;

/**
 * Validation class
 */

public final class Validation {

  /** Message for invalid hostname */

  static final String INVALID_EMAIL_MESSAGE = "is expected to be a valid email address but it is actually not a valid email address";

  /** Message for invalid hostname */

  static final String INVALID_HOSTNAME_MESSAGE = "is expected to be a valid hostname/IP address but it is actually not a valid hostname/IP address";

  /** Message for invalid port numbers */

  static final String INVALID_PORT_MESSAGE = "is expected to be within 1-65535 range but is found to be out of the range";

  /** Message for object with null values */

  static final String OBJECT_NULL_MESSAGE = "is expected to be non-null but is found to be null";

  /** Message for natural number error */

  static final String NATURAL_NUMBER_MESSAGE = "is expected to be a natural number (1, 2, ..., N-1, N) but it is actually not a natural number";

  /** Message for greater comparison error */

  static final String GREATER_NUMBER_MESSAGE = "is expected to be greater number, but it is not";

  /** Message for lesser comparison error */

  static final String LESSER_NUMBER_MESSAGE = "is expected to be lesser number, but it is not";

  /** Message for non-negative number error */

  static final String NONNEGATIVE_NUMBER_MESSAGE = "is expected to be non-negative but value is actually a negative number";

  /** Message for non-empty string error */

  static final String EMPTY_STRING_MESSAGE = "is expected to be non-empty but value is actually a empty string";

  /** Message for non-empty array/collection error */

  static final String EMPTY_ARRAY_MESSAGE = "is expected to be non-empty but value is actually empty";

  /** Message for nulls in array/collection error */

  static final String NULLS_IN_ARRAY_MESSAGE = "is expected to have all of its list members to be non-null but the list contains null members";

  /** Message for invalid index */

  static final String INVALID_INDEX_MESSAGE = "is expected to be a valid index but it is actually not a valid index";

  /** Message for invalid instance comparison */

  static final String INVALID_INSTANCE_MESSAGE = "is expected to be an instance of the required type but it is actually not";

  /** Message for invalid assignable comparison */

  static final String INVALID_ASSIGNABLE_MESSAGE = "is expected to be assignable from the given type but it is actually not";

  /** Regex pattern for valid hostname */

  private static final Pattern VALID_HOSTNAME_REGEX = Pattern.compile(
      "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$");

  /** Regex pattern for valid ip address */

  private static final Pattern VALID_IP_REGEX = Pattern.compile(
      "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$");

  /** Regex pattern for valid email address */

  private static final Pattern VALID_EMAIL_REGEX = Pattern.compile(
      "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])");

  private Validation() {
  }

  private static IllegalArgumentException createException(final String variable_name, final String message,
      final int level) {
    String composed_message = "Value ";
    if (variable_name != null) {
      composed_message += "in variable '" + variable_name + "' ";
    }
    composed_message += message;
    final IllegalArgumentException throwable = new IllegalArgumentException(composed_message);
    // Modify stack trace and remove irrelevant parts
    final StackTraceElement[] ste = throwable.getStackTrace();
    throwable.setStackTrace(Arrays.copyOfRange(ste, 2 + level, ste.length));
    return throwable;
  }

  private static IllegalArgumentException createException(final String variable_name, final String message,
      final List<Integer> indices, final int level) {
    String composed_message = "Value ";
    if (variable_name != null) {
      composed_message += "in variable '" + variable_name + "' ";
    }
    composed_message += message;
    if (indices.size() == 1)
      composed_message += ". The invalid member is at index " + indices.iterator().next();
    else {
      composed_message += ". Invalid members are located at indices " +
          Arrays.toString(indices.toArray(new Integer[0]));
    }
    final IllegalArgumentException throwable = new IllegalArgumentException(composed_message);
    // Modify stack trace and remove irrelevant parts
    final StackTraceElement[] ste = throwable.getStackTrace();
    throwable.setStackTrace(Arrays.copyOfRange(ste, 2 + level, ste.length));
    return throwable;
  }

  /**
   * Asserts that port number is valid. If it is invalid, then an exception will
   * be thrown.
   *
   * @param port port number to be asserted
   * @return valid port number
   * @throws IllegalArgumentException thrown if the port number is invalid in any
   *                                  way
   */
  @Nonnegative
  public static int assertValidPortNumber(final int port) throws IllegalArgumentException {
    return assertValidPortNumber(port, null);
  }

  /**
   * Asserts that port number is valid. If it is invalid, then an exception will
   * be thrown.
   *
   * @param port          port number to be asserted
   * @param variable_name name of variable
   * @return valid port number
   * @throws IllegalArgumentException thrown if the port number is invalid in any
   *                                  way
   */
  @Nonnegative
  public static int assertValidPortNumber(final int port, final String variable_name) throws IllegalArgumentException {
    if (port > 0 && port <= 65535)
      return port;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, INVALID_PORT_MESSAGE, 0);
  }

  /**
   * Asserts that hostname is valid. If it is invalid, then an exception will be
   * thrown.
   *
   * @param hostname hostname to be asserted
   * @return valid hostname
   * @throws IllegalArgumentException thrown if the hostname is invalid in any way
   */

  public static String assertValidHostname(final String hostname) throws IllegalArgumentException {
    return assertValidHostname(hostname, null);
  }

  /**
   * Asserts that hostname is valid. If it is invalid, then an exception will be
   * thrown.
   *
   * @param hostname      hostname to be asserted
   * @param variable_name name of variable
   * @return valid hostname
   * @throws IllegalArgumentException thrown if the hostname is invalid in any way
   */

  public static String assertValidHostname(String hostname, final String variable_name)
      throws IllegalArgumentException {
    hostname = innerAssertNonNull(hostname, variable_name, 1);
    if (VALID_HOSTNAME_REGEX.matcher(hostname).matches() || VALID_IP_REGEX.matcher(hostname).matches())
      return hostname;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, INVALID_HOSTNAME_MESSAGE, 0);
  }

  /**
   * Asserts that email address is valid. If it is invalid, then an exception will
   * be thrown.
   *
   * @param email_address email address to be asserted
   * @return valid email address
   * @throws IllegalArgumentException thrown if the email address is invalid in
   *                                  any way
   */

  public static String assertValidEmailAddress(final String email_address) throws IllegalArgumentException {
    return assertValidEmailAddress(email_address, null);
  }

  /**
   * Asserts that email address is valid. If it is invalid, then an exception will
   * be thrown.
   *
   * @param email_address email address to be asserted
   * @param variable_name name of variable
   * @return valid email address
   * @throws IllegalArgumentException thrown if the email address is invalid in
   *                                  any way
   */

  public static String assertValidEmailAddress(String email_address, final String variable_name)
      throws IllegalArgumentException {
    email_address = innerAssertNonNull(email_address, variable_name, 1);
    if (VALID_EMAIL_REGEX.matcher(email_address.toLowerCase()).matches())
      return email_address;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, INVALID_EMAIL_MESSAGE, 0);
  }

  /**
   * Asserts that object is not null. If it is null, then an exception will be
   * thrown.
   *
   * @param <T>    return value type
   * @param object object to be asserted
   * @return non-null object
   * @throws IllegalArgumentException thrown if the object is invalid in any way
   */

  public static <T> T assertNotNull(final T object) throws IllegalArgumentException {
    if (object != null)
      return object;

    // Otherwise go ahead and throw exception
    throw createException(null, OBJECT_NULL_MESSAGE, 0);
  }

  /**
   * Asserts that object is not null. If it is null, then an exception will be
   * thrown.
   *
   * @param <T>           return value type
   * @param object        object to be asserted
   * @param variable_name name of variable
   * @return non-null object
   * @throws IllegalArgumentException thrown if the object is invalid in any way
   */

  public static <T> T assertNotNull(final T object, final String variable_name)
      throws IllegalArgumentException {
    if (object != null)
      return object;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, OBJECT_NULL_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than compared number. If it is not a greater
   * an exception will be thrown.
   *
   * @param number  number to be asserted
   * @param compare number being compared
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertGreaterThan(final int number, final long compare) throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than compared number. If it is not a greater
   * an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param compare       number being compared
   * @param variable_name name of variable
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertGreaterThan(final int number, final long compare, final String variable_name)
      throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than compared number. If it is not a greater
   * an exception will be thrown.
   *
   * @param number  number to be asserted
   * @param compare number being compared
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertGreaterThan(final long number, final long compare) throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than compared number. If it is not a greater
   * an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param compare       number being compared
   * @param variable_name name of variable
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertGreaterThan(final long number, final long compare, final String variable_name)
      throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a lesser than compared number. If it is not a lesser
   * an exception will be thrown.
   *
   * @param number  number to be asserted
   * @param compare number being compared
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertLesserThan(final int number, final long compare) throws IllegalArgumentException {
    if (number < compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, LESSER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a lesser than compared number. If it is not a lesser
   * an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param compare       number being compared
   * @param variable_name name of variable
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertLesserThan(final int number, final long compare, final String variable_name)
      throws IllegalArgumentException {
    if (number < compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, LESSER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a lesser than compared number. If it is not a lesser
   * an exception will be thrown.
   *
   * @param number  number to be asserted
   * @param compare number being compared
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertLesserThan(final long number, final long compare) throws IllegalArgumentException {
    if (number < compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, LESSER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a lesser than compared number. If it is not a lesser
   * an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param compare       number being compared
   * @param variable_name name of variable
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertLesserThan(final long number, final long compare, final String variable_name)
      throws IllegalArgumentException {
    if (number < compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, LESSER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than or equals to compared number. If it is
   * not greater or equal, an exception will be thrown.
   *
   * @param number  number to be asserted
   * @param compare number being compared
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertGreaterThanOrEqual(final int number, final long compare) throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than or equals to compared number. If it is
   * not greater or equal, an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param compare       number being compared
   * @param variable_name name of variable
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertGreaterThanOrEqual(final int number, final long compare, final String variable_name)
      throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than or equals to compared number. If it is
   * not greater or equal, an exception will be thrown.
   *
   * @param number  number to be asserted
   * @param compare number being compared
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertGreaterThanOrEqual(final long number, final long compare) throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a greater than or equals to compared number. If it is
   * not greater or equal, an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param compare       number being compared
   * @param variable_name name of variable
   * @return valid number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertGreaterThanOrEqual(final long number, final long compare, final String variable_name)
      throws IllegalArgumentException {
    if (number > compare)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, GREATER_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a natural number. If it is not a natural number, then
   * an exception will be thrown.
   *
   * @param number number to be asserted
   * @return valid nonnegative number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertNaturalNumber(final int number) throws IllegalArgumentException {
    if (number > 0)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, NATURAL_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a natural number. If it is not a natural number, then
   * an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param variable_name name of variable
   * @return valid nonnegative number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static int assertNaturalNumber(final int number, final String variable_name) throws IllegalArgumentException {
    if (number > 0)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, NATURAL_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a natural number. If it is not a natural number, then
   * an exception will be thrown.
   *
   * @param number number to be asserted
   * @return valid nonnegative number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertNaturalNumber(final long number) throws IllegalArgumentException {
    if (number > 0)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(null, NATURAL_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is a natural number. If it is not a natural number, then
   * an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param variable_name name of variable
   * @return valid nonnegative number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertNaturalNumber(final long number, final String variable_name)
      throws IllegalArgumentException {
    if (number > 0)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, NATURAL_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that number is not a negative number. If it is a negative number,
   * then an exception will be thrown.
   *
   * @param number number to be asserted
   * @return valid nonnegative number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertNonNegative(final long number) throws IllegalArgumentException {
    return assertNonNegative(number, null);
  }

  /**
   * Asserts that number is not a negative number. If it is a negative number,
   * then an exception will be thrown.
   *
   * @param number        number to be asserted
   * @param variable_name name of variable
   * @return valid nonnegative number
   * @throws IllegalArgumentException thrown if the number is invalid in any way
   */
  public static long assertNonNegative(final long number, final String variable_name) throws IllegalArgumentException {
    if (number >= 0)
      return number;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, NONNEGATIVE_NUMBER_MESSAGE, 0);
  }

  /**
   * Asserts that the index is valid for the provided size. If not, an exception
   * will be thrown.
   *
   * @param index index to be asserted
   * @param size  collection or array size
   * @return valid index
   * @throws IllegalArgumentException thrown if the index is invalid in any way
   */
  public static int validIndex(final int index, final int size) throws IllegalArgumentException {
    return validIndex(index, size, null);
  }

  public static int validIndex(final int index, final int size, final String variable_name)
      throws IllegalArgumentException {
    if (index >= 0 && index < size)
      return index;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, INVALID_INDEX_MESSAGE, 0);
  }

  /**
   * Asserts that string is not blank. If it is blank, an exception will be
   * thrown. Blank means null, empty, or whitespace-only.
   *
   * @param string string to be asserted
   * @return valid nonblank string
   * @throws IllegalArgumentException thrown if the string is invalid in any way
   */
  public static String notBlank(final String string) throws IllegalArgumentException {
    return assertNonEmpty(string);
  }

  public static String notBlank(final String string, final String variable_name) throws IllegalArgumentException {
    return assertNonEmpty(string, variable_name);
  }

  /**
   * Asserts that string is not empty. If it is empty, an exception will be
   * thrown. Empty means null or an empty string, but whitespace is allowed.
   *
   * @param string string to be asserted
   * @return valid nonempty string
   * @throws IllegalArgumentException thrown if the string is invalid in any way
   */
  public static String notEmpty(final String string) throws IllegalArgumentException {
    return notEmpty(string, null);
  }

  public static String notEmpty(final String string, final String variable_name) throws IllegalArgumentException {
    final String normalized = innerAssertNonNull(string, variable_name, 1);
    if (!normalized.equals(""))
      return normalized;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, EMPTY_STRING_MESSAGE, 0);
  }

  /**
   * Asserts that the object is an instance of the provided type. If it is not,
   * an exception will be thrown.
   *
   * @param <T>    expected type
   * @param object object to be asserted
   * @param type   expected class type
   * @return object cast to expected type
   * @throws IllegalArgumentException thrown if the object is invalid in any way
   */
  public static <T> T isInstanceOf(final Object object, final Class<T> type) throws IllegalArgumentException {
    return isInstanceOf(object, type, null);
  }

  public static <T> T isInstanceOf(final Object object, final Class<T> type, final String variable_name)
      throws IllegalArgumentException {
    innerAssertNonNull(type, "type", 1);
    final Object normalized = innerAssertNonNull(object, variable_name, 1);
    if (type.isInstance(normalized))
      return type.cast(normalized);

    // Otherwise go ahead and throw exception
    throw createException(variable_name, INVALID_INSTANCE_MESSAGE, 0);
  }

  /**
   * Asserts that one type is assignable from another type. If it is not, an
   * exception will be thrown.
   *
   * @param superType super type to be asserted
   * @param subType   subtype to check assignability from
   * @return validated super type
   * @throws IllegalArgumentException thrown if the type relationship is invalid
   */
  public static Class<?> isAssignableFrom(final Class<?> superType, final Class<?> subType)
      throws IllegalArgumentException {
    return isAssignableFrom(superType, subType, null);
  }

  public static Class<?> isAssignableFrom(final Class<?> superType, final Class<?> subType,
      final String variable_name) throws IllegalArgumentException {
    innerAssertNonNull(superType, "superType", 1);
    innerAssertNonNull(subType, "subType", 1);
    if (superType.isAssignableFrom(subType))
      return superType;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, INVALID_ASSIGNABLE_MESSAGE, 0);
  }

  /**
   * Asserts that string is not an empty string. If it is an empty string, then an
   * exception will be thrown.
   * White space is considered empty space so if string consists of four spaces
   * with nothing else, then it is considered to be empty.
   *
   * @param string string to be asserted
   * @return valid nonempty string
   * @throws IllegalArgumentException thrown if the string is invalid in any way
   */
  public static String assertNonEmpty(final String string) throws IllegalArgumentException {
    return assertNonEmpty(string, null);
  }

  /**
   * Asserts that string is not an empty string. If it is an empty string, then an
   * exception will be thrown.
   * White space is considered empty space so if string consists of four spaces
   * with nothing else, then it is considered to be empty.
   *
   * @param string        string to be asserted
   * @param variable_name name of variable
   * @return valid nonempty string
   * @throws IllegalArgumentException thrown if the string is invalid in any way
   */
  public static String assertNonEmpty(String string, final String variable_name)
      throws IllegalArgumentException {
    string = innerAssertNonNull(string, variable_name, 1);
    if (!string.trim().equals(""))
      return string;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, EMPTY_STRING_MESSAGE, 0);
  }

  /**
   * Asserts that collection is not empty. If it is empty, then an exception will
   * be thrown.
   *
   * @param <T>        type of collection
   * @param collection collection to be asserted
   * @return valid nonempty collection
   * @throws IllegalArgumentException thrown if the collection is invalid in any
   *                                  way
   */
  public static <T> Collection<T> assertNonEmpty(final Collection<T> collection) throws IllegalArgumentException {
    return assertNonEmpty(collection, null, 0);
  }

  /**
   * Asserts that collection is not empty. If it is empty, then an exception will
   * be thrown.
   *
   * @param <T>           type of collection
   * @param collection    collection to be asserted
   * @param variable_name name of variable
   * @return valid nonempty collection
   * @throws IllegalArgumentException thrown if the collection is invalid in any
   *                                  way
   */
  public static <T> Collection<T> assertNonEmpty(final Collection<T> collection, final String variable_name)
      throws IllegalArgumentException {
    return assertNonEmpty(collection, variable_name, 0);
  }

  /**
   * Asserts that collection is not empty. If it is empty, then an exception will
   * be thrown.
   *
   * @param <T>           type of collection
   * @param collection    collection to be asserted
   * @param variable_name name of variable
   * @param level         level of calls. This is used to adjust the stacktrace.
   * @return valid nonempty collection
   * @throws IllegalArgumentException thrown if the collection is invalid in any
   *                                  way
   */
  private static <T> Collection<T> assertNonEmpty(
      final Collection<T> collection,
      final String variable_name,
      final int level)
      throws IllegalArgumentException {
    innerAssertNonNull(collection, variable_name, 1);

    // Pass it forward
    assertNonEmpty(collection.toArray(new Object[0]), variable_name, 1 + level);
    return collection;
  }

  /**
   * Asserts that array is not empty. If it is empty, then an exception will be
   * thrown.
   *
   * @param <T>   type of array
   * @param array array to be asserted
   * @return valid nonempty array
   * @throws IllegalArgumentException thrown if the array is invalid in any way
   */
  public static <T> T[] assertNonEmpty(final T[] array) throws IllegalArgumentException {
    return assertNonEmpty(array, null, 0);
  }

  /**
   * Asserts that array is not empty. If it is empty, then an exception will be
   * thrown.
   *
   * @param <T>           type of array
   * @param array         array to be asserted
   * @param variable_name name of variable
   * @return valid nonempty array
   * @throws IllegalArgumentException thrown if the array is invalid in any way
   */
  public static <T> T[] assertNonEmpty(final T[] array, final String variable_name)
      throws IllegalArgumentException {
    return assertNonEmpty(array, variable_name, 0);
  }

  /**
   * Asserts that array is not empty. If it is empty, then an exception will be
   * thrown.
   *
   * @param <T>           type of array
   * @param array         array to be asserted
   * @param variable_name name of variable
   * @param level         level of calls. This is used to adjust the stacktrace.
   * @return valid nonempty array
   * @throws IllegalArgumentException thrown if the array is invalid in any way
   */
  private static <T> T[] assertNonEmpty(final T[] array, final String variable_name, final int level)
      throws IllegalArgumentException {
    innerAssertNonNull(array, variable_name, level + 1);
    if (Objects.requireNonNull(array).length != 0)
      return array;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, EMPTY_ARRAY_MESSAGE, 0);
  }

  /**
   * Asserts that set does not contain any null elements. If it does contain null
   * elements, then an exception will be thrown.
   * Additionally, it can assert if set is non-empty if requested.
   *
   * @param <T>           type of array
   * @param set           set to be asserted
   * @param empty_allowed true to allow an empty set, false to assert a non-empty
   *                      set
   * @return valid nonnull set
   * @throws IllegalArgumentException thrown if the set is invalid in any way
   */
  public static <T> Set<T> assertNonnullElements(final Set<T> set, final boolean empty_allowed)
      throws IllegalArgumentException {
    return innerAssertNonNullElements(set, null, empty_allowed);
  }

  /**
   * Asserts that set does not contain any null elements. If it does contain null
   * elements, then an exception will be thrown.
   * Additionally, it can assert if set is non-empty if requested.
   *
   * @param <T>           type of array
   * @param set           set to be asserted
   * @param variable_name name of variable
   * @param empty_allowed true to allow an empty set, false to assert a non-empty
   *                      set
   * @return valid nonnull set
   * @throws IllegalArgumentException thrown if the set is invalid in any way
   */
  public static <T> Set<T> assertNonnullElements(
      final Set<T> set,
      final String variable_name,
      final boolean empty_allowed)
      throws IllegalArgumentException {
    return innerAssertNonNullElements(set, variable_name, empty_allowed);
  }

  /**
   * Asserts that set does not contain any null elements. If it does contain null
   * elements, then an exception will be thrown.
   * Additionally, it can assert if set is non-empty if requested.
   *
   * @param <T>           type of array
   * @param set           set to be asserted
   * @param variable_name name of variable
   * @param empty_allowed true to allow an empty set, false to assert a non-empty
   *                      set
   * @return valid nonnull set
   * @throws IllegalArgumentException thrown if the set is invalid in any way
   */
  private static <T> Set<T> innerAssertNonNullElements(
      final Set<T> set,
      final String variable_name,
      final boolean empty_allowed) throws IllegalArgumentException {

    innerAssertNonNull(set, variable_name, 1);
    if (!empty_allowed)
      assertNonEmpty(set, variable_name, 1);
    if (!set.contains(null))
      return set;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, NULLS_IN_ARRAY_MESSAGE, 0);
  }

  /**
   * Asserts that collection does not contain any null elements. If it does
   * contain null elements, then an exception will be thrown.
   * Additionally, it can assert if collection is non-empty if requested.
   *
   * @param <T>           type of array
   * @param collection    collection to be asserted
   * @param empty_allowed true to allow an empty collection, false to assert a
   *                      non-empty collection
   * @return valid nonnull collection
   * @throws IllegalArgumentException thrown if the collection is invalid in any
   *                                  way
   */
  public static <T> Collection<T> assertNonnullElements(final Collection<T> collection, final boolean empty_allowed)
      throws IllegalArgumentException {
    if (collection instanceof Set)
      return innerAssertNonNullElements((Set<T>) collection, null, empty_allowed);
    else
      return innerAssertNonNullElements(collection, null, empty_allowed);
  }

  /**
   * Asserts that collection does not contain any null elements. If it does
   * contain null elements, then an exception will be thrown.
   * Additionally, it can assert if collection is non-empty if requested.
   *
   * @param <T>           type of array
   * @param collection    collection to be asserted
   * @param variable_name name of variable
   * @param empty_allowed true to allow an empty collection, false to assert a
   *                      non-empty collection
   * @return valid nonnull collection
   * @throws IllegalArgumentException thrown if the collection is invalid in any
   *                                  way
   */
  public static <T> Collection<T> assertNonnullElements(
      final Collection<T> collection,
      final String variable_name,
      final boolean empty_allowed) throws IllegalArgumentException {
    if (collection instanceof Set)
      return innerAssertNonNullElements((Set<T>) collection, variable_name, empty_allowed);
    else
      return innerAssertNonNullElements(collection, variable_name, empty_allowed);
  }

  /**
   * Asserts that collection does not contain any null elements. If it does
   * contain null elements, then an exception will be thrown.
   * Additionally, it can assert if collection is non-empty if requested.
   *
   * @param <T>           type of array
   * @param collection    collection to be asserted
   * @param variable_name name of variable
   * @param empty_allowed true to allow an empty collection, false to assert a
   *                      non-empty collection
   * @return valid nonnull collection
   * @throws IllegalArgumentException thrown if the collection is invalid in any
   *                                  way
   */
  private static <T> Collection<T> innerAssertNonNullElements(
      final Collection<T> collection,
      final String variable_name,
      final boolean empty_allowed) throws IllegalArgumentException {

    innerAssertNonNull(collection, variable_name, 1);
    // Pass it forward
    innerAssertNonNullElements(collection.toArray(new Object[0]), variable_name, empty_allowed, 1);
    return collection;
  }

  /**
   * Asserts that array does not contain any null elements. If it does contain
   * null elements, then an exception will be thrown.
   * Additionally, it can assert if array is non-empty if requested.
   *
   * @param <T>           type of array
   * @param array         array to be asserted
   * @param empty_allowed true to allow an empty array, false to assert a
   *                      non-empty array
   * @return valid nonnull array
   * @throws IllegalArgumentException thrown if the array is invalid in any way
   */
  public static <T> T[] assertNonnullElements(final T[] array, final boolean empty_allowed)
      throws IllegalArgumentException {
    return innerAssertNonNullElements(array, null, empty_allowed, 0);
  }

  /**
   * Asserts that array does not contain any null elements. If it does contain
   * null elements, then an exception will be thrown.
   * Additionally, it can assert if array is non-empty if requested.
   *
   * @param <T>           type of array
   * @param array         array to be asserted
   * @param variable_name name of variable
   * @param empty_allowed true to allow an empty array, false to assert a
   *                      non-empty array
   * @return valid nonnull array
   * @throws IllegalArgumentException thrown if the array is invalid in any way
   */
  public static <T> T[] assertNonNullElements(
      final T[] array,
      final String variable_name,
      final boolean empty_allowed) throws IllegalArgumentException {
    return innerAssertNonNullElements(array, variable_name, empty_allowed, 0);
  }

  /**
   * Asserts that array does not contain any null elements. If it does contain
   * null elements, then an exception will be thrown.
   * Additionally, it can assert if array is non-empty if requested.
   *
   * @param <T>           type of array
   * @param array         array to be asserted
   * @param variable_name name of variable
   * @param empty_allowed true to allow an empty array, false to assert a
   *                      non-empty array
   * @param level         level of calls. This is used to adjust the stacktrace.
   * @return valid nonnull array
   * @throws IllegalArgumentException thrown if the array is invalid in any way
   */

  private static <T> T[] innerAssertNonNullElements(
      final T[] array,
      final String variable_name,
      final boolean empty_allowed,
      final int level) throws IllegalArgumentException {

    innerAssertNonNull(array, variable_name, level + 1);
    if (!empty_allowed)
      assertNonEmpty(array, variable_name, level + 1);

    // List to collect any nulls
    final List<Integer> nulls = new LinkedList<>();
    for (int i = 0; i < array.length; i++) {
      if (array[i] == null) {
        nulls.add(i);
      }
    }
    if (nulls.isEmpty()) {
      return array;
    }

    // Otherwise go ahead and throw exception
    throw createException(variable_name, NULLS_IN_ARRAY_MESSAGE, nulls, 0);
  }

  private static <T> T innerAssertNonNull(final T object, final String variable_name, final int level)
      throws IllegalArgumentException {
    if (object != null)
      return object;

    // Otherwise go ahead and throw exception
    throw createException(variable_name, OBJECT_NULL_MESSAGE, level);
  }
}
