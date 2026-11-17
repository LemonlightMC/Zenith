package com.lemonlightmc.zenith.exceptions;

/**
 * Utility container for lazily constructing exception instances.
 *
 * @param <T> the exception type produced by this container
 */
public class ExceptionContainer<T> {
  private final Class<T> cls;
  private final ExceptionContainerFunction function;

  /**
   * Creates a new exception container with a message-producing function.
   *
   * @param cls the exception class to instantiate
   * @param function the function used to produce constructor arguments or messages
   */
  public ExceptionContainer(final Class<T> cls, final ExceptionContainerFunction function) {
    this.cls = cls;
    this.function = function;
  }

  /**
   * Creates a new exception container with a fixed message.
   *
   * @param cls the exception class to instantiate
   * @param message the fixed message to use when creating exceptions
   */
  public ExceptionContainer(final Class<T> cls, final String message) {
    this.cls = cls;
    this.function = e -> message;
  }

  /**
   * Creates an exception container from the given class and message function.
   *
   * @param cls the exception class to instantiate
   * @param function the function used to produce constructor arguments or messages
   * @param <T> the exception type
   * @return a new exception container
   */
  public static <T> ExceptionContainer<T> from(final Class<T> cls, final ExceptionContainerFunction function) {
    return new ExceptionContainer<T>(cls, function);
  }

  /**
   * Creates an exception container from the given class and fixed message.
   *
   * @param cls the exception class to instantiate
   * @param message the fixed message to use when creating exceptions
   * @param <T> the exception type
   * @return a new exception container
   */
  public static <T> ExceptionContainer<T> from(final Class<T> cls, final String message) {
    return new ExceptionContainer<T>(cls, e -> message);
  }

  /**
   * Creates an exception container from the given class with no message payload.
   *
   * @param cls the exception class to instantiate
   * @param <T> the exception type
   * @return a new exception container
   */
  public static <T> ExceptionContainer<T> from(final Class<T> cls) {
    return new ExceptionContainer<T>(cls, e -> null);
  }

  /**
   * Instantiates the configured exception type with the provided constructor arguments.
   *
   * @param args the constructor arguments
   * @return the created exception instance
   */
  private T _create(Object... args) {
    Object value;
    for (int i = 0; i < args.length; i++) {
      value = args[i];
      args[i] = value == null ? value : value.toString();
    }
    try {
      return (T) cls.getDeclaredConstructor().newInstance(args);
    } catch (Exception e) {
      throw new Error("Failed to create Exception " + e.getClass().getName(), e);
    }
  }

  /**
   * Creates an exception instance using the configured function with no arguments.
   *
   * @return the created exception instance
   */
  public T create() {
    return _create(function.apply());
  }

  /**
   * Creates an exception instance using the configured function with the given arguments.
   *
   * @param args the arguments passed to the message function
   * @return the created exception instance
   */
  public T create(final Object... args) {
    return _create(function.apply(args));
  }

  /**
   * Creates an exception instance with a leading context value and generated message.
   *
   * @param ctx the context value to pass as the first constructor argument
   * @param args the arguments passed to the message function
   * @param <C> the context type
   * @return the created exception instance
   */
  public <C> T createWithContext(final C ctx, final Object... args) {
    return _create(ctx, function.apply(args));
  }

  /**
   * Function used to produce exception constructor input from provided arguments.
   */
  @FunctionalInterface
  public static interface ExceptionContainerFunction {
    /**
     * Applies this function to the given arguments.
     *
     * @param args the input arguments
     * @return the produced value
     */
    public String apply(Object... args);
  }
}