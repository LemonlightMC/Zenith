package com.lemonlightmc.zenith.v2.schema.types;

import java.sql.Timestamp;
import java.time.Instant;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class DateTimeColumn extends AbstractColumn<Instant> {

  public static DateTimeColumn of(final String name) {
    return new DateTimeColumn(name);
  }

  private DateTimeColumn(final String name) {
    super(name, "DATETIME", null, null, false);
  }

  @Override
  public Instant get(final Object object) {
    if (object instanceof Timestamp) {
      return ((Timestamp) object).toInstant();
    }
    return null;
  }

}
