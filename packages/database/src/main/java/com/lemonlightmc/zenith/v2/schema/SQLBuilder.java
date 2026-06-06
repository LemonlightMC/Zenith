package com.lemonlightmc.zenith.v2.schema;

import java.math.BigDecimal;
import java.sql.*;
import java.util.StringJoiner;

import com.lemonlightmc.zenith.v2.DB;

public class SQLBuilder implements AutoCloseable {
  private final Connection connection;
  private PreparedStatement ps;
  private ResultSet set;
  private StringBuilder statement = new StringBuilder();

  public SQLBuilder() throws SQLException {
    connection = DB.getGlobalDatabase().getConnection();
  }

  public SQLBuilder(final Connection connection) {
    this.connection = connection;
  }

  public String getStatement() {
    return statement.toString();
  }

  public SQLBuilder createTable(final Table table, final boolean ifNotExists) {
    statement.append("CREATE TABLE").append(ifNotExists ? " IF NOT EXISTS " : " ").append(table.getName())
        .append("(").append(table.getTableAttributes()).append(")");
    return this;
  }

  public String getColumnsAsString(final Column<?>... columns) {
    String columnQuery;
    if (columns.length == 0) {
      columnQuery = "*";
    } else {
      final StringJoiner joiner = new StringJoiner(",");
      for (final Column<?> column : columns) {
        joiner.add(column.getName());
      }
      columnQuery = joiner.toString();
    }
    return columnQuery;
  }

  public SQLBuilder select(final Table table, final Column<?>... columns) {
    statement.append("SELECT ").append(getColumnsAsString(columns)).append(" FROM ").append(table.getName());
    return this;
  }

  public SQLBuilder insert(final Table table, final Column<?>... columns) {
    statement.append("INSERT INTO ").append(table.getName());
    if (columns.length > 0) {
      statement.append(" (").append(getColumnsAsString(columns)).append(")");
    }
    return this;
  }

  public SQLBuilder insertIgnore(final Table table, final Column<?>... columns) {
    statement.append("INSERT IGNORE INTO ").append(table.getName());
    if (columns.length > 0) {
      statement.append(" (").append(getColumnsAsString(columns)).append(")");
    }
    return this;
  }

  public SQLBuilder values(final String values) {
    statement.append(" VALUES (").append(values).append(")");
    return this;
  }

  public SQLBuilder onDuplicateKeyUpdate(final String assignment) {
    statement.append(" ON DUPLICATE KEY UPDATE ").append(assignment);
    return this;
  }

  public SQLBuilder update(final Table table, final Column<?>... columns) {
    statement.append("UPDATE ").append(table.getName()).append(" SET ");
    for (int i = 0; i < columns.length; i++) {
      final String column = columns[i].getName();
      statement.append(column).append(" = ?");

      if (i + 1 < columns.length) {
        statement.append(", ");
      }
    }
    return this;
  }

  public SQLBuilder updateRaw(final Table table) {
    statement.append("UPDATE ").append(table.getName());
    return this;
  }

  public SQLBuilder set() {
    statement.append(" SET ");
    return this;
  }

  public SQLBuilder delete(final Table table) {
    statement.append("DELETE FROM ").append(table.getName());
    return this;
  }

  public SQLBuilder where(final String condition) {
    if (!condition.isEmpty()) {
      statement.append(" WHERE ").append(condition);
    }
    return this;
  }

  public SQLBuilder whereColumnIs(final Column<?> column) {
    return where(column.getName() + " = ?");
  }

  public SQLBuilder and(final String condition) {
    statement.append(" AND ").append(condition);
    return this;
  }

  public SQLBuilder andColumnIs(final Column<?> column) {
    return and(column.getName() + " = ?");
  }

  public SQLBuilder or(final String condition) {
    statement.append(" OR ").append(condition);
    return this;
  }

  public SQLBuilder orColumnIs(final Column<?> column) {
    return or(column.getName() + " = ?");
  }

  public SQLBuilder in(final String values) {
    if (!values.isEmpty()) {
      statement.append(" IN (").append(values).append(")");
    }
    return this;
  }

  public SQLBuilder orderBy(final Column<?> column, final boolean ascending) {
    statement.append(" ORDER BY ").append(column.getName()).append(ascending ? " ASC" : " DESC");
    return this;
  }

  public SQLBuilder limit(final int limit) {
    statement.append(" LIMIT ").append(limit);
    return this;
  }

  public SQLBuilder join(final Table table, final String condition) {
    statement.append(" JOIN ").append(table.getName()).append(" ON ").append(condition);
    return this;
  }

  public SQLBuilder leftJoin(final Table table, final String condition) {
    statement.append(" LEFT JOIN ").append(table.getName()).append(" ON ").append(condition);
    return this;
  }

  public SQLBuilder incrInteger(final Column<?> column, final int amount) {
    statement.append(column.getName()).append(" = ").append(column.getName()).append(" + ").append(amount);
    return this;
  }

  public SQLBuilder setStatement(final String statement) {
    this.statement = new StringBuilder(statement);
    return this;
  }

  public SQLBuilder addStatement(final String sql) throws SQLException {
    if (ps == null) {
      createStatement();
    }
    ps.addBatch(sql);
    return this;
  }

  public SQLBuilder queue(final Object... params) throws SQLException {
    if (ps == null) {
      createStatement();
    }
    fillParams(params);
    ps.addBatch();
    return this;
  }

  public SQLBuilder clearParameters() throws SQLException {
    ps.clearParameters();
    statement = new StringBuilder();
    return this;
  }

  public SQLBuilder createStatement() throws SQLException {
    if (connection != null && !connection.isClosed()) {
      ps = connection.prepareStatement(statement.toString());
    } else {
      throw new SQLException("Connection already closed");
    }
    return this;
  }

  public SQLBuilder createStatement(final int autoGeneratedKeys) throws SQLException {
    if (connection != null && !connection.isClosed()) {
      ps = connection.prepareStatement(statement.toString(), autoGeneratedKeys);
    } else {
      throw new SQLException("Connection already closed");
    }
    return this;
  }

  public SQLBuilder setString(final int number, final String val) throws SQLException {
    this.getPreparedStatement().setString(number, val);
    return this;
  }

  public SQLBuilder setObject(final int number, final Object obj) throws SQLException {
    this.getPreparedStatement().setObject(number, obj);
    return this;
  }

  public int executeUpdate(final Object... params) throws SQLException {
    if (ps == null) {
      createStatement();
    }
    fillParams(params);
    return ps.executeUpdate();
  }

  public int[] executeBatch() throws SQLException {
    return ps.executeBatch();
  }

  public SQLBuilder executeQuery(final Object... params) throws SQLException {
    if (ps == null) {
      createStatement();
    }
    fillParams(params);
    set = ps.executeQuery();
    return this;
  }

  protected void fillParams(final Object... params) throws SQLException {
    for (int i = 0; i < params.length; i++) {
      ps.setObject(i + 1, params[i]);
    }
  }

  public boolean next() throws SQLException {
    return set.next();
  }

  public Object getObject(final int column) throws SQLException {
    return set.getObject(column);
  }

  public Object getObject(final String column) throws SQLException {
    return set.getObject(column);
  }

  public String getString(final String column) throws SQLException {
    return (String) this.getObject(column);
  }

  public String getString(final int column) throws SQLException {
    return (String) getObject(column);
  }

  public Integer getInteger(final String column) throws SQLException {
    final Object obj = getObject(column);
    if (obj instanceof BigDecimal) {
      return ((BigDecimal) obj).intValue();
    }
    return (Integer) obj;
  }

  public Long getLong(final String column) throws SQLException {
    final Object obj = getObject(column);
    if (obj instanceof BigDecimal) {
      return ((BigDecimal) obj).longValue();
    }
    return (Long) obj;
  }

  public Timestamp getTimestamp(final String column) throws SQLException {
    return (Timestamp) getObject(column);
  }

  public Timestamp getTimestamp(final int column) throws SQLException {
    return (Timestamp) getObject(column);
  }

  public Date getDate(final String column) throws SQLException {
    return (Date) getObject(column);
  }

  public Date getDate(final int column) throws SQLException {
    return (Date) getObject(column);
  }

  public <T> T get(final Column<T> column) throws SQLException {
    return column.get(getObject(column.getName()));
  }

  public PreparedStatement getPreparedStatement() {
    return ps;
  }

  public ResultSet getResultSet() {
    return set;
  }

  public Connection getConnection() {
    return connection;
  }

  @Override
  public void close() {
    if (ps != null) {
      closeResource(ps);
    }
    if (set != null) {
      closeResource(set);
    }
    if (connection != null) {
      closeResource(connection);
    }
  }

  protected void closeResource(final AutoCloseable resource) {
    try {
      resource.close();
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}
