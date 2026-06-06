package com.lemonlightmc.zenith.v2.schema;

public abstract class AbstractColumn<T> implements Column<T> {
  private final String name, sqlType;
  private final boolean nullable;
  private final String defaultValue, generatedAs;

  public AbstractColumn(final String name, final String sqlType, final String defaultValue,
      final String generatedAs, final boolean nullable) {
    this.name = name;
    this.sqlType = sqlType;
    this.nullable = nullable;
    this.defaultValue = defaultValue;
    this.generatedAs = generatedAs;
  }

  public AbstractColumn(final String name, final String sqlType, final String defaultValue,
      final String generatedAs) {
    this(name, sqlType, defaultValue, generatedAs, true);
  }

  @Override
  public String getDefinition() {
    final StringBuilder definitionBuilder = new StringBuilder();
    definitionBuilder.append(getName())
        .append(" ")
        .append(getSqlType());
    if (!isNullable())
      definitionBuilder.append(" NOT NULL");
    if (defaultValue != null)
      definitionBuilder.append(" DEFAULT ").append(defaultValue);
    if (generatedAs != null)
      definitionBuilder.append(" GENERATED ALWAYS AS (").append(generatedAs).append(") PERSISTENT");
    return definitionBuilder.toString();
  }

  @Override
  public abstract T get(Object object);

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSqlType() {
    return sqlType;
  }

  @Override
  public boolean isNullable() {
    return nullable;
  }
}
