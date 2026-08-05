package com.lemonlightmc.zenith.additive.interfaces;

@FunctionalInterface
public interface ThrowingPredicate<T> {
  boolean test(T t) throws Exception;
}