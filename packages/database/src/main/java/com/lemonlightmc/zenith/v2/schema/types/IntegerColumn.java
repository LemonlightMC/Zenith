package com.lemonlightmc.zenith.v2.schema.types;

import java.math.BigDecimal;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class IntegerColumn extends AbstractColumn<Integer> {

  private final boolean autoIncrement;

  public static IntegerColumn of(final String name, final boolean autoIncrement) {
    return new IntegerColumn(name, autoIncrement);
  }

  public static IntegerColumn of(final String name) {
    return of(name, false);
  }

  public static IntegerColumn ofNullable(final String name) {
    return new IntegerColumn(name, true, null, null);
  }

  public static IntegerColumn ofDefault(final String name, final int defaultValue) {
    return new IntegerColumn(name, false, String.valueOf(defaultValue), null);
  }

  public static IntegerColumn ofGenerated(final String name, final String generatedAs) {
    return new IntegerColumn(name, true, null, generatedAs);
  }

  private IntegerColumn(final String name, final boolean autoIncrement) {
    super(name, "INTEGER", null, null, false);
    this.autoIncrement = autoIncrement;
  }

  private IntegerColumn(final String name, final boolean nullable, final String defaultValue,
      final String generatedAs) {
    super(name, "INTEGER", defaultValue, generatedAs, nullable);
    this.autoIncrement = false;
  }

  @Override
  public Integer get(final Object object) {
    if (object instanceof BigDecimal) {
      return ((BigDecimal) object).intValue();
    }
    return (Integer) object;
  }

  @Override
  public String getDefinition() {
    String def = super.getDefinition();
    if (autoIncrement) {
      def = def + " AUTO_INCREMENT";
    }
    return def;
  }

}
