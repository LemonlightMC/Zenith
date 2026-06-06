package com.lemonlightmc.zenith.v2.options;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;

public class DatabaseOptions {
  private static final DatabaseTiming NULL_TIMING = new NullDatabaseTiming();

  protected String dsn;
  protected String driverClassName;
  protected String dataSourceClassName;
  protected String defaultIsolationLevel;

  protected boolean favorDataSourceOverDriver = true;
  protected String poolName = "DB";
  protected boolean useOptimizations = true;

  protected int minAsyncThreads = Math.min(Runtime.getRuntime().availableProcessors(), 2);
  protected int maxAsyncThreads = Runtime.getRuntime().availableProcessors();
  protected int asyncThreadTimeout = 60;
  protected TimingsProvider timingsProvider = (name, parent) -> NULL_TIMING;

  protected String user;
  protected String password;
  protected ExecutorService executor;

  public DatabaseOptions() {
  }

  private DatabaseOptions(String dsn, String driverClassName, String dataSourceClassName, String defaultIsolationLevel,
      boolean favorDataSourceOverDriver, String poolName, boolean useOptimizations,
      int minAsyncThreads, int maxAsyncThreads, int asyncThreadTimeout,
      TimingsProvider timingsProvider,
      String user, String password, ExecutorService executor) {
    this.dsn = Objects.requireNonNull(dsn, "dsn");
    this.driverClassName = Objects.requireNonNull(driverClassName, "driverClassName");
    this.dataSourceClassName = dataSourceClassName;
    this.defaultIsolationLevel = defaultIsolationLevel;
    this.favorDataSourceOverDriver = favorDataSourceOverDriver;
    this.poolName = poolName;
    this.useOptimizations = useOptimizations;
    this.minAsyncThreads = minAsyncThreads;
    this.maxAsyncThreads = maxAsyncThreads;
    this.asyncThreadTimeout = asyncThreadTimeout;
    this.timingsProvider = timingsProvider != null ? timingsProvider : (name, parent) -> NULL_TIMING;
    this.user = user;
    this.password = password;
    this.executor = executor;
  }

  public static DatabaseOptionsBuilder builder() {
    return new DatabaseOptionsBuilder();
  }

  public DatabaseOptionsBuilder toBuilder() {
    return new DatabaseOptionsBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DatabaseOptions that = (DatabaseOptions) o;
    return favorDataSourceOverDriver == that.favorDataSourceOverDriver &&
        useOptimizations == that.useOptimizations &&
        minAsyncThreads == that.minAsyncThreads &&
        maxAsyncThreads == that.maxAsyncThreads &&
        asyncThreadTimeout == that.asyncThreadTimeout &&
        Objects.equals(dsn, that.dsn) &&
        Objects.equals(driverClassName, that.driverClassName) &&
        Objects.equals(dataSourceClassName, that.dataSourceClassName) &&
        Objects.equals(defaultIsolationLevel, that.defaultIsolationLevel) &&
        Objects.equals(poolName, that.poolName) &&
        Objects.equals(timingsProvider, that.timingsProvider) &&
        Objects.equals(user, that.user) &&
        Objects.equals(password, that.password) &&
        Objects.equals(executor, that.executor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(dsn, driverClassName, dataSourceClassName, defaultIsolationLevel, favorDataSourceOverDriver,
        poolName, useOptimizations, minAsyncThreads, maxAsyncThreads, asyncThreadTimeout, timingsProvider, user,
        password,
        executor);
  }

  @Override
  public String toString() {
    return "DatabaseOptions{" +
        "dsn='" + dsn + '\'' +
        ", driverClassName='" + driverClassName + '\'' +
        ", dataSourceClassName='" + dataSourceClassName + '\'' +
        ", defaultIsolationLevel='" + defaultIsolationLevel + '\'' +
        ", favorDataSourceOverDriver=" + favorDataSourceOverDriver +
        ", poolName='" + poolName + '\'' +
        ", useOptimizations=" + useOptimizations +
        ", minAsyncThreads=" + minAsyncThreads +
        ", maxAsyncThreads=" + maxAsyncThreads +
        ", asyncThreadTimeout=" + asyncThreadTimeout +
        ", user='" + user + '\'' +
        ", password='" + password + '\'' +
        ", executor=" + executor +
        '}';
  }

  public static class DatabaseOptionsBuilder {
    private String dsn;
    private String driverClassName;
    private String dataSourceClassName;
    private String defaultIsolationLevel;

    private boolean favorDataSourceOverDriver = true;
    private String poolName = "DB";
    private boolean useOptimizations = true;

    private int minAsyncThreads = Math.min(Runtime.getRuntime().availableProcessors(), 2);
    private int maxAsyncThreads = Runtime.getRuntime().availableProcessors();
    private int asyncThreadTimeout = 60;
    private TimingsProvider timingsProvider = (name, parent) -> NULL_TIMING;

    private String user;
    private String password;
    private ExecutorService executor;

    public DatabaseOptionsBuilder() {
    }

    private DatabaseOptionsBuilder(DatabaseOptions base) {
      this.dsn = base.dsn;
      this.driverClassName = base.driverClassName;
      this.dataSourceClassName = base.dataSourceClassName;
      this.defaultIsolationLevel = base.defaultIsolationLevel;
      this.favorDataSourceOverDriver = base.favorDataSourceOverDriver;
      this.poolName = base.poolName;
      this.useOptimizations = base.useOptimizations;
      this.minAsyncThreads = base.minAsyncThreads;
      this.maxAsyncThreads = base.maxAsyncThreads;
      this.asyncThreadTimeout = base.asyncThreadTimeout;
      this.timingsProvider = base.timingsProvider;
      this.user = base.user;
      this.password = base.password;
      this.executor = base.executor;
    }

    public DatabaseOptionsBuilder dsn(String dsn) {
      this.dsn = dsn;
      return this;
    }

    public DatabaseOptionsBuilder defaultIsolationLevel(String defaultIsolationLevel) {
      this.defaultIsolationLevel = defaultIsolationLevel;
      return this;
    }

    public DatabaseOptionsBuilder favorDataSourceOverDriver(boolean favor) {
      this.favorDataSourceOverDriver = favor;
      return this;
    }

    public DatabaseOptionsBuilder poolName(String poolName) {
      this.poolName = poolName;
      return this;
    }

    public DatabaseOptionsBuilder useOptimizations(boolean useOptimizations) {
      this.useOptimizations = useOptimizations;
      return this;
    }

    public DatabaseOptionsBuilder minAsyncThreads(int minAsyncThreads) {
      this.minAsyncThreads = minAsyncThreads;
      return this;
    }

    public DatabaseOptionsBuilder maxAsyncThreads(int maxAsyncThreads) {
      this.maxAsyncThreads = maxAsyncThreads;
      return this;
    }

    public DatabaseOptionsBuilder asyncThreadTimeout(int asyncThreadTimeout) {
      this.asyncThreadTimeout = asyncThreadTimeout;
      return this;
    }

    public DatabaseOptionsBuilder timingsProvider(TimingsProvider timingsProvider) {
      this.timingsProvider = timingsProvider;
      return this;
    }

    public DatabaseOptionsBuilder user(String user) {
      this.user = user;
      return this;
    }

    public DatabaseOptionsBuilder password(String password) {
      this.password = password;
      return this;
    }

    public DatabaseOptionsBuilder executor(ExecutorService executor) {
      this.executor = executor;
      return this;
    }

    public DatabaseOptionsBuilder mysql(String user, String password, String db, String hostAndPort) {
      if (hostAndPort == null) {
        hostAndPort = "localhost:3306";
      }
      this.user = user;
      this.password = password;

      if (this.defaultIsolationLevel == null)
        this.defaultIsolationLevel = "TRANSACTION_READ_COMMITTED";

      if (this.dataSourceClassName == null)
        tryDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
      if (this.dataSourceClassName == null)
        tryDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
      if (this.dataSourceClassName == null)
        tryDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");

      if (this.driverClassName == null)
        tryDriverClassName("org.mariadb.jdbc.Driver");
      if (this.driverClassName == null)
        tryDriverClassName("com.mysql.cj.jdbc.Driver");
      if (this.driverClassName == null)
        tryDriverClassName("com.mysql.jdbc.Driver");

      this.dsn = "mysql://" + hostAndPort + "/" + db;
      return this;
    }

    public DatabaseOptionsBuilder sqlite(File file) {
      return this.sqlite(file.getAbsoluteFile());
    }

    public DatabaseOptionsBuilder sqlite(String fileName) {
      if (this.defaultIsolationLevel == null)
        this.defaultIsolationLevel = "TRANSACTION_SERIALIZABLE";

      if (this.dataSourceClassName == null)
        tryDataSourceClassName("org.sqlite.SQLiteDataSource");

      if (this.driverClassName == null)
        tryDriverClassName("org.sqlite.JDBC");

      this.dsn = "sqlite:" + fileName;
      return this;
    }

    public DatabaseOptionsBuilder postgresSQL(String user, String password, String db, String hostAndPort) {
      if (hostAndPort == null) {
        hostAndPort = "localhost:5432";
      }

      this.user = user;
      this.password = password;

      // if (dataSourceClassName == null)
      // tryDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
      if (dataSourceClassName == null)
        tryDataSourceClassName("org.postgresql.jdbc3.Jdbc3PoolingDataSource");
      if (driverClassName == null)
        tryDriverClassName("org.postgresql.Driver");

      if (defaultIsolationLevel == null)
        defaultIsolationLevel = "TRANSACTION_READ_COMMITTED";
      this.dsn = "postgresql://" + hostAndPort + "/" + db;
      return this;
    }

    public DatabaseOptionsBuilder h2(File file) {
      return this.h2(file.getAbsoluteFile());
    }

    public DatabaseOptionsBuilder h2(String fileName) {
      if (defaultIsolationLevel == null)
        defaultIsolationLevel = "TRANSACTION_SERIALIZABLE";
      if (dataSourceClassName == null)
        tryDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
      if (driverClassName == null)
        tryDriverClassName("org.h2.Driver");
      this.dsn = "h2:file:" + fileName + ";mode=MySQL;DB_CLOSE_ON_EXIT=TRUE;FILE_LOCK=NO";
      return this;
    }

    private void tryDriverClassName(String className) {
      if (isValidDriverClassName(className)) {
        this.driverClassName = className;
      }
    }

    private void tryDataSourceClassName(String className) {
      if (isValidDataSourceClassName(className)) {
        this.driverClassName = className;
      }
    }

    public DatabaseOptionsBuilder driverClassName(String className) {
      if (!isValidDriverClassName(className)) {
        throw new IllegalArgumentException("Invalid DB Driver Class Name");
      }
      this.driverClassName = className;
      return this;
    }

    public DatabaseOptionsBuilder dataSourceClassName(String className) {
      if (!isValidDataSourceClassName(className)) {
        throw new IllegalArgumentException("Invalid DB DataSource Class Name");
      }
      this.dataSourceClassName = className;
      return this;
    }

    public DatabaseOptions build() {
      return new DatabaseOptions(
          Objects.requireNonNull(dsn, "dsn"),
          Objects.requireNonNull(driverClassName, "driverClassName"),
          dataSourceClassName,
          defaultIsolationLevel,
          favorDataSourceOverDriver,
          poolName,
          useOptimizations,
          minAsyncThreads,
          maxAsyncThreads,
          asyncThreadTimeout,
          timingsProvider,
          user,
          password,
          executor);
    }

  }

  private static boolean isValidDriverClassName(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }

  private static boolean isValidDataSourceClassName(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (Exception ignored) {
      return false;
    }
  }
}