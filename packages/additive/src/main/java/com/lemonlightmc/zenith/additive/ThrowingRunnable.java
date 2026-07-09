package com.lemonlightmc.zenith.additive;

@FunctionalInterface
public interface ThrowingRunnable {
  void run() throws Exception;
}