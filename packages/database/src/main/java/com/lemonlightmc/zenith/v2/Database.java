package com.lemonlightmc.zenith.v2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.lemonlightmc.zenith.v2.options.DatabaseTiming;

public interface Database {

  default void close() {
    close(30, TimeUnit.SECONDS);
  }

  void close(long timeout, TimeUnit unit);

  Connection getConnection() throws SQLException;

  DatabaseTiming timings(String name);

  // DatabaseOptions getOptions();

  default void closeConnection(final Connection conn) throws SQLException {
    conn.close();
  }

  <T> CompletableFuture<T> dispatchAsync(Callable<T> task);

  default DbStatement createStatement() throws SQLException {
    return new DbStatement(this);
  }

  default DbStatement query(final String query) throws SQLException {
    final DbStatement stm = new DbStatement(this);
    try {
      stm.query(query);
      return stm;
    } catch (final Exception e) {
      stm.close();
      throw e;
    }
  }

  default CompletableFuture<DbStatement> queryAsync(final String query) {
    return dispatchAsync(() -> {
      final DbStatement stm = new DbStatement(this);
      try {
        stm.query(query);
        return stm;
      } catch (final Exception e) {
        stm.close();
        throw e;
      }
    });
  }

  default DbRow getFirstRow(final String query, final Object... params) throws SQLException {
    try (DbStatement statement = query(query)) {
      statement.execute(params);
      return statement.getNextRow();
    }
  }

  default CompletableFuture<DbRow> getFirstRowAsync(final String query, final Object... params) {
    return dispatchAsync(() -> getFirstRow(query, params));
  }

  default <T> T getFirstColumn(final String query, final Object... params) throws SQLException {
    try (DbStatement statement = query(query)) {
      statement.execute(params);
      return statement.getFirstColumn();
    }
  }

  default <T> CompletableFuture<T> getFirstColumnAsync(final String query, final Object... params) {
    return dispatchAsync(() -> getFirstColumn(query, params));
  }

  default <T> List<T> getFirstColumnResults(final String query, final Object... params) throws SQLException {
    final List<T> dbRows = new ArrayList<>();
    T result;
    try (DbStatement statement = query(query)) {
      statement.execute(params);
      while ((result = statement.getFirstColumn()) != null) {
        dbRows.add(result);
      }
    }
    return dbRows;
  }

  default <T> CompletableFuture<List<T>> getFirstColumnResultsAsync(final String query, final Object... params) {
    return dispatchAsync(() -> getFirstColumnResults(query, params));
  }

  default List<DbRow> getResults(final String query, final Object... params) throws SQLException {
    try (DbStatement statement = query(query)) {
      statement.execute(params);
      return statement.getResults();
    }
  }

  default CompletableFuture<List<DbRow>> getResultsAsync(final String query, final Object... params) {
    return dispatchAsync(() -> getResults(query, params));
  }

  default Long executeInsert(final String query, final Object... params) throws SQLException {
    try (DbStatement statement = query(query)) {
      final int i = statement.executeUpdate(params);
      if (i > 0) {
        return statement.getLastInsertId();
      }
    }
    return null;
  }

  default int executeUpdate(final String query, final Object... params) throws SQLException {
    try (DbStatement statement = query(query)) {
      return statement.executeUpdate(params);
    }
  }

  default CompletableFuture<Integer> executeUpdateAsync(final String query, final Object... params) {
    return dispatchAsync(() -> executeUpdate(query, params));
  }

  default void createTransactionAsync(final TransactionCallback run) {
    createTransactionAsync(run, null, null);
  }

  default void createTransactionAsync(final TransactionCallback run, final Runnable onSuccess, final Runnable onFail) {
    dispatchAsync(() -> {
      if (!createTransaction(run)) {
        if (onFail != null) {
          onFail.run();
        }
      } else if (onSuccess != null) {
        onSuccess.run();
      }
      return null;
    });
  }

  default boolean createTransaction(final TransactionCallback run) {
    try (DbStatement stm = new DbStatement(this)) {
      try {
        stm.startTransaction();
        if (!run.apply(stm)) {
          stm.rollback();
          return false;
        } else {
          stm.commit();
          return true;
        }
      } catch (final Exception e) {
        stm.rollback();
        DB.logException(e);
      }
    } catch (final SQLException e) {
      DB.logException(e);
    }
    return false;
  }
}