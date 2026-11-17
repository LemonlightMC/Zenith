package com.lemonlightmc.zenith.additive.interfaces;

/**
 * A runnable operation that may throw a checked exception.
 */
@FunctionalInterface
public interface ThrowingRunnable {

  /**
   * Executes this operation.
   *
   * @throws Exception if the operation fails
   */
  void run() throws Exception;
}