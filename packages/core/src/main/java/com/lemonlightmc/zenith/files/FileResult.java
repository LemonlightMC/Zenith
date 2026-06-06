package com.lemonlightmc.zenith.files;

import com.lemonlightmc.zenith.exceptions.FileException;

public record FileResult(boolean success, boolean invalid, boolean failed, String message, Object data) {
  public static FileResult successful(final Object data) {
    return new FileResult(true, false, false, null, data);
  }

  public static FileResult successful() {
    return new FileResult(true, false, false, null, null);
  }

  public static FileResult invalid(final String message) {
    return new FileResult(false, true, false, message, null);
  }

  public static FileResult failed(final String message) {
    return new FileResult(false, false, true, message, null);
  }

  public static FileResult failed(final Exception ex) {
    return new FileResult(false, false, true, ex.getMessage(), null);
  }

  public FileResult throwIfFailed() {
    if (failed) {
      throw new FileException(message);
    }
    return this;
  }

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
