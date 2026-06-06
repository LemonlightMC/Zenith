package com.lemonlightmc.zenith.v2.schema.types;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class StringColumn extends AbstractColumn<String> {

  public static StringColumn of(final String name, final int length) {
    return new StringColumn(name, length);
  }

  public static StringColumn ofNullable(final String name, final int length) {
    return new StringColumn(name, length, true, null);
  }

  public static StringColumn ofDefault(final String name, final int length, final String defaultValue) {
    return new StringColumn(name, length, false, defaultValue);
  }

  public StringColumn(final String name, final int length) {
    super(name, "VARCHAR(" + length + ")", null, null, false);
  }

  public StringColumn(final String name, final int length, final boolean nullable, final String defaultValue) {
    super(name, "VARCHAR(" + length + ")", defaultValue, null, nullable);
  }

  @Override
  public String get(final Object object) {
    return (String) object;
  }
}
