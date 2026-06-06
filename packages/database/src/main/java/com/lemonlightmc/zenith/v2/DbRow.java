package com.lemonlightmc.zenith.v2;

import java.util.HashMap;

public class DbRow extends HashMap<String, Object> {

  @SuppressWarnings("unchecked")
  public <T> T get(final String column) {
    final Object obj = super.get(column);
    return (T) obj;
  }

  @SuppressWarnings("unchecked")
  public <T> T get(final String column, final T def) {
    final T res = (T) super.get(column);
    return res == null ? def : res;
  }

  public Long getLong(final String column) {
    return get(column);
  }

  public Long getLong(final String column, final long def) {
    return get(column, def);
  }

  public Integer getInt(final String column) {
    return get(column);
  }

  public Integer getInt(final String column, final int def) {
    return get(column, def);
  }

  public float getFloat(final String column) {
    return get(column);
  }

  public float getFloat(final String column, final float def) {
    return get(column, def);
  }

  public double getDouble(final String column) {
    return get(column);
  }

  public double getDouble(final String column, final double def) {
    return get(column, def);
  }

  public String getString(final String column) {
    return get(column);
  }

  public String getString(final String column, final String def) {
    return get(column, def);
  }

  public Short getShort(final String column) {
    return get(column);
  }

  public Short getShort(final String column, final Short def) {
    return get(column, def);
  }

  public Byte getByte(final String column) {
    return get(column);
  }

  public Byte getByte(final String column, final Byte def) {
    return get(column, def);
  }

  public Byte[] getBytes(final String column) {
    return get(column);
  }

  public boolean getBoolean(final String column) {
    return getBoolean(column, false);
  }

  public boolean getBoolean(final String column, final Boolean def) {
    final Object value = this.get(column);
    if (value instanceof final Long l)
      return l.longValue() == 1L;
    if (value instanceof final Integer i)
      return i.intValue() == 1;
    if (value instanceof final String str) {
      if (str == "false")
        return false;
      else if (str == "true")
        return false;
    }
    return def;
  }

  @SuppressWarnings("unchecked")
  public <T> T remove(final String column) {
    return (T) super.remove(column);
  }

  @SuppressWarnings("unchecked")
  public <T> T remove(final String column, final T def) {
    final T res = (T) super.remove(column);
    return res == null ? def : res;
  }

  @Override
  public DbRow clone() {
    final DbRow row = new DbRow();
    row.putAll(this);
    return row;
  }
}
