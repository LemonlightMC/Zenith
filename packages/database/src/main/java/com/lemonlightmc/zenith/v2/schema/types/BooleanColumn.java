package com.lemonlightmc.zenith.v2.schema.types;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class BooleanColumn extends AbstractColumn<Boolean> {

  public static BooleanColumn of(final String name) {
    return new BooleanColumn(name);
  }

  public static BooleanColumn ofDefault(final String name, final boolean defaultValue) {
    return new BooleanColumn(name, defaultValue);
  }

  private BooleanColumn(final String name) {
    super(name, "BIT(1)", null, null);
  }

  private BooleanColumn(final String name, final boolean defaultValue) {
    super(name, "BIT(1)", String.valueOf(defaultValue), null, false);
  }

  @Override
  public Boolean get(final Object object) {
    return (Boolean) object;
  }

}
