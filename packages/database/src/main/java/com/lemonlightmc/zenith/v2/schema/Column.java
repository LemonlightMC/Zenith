package com.lemonlightmc.zenith.v2.schema;

public interface Column<T> extends Attribute {
  T get(Object object);

  String getName();

  String getSqlType();

  boolean isNullable();
}