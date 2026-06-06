package com.lemonlightmc.zenith.v2;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import com.lemonlightmc.zenith.messages.Logger;

public final class DB {
  private DB() {
  }

  private static Database globalDatabase;

  public synchronized static Database getGlobalDatabase() {
    return globalDatabase;
  }

  public synchronized static void setGlobalDatabase(Database database) {
    globalDatabase = database;
  }

  public synchronized static void close() {
    close(120, TimeUnit.SECONDS);
  }

  public synchronized static void close(long timeout, TimeUnit unit) {
    if (globalDatabase != null) {
      globalDatabase.close(timeout, unit);
      globalDatabase = null;
    }
  }

  public static DbRow getFirstRow(String query, Object... params) throws SQLException {
    return globalDatabase.getFirstRow(query, params);
  }

  public static CompletableFuture<DbRow> getFirstRowAsync(String query, Object... params) {
    return globalDatabase.getFirstRowAsync(query, params);
  }

  public static <T> T getFirstColumn(String query, Object... params) throws SQLException {
    return globalDatabase.getFirstColumn(query, params);
  }

  public static <T> CompletableFuture<T> getFirstColumnAsync(String query, Object... params) {
    return globalDatabase.getFirstColumnAsync(query, params);
  }

  public static <T> List<T> getFirstColumnResults(String query, Object... params) throws SQLException {
    return globalDatabase.getFirstColumnResults(query, params);
  }

  public static <T> CompletableFuture<List<T>> getFirstColumnResultsAsync(String query,
      Object... params) {
    return globalDatabase.getFirstColumnResultsAsync(query, params);
  }

  public static List<DbRow> getResults(String query, Object... params) throws SQLException {
    return globalDatabase.getResults(query, params);
  }

  public static CompletableFuture<List<DbRow>> getResultsAsync(String query, Object... params) {
    return globalDatabase.getResultsAsync(query, params);
  }

  public static Long executeInsert(String query, Object... params) throws SQLException {
    return globalDatabase.executeInsert(query, params);
  }

  public static int executeUpdate(String query, Object... params) throws SQLException {
    return globalDatabase.executeUpdate(query, params);
  }

  public static CompletableFuture<Integer> executeUpdateAsync(String query, final Object... params) {
    return globalDatabase.executeUpdateAsync(query, params);
  }

  public synchronized static <T> CompletableFuture<T> dispatchAsync(Callable<T> task) {
    return globalDatabase.dispatchAsync(task);
  }

  public static void createTransactionAsync(TransactionCallback run) {
    globalDatabase.createTransactionAsync(run, null, null);
  }

  public static void createTransactionAsync(TransactionCallback run, Runnable onSuccess, Runnable onFail) {
    globalDatabase.createTransactionAsync(run, onSuccess, onFail);
  }

  public static boolean createTransaction(TransactionCallback run) {
    return globalDatabase.createTransaction(run);
  }

  public static void logException(Exception e) {
    if (e == null) {
      return;
    }
    Logger.warn(e.getMessage());
    e.printStackTrace();
  }

  public static void logException(String message, Exception e) {
    Logger.warn(message);
    if (e != null) {
      e.printStackTrace();
    }
  }
}