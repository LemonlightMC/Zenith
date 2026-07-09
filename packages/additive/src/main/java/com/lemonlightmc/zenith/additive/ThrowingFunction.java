package com.lemonlightmc.zenith.additive;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
  R apply(T t) throws Exception;
}