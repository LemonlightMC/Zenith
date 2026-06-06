package com.lemonlightmc.zenith.v2.options;

public class NullDatabaseTiming implements DatabaseTiming {
  @Override
  public DatabaseTiming startTiming() {
    return this;
  }

  @Override
  public void stopTiming() {

  }
}