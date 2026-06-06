package com.lemonlightmc.zenith.v2.schema;

import java.util.StringJoiner;

public interface Key extends Attribute {
  static Key primary(final Column<?>... columns) {
    return new PrimaryKey(columns);
  }

  static Key index(final Column<?>... columns) {
    return new IndexKey(false, columns);
  }

  static Key index(final boolean unique, final Column<?>... columns) {
    return new IndexKey(unique, columns);
  }

  static Key unique(final Column<?>... columns) {
    return new Unique(columns);
  }

  Column<?>[] getColumns();

  class PrimaryKey implements Key {

    Column<?>[] columns;

    public PrimaryKey(final Column<?>... columns) {
      this.columns = columns;
    }

    @Override
    public Column<?>[] getColumns() {
      return columns;
    }

    @Override
    public String getDefinition() {
      final StringJoiner joiner = new StringJoiner(",");
      for (final Column<?> column : columns) {
        joiner.add(column.getName());
      }
      return "PRIMARY KEY (" + joiner.toString() + ")";
    }
  }

  class IndexKey implements Key {
    Column<?>[] columns;
    boolean unique;

    public IndexKey(final boolean unique, final Column<?>... columns) {
      this.unique = unique;
      this.columns = columns;
    }

    @Override
    public Column<?>[] getColumns() {
      return columns;
    }

    @Override
    public String getDefinition() {
      final StringJoiner joiner = new StringJoiner(",");
      for (final Column<?> column : columns) {
        joiner.add(column.getName());
      }
      return (unique ? "UNIQUE " : "") + "KEY (" + joiner.toString() + ")";
    }
  }

  class Unique implements Key {
    Column<?>[] columns;

    public Unique(final Column<?>... columns) {
      this.columns = columns;
    }

    @Override
    public Column<?>[] getColumns() {
      return columns;
    }

    @Override
    public String getDefinition() {
      final StringJoiner joiner = new StringJoiner(",");
      for (final Column<?> column : columns) {
        joiner.add(column.getName());
      }
      return "UNIQUE (" + joiner.toString() + ")";
    }
  }
}
