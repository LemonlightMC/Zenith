package com.lemonlightmc.zenith.exceptions;

public class FileException extends RuntimeException {

  public FileException() {
  }

  public FileException(String msg) {
    super(msg);
  }

  public FileException(Throwable cause) {
    super(cause);
  }

  public FileException(String msg, Throwable cause) {
    super(msg, cause);
  }
}
