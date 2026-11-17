package com.lemonlightmc.zenith.exceptions;

/**
 * Indicates that an event handler failed while processing an event.
 */
public class EventHandlerException extends RuntimeException {

  /**
   * Creates a new event handler exception for the specified event and cause.
   *
   * @param cause the underlying failure
   * @param event the event being handled
   */
  public EventHandlerException(Throwable cause, Object event) {
    super("Event handler for " + event.getClass().getName(), cause);
  }

}
