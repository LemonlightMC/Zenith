package com.lemonlightmc.zenith.v2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.lemonlightmc.zenith.v2.options.DatabaseTiming;

public class DbStatement implements AutoCloseable {
  private Database db;
  private Connection dbConn;
  private PreparedStatement preparedStatement;
  private ResultSet resultSet;
  private String[] resultCols;
  public String query = "";
  // Has changes been made to a transaction w/o commit/rollback on close
  private volatile boolean isDirty = false;
  private final List<Consumer<DbStatement>> onCommit = new ArrayList<>(0);
  private final List<Consumer<DbStatement>> onRollback = new ArrayList<>(0);

  public DbStatement() throws SQLException {
    this(DB.getGlobalDatabase());
  }

  public DbStatement(Database db) throws SQLException {
    this.db = db;
    dbConn = db.getConnection();
    if (dbConn == null) {
      DB.logException(new SQLException("We do not have a database"));
    }
  }

  @SuppressWarnings("unused")
  public void startTransaction() throws SQLException {
    try (DatabaseTiming timing = db.timings("startTransaction")) {
      dbConn.setAutoCommit(false);
      isDirty = true;
    }
  }

  @SuppressWarnings("unused")
  public void commit() throws SQLException {
    if (!isDirty) {
      return;
    }
    try (DatabaseTiming timing = db.timings("commit")) {
      isDirty = false;
      dbConn.commit();
      dbConn.setAutoCommit(true);
      runEvents(this.onCommit);
    }
  }

  private synchronized void runEvents(List<Consumer<DbStatement>> runnables) {
    runnables.forEach(run -> {
      try {
        run.accept(this);
      } catch (Exception e) {
        DB.logException("Exception on transaction runnable", e);
      }
    });
    this.onCommit.clear();
    this.onRollback.clear();
  }

  @SuppressWarnings("unused")
  public synchronized void rollback() throws SQLException {
    if (!isDirty) {
      return;
    }
    try (DatabaseTiming timing = db.timings("rollback")) {
      isDirty = false;
      dbConn.rollback();
      dbConn.setAutoCommit(true);
      runEvents(this.onRollback);
    }
  }

  public boolean inTransaction() {
    return isDirty;
  }

  public synchronized void onCommit(Consumer<DbStatement> run) {
    synchronized (this.onCommit) {
      this.onCommit.add(run);
    }
  }

  public synchronized void onRollback(Consumer<DbStatement> run) {
    synchronized (this.onRollback) {
      this.onRollback.add(run);
    }
  }

  @SuppressWarnings("unused")
  public DbStatement query(String query) throws SQLException {
    this.query = query;
    try (DatabaseTiming timing = db.timings("query: " + query)) {
      closeStatement();
      try {
        preparedStatement = dbConn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
      } catch (SQLException e) {
        close();
        throw e;
      }
    }

    return this;
  }

  public ArrayList<DbRow> executeQueryGetResults(String query, Object... params) throws SQLException {
    this.query(query);
    this.execute(params);
    return getResults();
  }

  public int executeUpdateQuery(String query, Object... params) throws SQLException {
    this.query(query);
    return this.executeUpdate(params);
  }

  public DbRow executeQueryGetFirstRow(String query, Object... params) throws SQLException {
    this.query(query);
    this.execute(params);
    return this.getNextRow();
  }

  public <T> T executeQueryGetFirstColumn(String query, Object... params) throws SQLException {
    this.query(query);
    this.execute(params);
    return this.getFirstColumn();
  }

  public <T> List<T> executeQueryGetFirstColumnResults(String query, Object... params)
      throws SQLException {
    this.query(query);
    this.execute(params);
    List<T> dbRows = new ArrayList<>();
    T result;
    while ((result = this.getFirstColumn()) != null) {
      dbRows.add(result);
    }
    return dbRows;
  }

  @SuppressWarnings("unused")
  private void prepareExecute(Object... params) throws SQLException {
    try (DatabaseTiming timing = db.timings("prepareExecute: " + query)) {
      closeResult();
      if (preparedStatement == null) {
        throw new IllegalStateException("Run Query first on statement before executing!");
      }

      for (int i = 0; i < params.length; i++) {
        preparedStatement.setObject(i + 1, params[i]);
      }
    }
  }

  @SuppressWarnings("unused")
  public int executeUpdate(Object... params) throws SQLException {
    try (DatabaseTiming timing = db.timings("executeUpdate: " + query)) {
      try {
        prepareExecute(params);
        int result = preparedStatement.executeUpdate();
        if (!isDirty) {
          runEvents(this.onCommit);
        }
        return result;
      } catch (SQLException e) {
        if (!isDirty) {
          runEvents(this.onRollback);
        }
        close();
        throw e;
      }
    }
  }

  @SuppressWarnings("unused")
  public DbStatement execute(Object... params) throws SQLException {
    try (DatabaseTiming timing = db.timings("execute: " + query)) {
      try {
        prepareExecute(params);
        resultSet = preparedStatement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int numberOfColumns = resultSetMetaData.getColumnCount();

        resultCols = new String[numberOfColumns];
        // get the column names; column indexes start from 1
        for (int i = 1; i < numberOfColumns + 1; i++) {
          resultCols[i - 1] = resultSetMetaData.getColumnLabel(i);
        }
      } catch (SQLException e) {
        close();
        throw e;
      }
    }
    return this;
  }

  @SuppressWarnings("unused")
  public Long getLastInsertId() throws SQLException {
    try (DatabaseTiming timing = db.timings("getLastInsertId")) {
      try (ResultSet genKeys = preparedStatement.getGeneratedKeys()) {
        if (genKeys == null) {
          return null;
        }
        Long result = null;
        if (genKeys.next()) {
          result = genKeys.getLong(1);
        }
        return result;
      }
    }
  }

  @SuppressWarnings("unused")
  public ArrayList<DbRow> getResults() throws SQLException {
    if (resultSet == null) {
      return null;
    }
    try (DatabaseTiming timing = db.timings("getResults")) {
      ArrayList<DbRow> result = new ArrayList<>();
      DbRow row;
      while ((row = getNextRow()) != null) {
        result.add(row);
      }
      return result;
    }
  }

  public DbRow getNextRow() throws SQLException {
    if (resultSet == null) {
      return null;
    }

    ResultSet nextResultSet = getNextResultSet();
    if (nextResultSet != null) {
      DbRow row = new DbRow();
      for (String col : resultCols) {
        row.put(col, nextResultSet.getObject(col));
      }
      return row;
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  public <T> T getFirstColumn() throws SQLException {
    ResultSet resultSet = getNextResultSet();
    if (resultSet != null) {
      return (T) resultSet.getObject(1);
    }
    return null;
  }

  private ResultSet getNextResultSet() throws SQLException {
    if (resultSet != null && resultSet.next()) {
      return resultSet;
    } else {
      closeResult();
      return null;
    }
  }

  private void closeResult() throws SQLException {
    if (resultSet != null) {
      resultSet.close();
      resultSet = null;
    }
  }

  private void closeStatement() throws SQLException {
    closeResult();
    if (preparedStatement != null) {
      preparedStatement.close();
      preparedStatement = null;
    }
  }

  @Override
  @SuppressWarnings("unused")
  public void close() {
    try (DatabaseTiming timing = db.timings("close")) {

      try {
        closeStatement();
        if (dbConn != null) {
          if (isDirty && !dbConn.getAutoCommit()) {
            DB.logException(new Exception("Statement was not finalized: " + query));
            rollback();
          }
          db.closeConnection(dbConn);
        }
      } catch (SQLException ex) {
        DB.logException("Failed to close DB connection: " + query, ex);
      } finally {
        dbConn = null;
      }
    }
  }

  public boolean isClosed() throws SQLException {
    return dbConn == null || dbConn.isClosed();
  }
}
