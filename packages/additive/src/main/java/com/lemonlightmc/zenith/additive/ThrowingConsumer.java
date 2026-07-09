package com.lemonlightmc.zenith.additive;

@FunctionalInterface
public interface ThrowingConsumer<T> {
  void accept(T t) throws Exception;
}