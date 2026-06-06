package com.lemonlightmc.zenith.data;

public interface Registrable<K> {

  public K getKey();

  public default void onRegister() {
  }

  public default void onRemove() {
  }
}
