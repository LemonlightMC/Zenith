package com.lemonlightmc.zenith.additive.files;

import com.lemonlightmc.zenith.additive.exceptions.FileException;

/**
 * Represents the outcome of a file operation.
 *
 * @param success whether the operation completed successfully
 * @param invalid whether the operation failed due to invalid input
 * @param failed whether the operation failed during execution
 * @param message a descriptive message for non-successful results
 * @param data optional result data associated with the operation
 */
public record FileResult(boolean success, boolean invalid, boolean failed, String message, Object data) {
  /**
   * Creates a successful result with associated data.
   *
   * @param data the result data
   * @return a successful file result
   */
  public static FileResult successful(final Object data) {
    return new FileResult(true, false, false, null, data);
  }

  /**
   * Creates a successful result without data.
   *
   * @return a successful file result
   */
  public static FileResult successful() {
    return new FileResult(true, false, false, null, null);
  }

  /**
   * Creates a result indicating invalid input.
   *
   * @param message the validation or input error message
   * @return an invalid file result
   */
  public static FileResult invalid(final String message) {
    return new FileResult(false, true, false, message, null);
  }

  /**
   * Creates a result indicating a failed operation.
   *
   * @param message the failure message
   * @return a failed file result
   */
  public static FileResult failed(final String message) {
    return new FileResult(false, false, true, message, null);
  }

  /**
   * Creates a result from an exception.
   *
   * @param ex the exception describing the failure
   * @return a failed file result
   */
  public static FileResult failed(final Exception ex) {
    return new FileResult(false, false, true, ex.getMessage(), null);
  }

  /**
   * Throws a {@link FileException} if this result represents a failure.
   *
   * @return this result if no failure occurred
   * @throws FileException if the operation failed
   */
  public FileResult throwIfFailed() {
    if (failed) {
      throw new FileException(message);
    }
    return this;
  }

  /**
   * Throws a {@link FileException} with the provided message if this result represents a failure.
   *
   * @param message the exception message to use
   * @return this result if no failure occurred
   * @throws FileException if the operation failed
   */
  public FileResult throwIfFailed(final String message) {
    if (failed) {
      throw new FileException(message);
    }
    return this;
  }

  public <E extends RuntimeException> FileResult throwIfFailed(final E exception) {
    if (!failed) {
      return this;
    }
    if (exception == null) {
      throw new FileException();
    }
    throw exception;
  }

  public <E extends RuntimeException> FileResult throwIfFailed(final Class<E> exceptionType) {
    if (!failed) {
      return this;
    }

    if (exceptionType == null) {
      throw new FileException(message);
    }
    final E ex;
    try {
      ex = exceptionType.getConstructor(String.class).newInstance(message);
      if (ex == null) {
        throw new FileException(message);
      }
    } catch (final FileException e) {
      throw e;
    } catch (final Exception e) {
      throw new FileException(message);
    }
    throw ex;
  }

  public <E extends RuntimeException> FileResult throwIfFailedWith(final Class<E> exceptionType,
      final String message2) {
    if (!failed) {
      return this;
    }
    if (exceptionType == null) {
      throw new FileException(message);
    }
    final E ex;
    try {
      ex = exceptionType.getConstructor(String.class).newInstance(message2 + " " + message);
      if (ex == null) {
        throw new FileException(message);
      }
    } catch (final FileException e) {
      throw e;
    } catch (final Exception e) {
      throw new FileException(message);
    }
    throw ex;
  }
}
