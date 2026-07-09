package com.lemonlightmc.zenith.additive;

@FunctionalInterface
public interface ThrowingSupplier<T> {
  T get() throws Exception;
}