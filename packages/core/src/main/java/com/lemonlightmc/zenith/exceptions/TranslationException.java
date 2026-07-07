package com.lemonlightmc.zenith.exceptions;

public class TranslationException extends RuntimeException {
  public TranslationException() {
    super();
  }

  public TranslationException(String message) {
    super(message);
  }

  public TranslationException(String message, Throwable cause) {
    super(message, cause);
  }

  public TranslationException(Throwable cause) {
    super(cause);
  }
}
