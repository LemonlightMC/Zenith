package com.lemonlightmc.zenith.v2.options;

import javax.sql.DataSource;

import com.lemonlightmc.zenith.v2.DB;
import com.lemonlightmc.zenith.v2.Database;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BaseDatabase implements Database {
  private final TimingsProvider timingsProvider;
  private final DatabaseTiming sqlTiming;
  private final DatabaseOptions options;
  private ExecutorService threadPool;
  DataSource dataSource;

  public BaseDatabase(final DatabaseOptions options) {
    this.options = options;
    if (options.driverClassName != null && !options.favorDataSourceOverDriver) {
      options.dataSourceClassName = null;
    }
    if (options.driverClassName == null && options.dataSourceClassName == null) {
      throw new NullPointerException(
          "Both driverClassName and dataSourceClassName can not be null. Please load an appropriate DataSource or Driver.");
    }
    this.timingsProvider = options.timingsProvider;
    this.threadPool = options.executor;
    if (this.threadPool == null) {
      this.threadPool = new ThreadPoolExecutor(
          options.minAsyncThreads,
          options.maxAsyncThreads,
          options.asyncThreadTimeout,
          TimeUnit.SECONDS,
          new LinkedBlockingQueue<>());
      ((ThreadPoolExecutor) threadPool).allowCoreThreadTimeOut(true);
    }
    this.sqlTiming = timingsProvider.of("Database");
  }

  @Override
  public void close(final long timeout, final TimeUnit unit) {
    threadPool.shutdown();
    try {
      threadPool.awaitTermination(timeout, unit);
    } catch (final InterruptedException e) {
      DB.logException(e);
    }
    if (dataSource instanceof Closeable) {
      try {
        ((Closeable) dataSource).close();
      } catch (final IOException e) {
        DB.logException(e);
      } finally {
        dataSource = null;
      }
    }
  }

  @Override
  public synchronized <T> CompletableFuture<T> dispatchAsync(final Callable<T> task) {
    final CompletableFuture<T> future = new CompletableFuture<>();
    final Runnable run = () -> {
      try {
        future.complete(task.call());
      } catch (final Exception e) {
        future.completeExceptionally(e);
      }
    };
    if (threadPool == null) {
      run.run();
    } else {
      threadPool.submit(run);
    }
    return future;
  }

  @Override
  public DatabaseTiming timings(final String name) {
    return timingsProvider.of(options.poolName + " - " + name, sqlTiming);
  }

  public DatabaseOptions getOptions() {
    return this.options;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return dataSource != null ? dataSource.getConnection() : null;
  }
}
