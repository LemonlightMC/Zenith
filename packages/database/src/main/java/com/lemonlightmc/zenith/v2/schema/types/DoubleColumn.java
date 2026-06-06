package com.lemonlightmc.zenith.v2.schema.types;

import java.math.BigDecimal;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class DoubleColumn extends AbstractColumn<Double> {

  public static DoubleColumn of(final String name) {
    return new DoubleColumn(name);
  }

  public static DoubleColumn ofNullable(final String name) {
    return new DoubleColumn(name, true, null, null);
  }

  public static DoubleColumn ofDefault(final String name, final int defaultValue) {
    return new DoubleColumn(name, false, String.valueOf(defaultValue), null);
  }

  public static DoubleColumn ofGenerated(final String name, final String generatedAs) {
    return new DoubleColumn(name, true, null, generatedAs);
  }

  private DoubleColumn(final String name) {
    super(name, "DOUBLE", null, null, false);
  }

  public DoubleColumn(final String name, final boolean nullable, final String defaultValue, final String generatedAs) {
    super(name, "DOUBLE", defaultValue, generatedAs, nullable);
  }

  @Override
  public Double get(final Object object) {
    if (object instanceof BigDecimal) {
      return ((BigDecimal) object).doubleValue();
    }
    return (Double) object;
  }

}
