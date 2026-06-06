package com.lemonlightmc.zenith.v2.schema.types;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class LongTextColumn extends AbstractColumn<String> {

  public static LongTextColumn of(final String name) {
    return new LongTextColumn(name);
  }

  public static LongTextColumn ofNullable(final String name) {
    return new LongTextColumn(name, true, null);
  }

  public static LongTextColumn ofDefault(final String name, final String defaultValue) {
    return new LongTextColumn(name, false, defaultValue);
  }

  public LongTextColumn(final String name) {
    super(name, "LONGTEXT", null, null, false);
  }

  public LongTextColumn(final String name, final boolean nullable, final String defaultValue) {
    super(name, "LONGTEXT", defaultValue, null, nullable);
  }

  @Override
  public String get(final Object object) {
    return (String) object;
  }

}
