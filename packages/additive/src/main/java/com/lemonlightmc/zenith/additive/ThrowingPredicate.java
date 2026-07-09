package com.lemonlightmc.zenith.additive;

@FunctionalInterface
public interface ThrowingPredicate<T> {
  boolean test(T t) throws Exception;
}