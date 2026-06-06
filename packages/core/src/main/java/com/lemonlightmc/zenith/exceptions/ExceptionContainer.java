package com.lemonlightmc.zenith.exceptions;

public class ExceptionContainer<T> {
  private final Class<T> cls;
  private final ExceptionContainerFunction function;

  public ExceptionContainer(final Class<T> cls, final ExceptionContainerFunction function) {
    this.cls = cls;
    this.function = function;
  }

  public ExceptionContainer(final Class<T> cls, final String message) {
    this.cls = cls;
    this.function = e -> message;
  }

  public static <T> ExceptionContainer<T> from(final Class<T> cls, final ExceptionContainerFunction function) {
    return new ExceptionContainer<T>(cls, function);
  }

  public static <T> ExceptionContainer<T> from(final Class<T> cls, final String message) {
    return new ExceptionContainer<T>(cls, e -> message);
  }

  public static <T> ExceptionContainer<T> from(final Class<T> cls) {
    return new ExceptionContainer<T>(cls, e -> null);
  }

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

  public T create() {
    return _create(function.apply());
  }

  public T create(final Object... args) {
    return _create(function.apply(args));
  }

  public <C> T createWithContext(final C ctx, final Object... args) {
    return _create(ctx, function.apply(args));
  }

  @FunctionalInterface
  public static interface ExceptionContainerFunction {
    public String apply(Object... args);
  }
}