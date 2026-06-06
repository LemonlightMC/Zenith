package com.lemonlightmc.zenith.exceptions;

public class ReflectionException extends RuntimeException {

  public ReflectionException(String message) {
    super("Failed at Reflection due " + message);
  }

  public ReflectionException(String message, Throwable cause) {
    super("Failed at Reflection due " + message, cause);
  }

  public ReflectionException(Throwable cause) {
    super("Failed at Reflection due ", cause);
  }
}
