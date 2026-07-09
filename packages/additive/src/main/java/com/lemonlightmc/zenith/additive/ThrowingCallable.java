package com.lemonlightmc.zenith.additive;

@FunctionalInterface
public interface ThrowingCallable<T> {
  T call() throws Exception;
}