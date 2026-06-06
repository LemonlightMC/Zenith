package com.lemonlightmc.zenith.v2.schema.types;

import java.util.UUID;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class UUIDColumn extends AbstractColumn<UUID> {

  public static UUIDColumn of(String name) {
    return new UUIDColumn(name);
  }

  public static UUIDColumn ofNullable(String name) {
    return new UUIDColumn(name, true, null);
  }

  public static UUIDColumn ofDefault(String name, String defaultValue) {
    return new UUIDColumn(name, false, defaultValue);
  }

  public UUIDColumn(String name) {
    super(name, "VARCHAR(" + 36 + ")", null, null, false);
  }

  public UUIDColumn(String name, boolean nullable, String defaultValue) {
    super(name, "VARCHAR(" + 36 + ")", defaultValue, null, nullable);
  }

  @Override
  public UUID get(Object object) {
    return object instanceof String ? UUID.fromString((String) object) : null;
  }
}
