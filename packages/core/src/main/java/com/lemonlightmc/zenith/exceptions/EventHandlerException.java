package com.lemonlightmc.zenith.exceptions;

public class EventHandlerException extends RuntimeException {

  public EventHandlerException(Throwable cause, Object event) {
    super("Event handler for " + event.getClass().getName(), cause);
  }

}
