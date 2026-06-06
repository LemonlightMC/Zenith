package com.lemonlightmc.zenith.v2.schema.types;

import java.sql.Date;
import java.time.LocalDate;

import com.lemonlightmc.zenith.v2.schema.AbstractColumn;

public class DateColumn extends AbstractColumn<LocalDate> {

  public static DateColumn of(final String name) {
    return new DateColumn(name);
  }

  private DateColumn(final String name) {
    super(name, "DATE", null, null, false);
  }

  @Override
  public LocalDate get(final Object object) {
    if (object instanceof Date) {
      return ((Date) object).toLocalDate();
    }
    return null;
  }
}
