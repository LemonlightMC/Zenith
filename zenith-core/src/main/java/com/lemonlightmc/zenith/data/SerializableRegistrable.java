package com.lemonlightmc.zenith.data;

public interface SerializableRegistrable<K> extends Registrable<K> {
  public String toJson();

}
