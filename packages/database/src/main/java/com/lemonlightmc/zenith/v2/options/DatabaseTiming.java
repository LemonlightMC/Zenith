package com.lemonlightmc.zenith.v2.options;

public interface DatabaseTiming extends AutoCloseable {

  DatabaseTiming startTiming();

  void stopTiming();

  @Override
  default void close() {
    this.stopTiming();
  }
}
