package com.lemonlightmc.zenith.additive.interfaces;

@FunctionalInterface
public interface ThrowingSupplier<T> {
  T get() throws Exception;
}