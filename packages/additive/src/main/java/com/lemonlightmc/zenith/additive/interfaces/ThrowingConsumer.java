package com.lemonlightmc.zenith.additive.interfaces;

@FunctionalInterface
public interface ThrowingConsumer<T> {
  void accept(T t) throws Exception;
}