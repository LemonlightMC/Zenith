package com.lemonlightmc.zenith.additive.interfaces;

@FunctionalInterface
public interface ThrowingRunnable {
  void run() throws Exception;
}