package com.lemonlightmc.zenith.additive.interfaces;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
  R apply(T t) throws Exception;
}