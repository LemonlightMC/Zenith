package com.lemonlightmc.zenith.v2.options;

import java.util.Map;
import java.util.Objects;

public class PooledDatabaseOptions {
  protected int minIdleConnections = 3;
  protected int maxConnections = 5;
  protected int connectionTimeout = 30 * 1000;
  protected int leakDetectionThreshold = 30 * 1000;

  protected Map<String, Object> dataSourceProperties;
  protected DatabaseOptions options;

  public PooledDatabaseOptions() {
  }

  private PooledDatabaseOptions(final int minIdleConnections, final int maxConnections, final int connectionTimeout,
      final int leakDetectionThreshold,
      final Map<String, Object> dataSourceProperties,
      final DatabaseOptions options) {
    this.minIdleConnections = minIdleConnections;
    this.maxConnections = maxConnections;
    this.leakDetectionThreshold = leakDetectionThreshold;
    this.maxConnections = maxConnections;
    this.connectionTimeout = connectionTimeout;
    this.dataSourceProperties = dataSourceProperties;
    this.options = options;
  }

  public static PooledDatabaseOptionsBuilder builder() {
    return new PooledDatabaseOptionsBuilder();
  }

  public static PooledDatabaseOptionsBuilder builder(final DatabaseOptions options) {
    return new PooledDatabaseOptionsBuilder(options);
  }

  public PooledDatabaseOptionsBuilder toBuilder() {
    return new PooledDatabaseOptionsBuilder(this);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    final PooledDatabaseOptions that = (PooledDatabaseOptions) o;
    return minIdleConnections == that.minIdleConnections &&
        maxConnections == that.maxConnections &&
        Objects.equals(dataSourceProperties, that.dataSourceProperties) &&
        Objects.equals(options, that.options);
  }

  @Override
  public int hashCode() {
    return Objects.hash(minIdleConnections, maxConnections, dataSourceProperties, options);
  }

  @Override
  public String toString() {
    return "PooledDatabaseOptions{" +
        "minIdleConnections=" + minIdleConnections +
        ", maxConnections=" + maxConnections +
        ", dataSourceProperties=" + dataSourceProperties +
        ", options=" + options +
        '}';
  }

  public static class PooledDatabaseOptionsBuilder {
    private int minIdleConnections = 3;
    private int maxConnections = 5;
    private int connectionTimeout = 30 * 1000;
    private int leakDetectionThreshold = 30 * 1000;
    private Map<String, Object> dataSourceProperties;
    private DatabaseOptions options;

    public PooledDatabaseOptionsBuilder() {
    }

    public PooledDatabaseOptionsBuilder(DatabaseOptions options) {
      if (options == null) {
        return;
      }
      this.options = options;
    }

    private PooledDatabaseOptionsBuilder(final PooledDatabaseOptions base) {
      if (base == null) {
        return;
      }
      this.minIdleConnections = base.minIdleConnections;
      this.maxConnections = base.maxConnections;
      this.dataSourceProperties = base.dataSourceProperties;
      this.options = base.options;
    }

    public PooledDatabaseOptionsBuilder minIdleConnections(final int minIdleConnections) {
      this.minIdleConnections = minIdleConnections;
      return this;
    }

    public PooledDatabaseOptionsBuilder maxConnections(final int maxConnections) {
      this.maxConnections = maxConnections;
      return this;
    }

    public PooledDatabaseOptionsBuilder dataSourceProperties(final Map<String, Object> dataSourceProperties) {
      this.dataSourceProperties = dataSourceProperties;
      return this;
    }

    public PooledDatabaseOptionsBuilder options(final DatabaseOptions options) {
      this.options = options;
      return this;
    }

    public PooledDatabaseOptionsBuilder connectionTimeout(final int connectionTimeout) {
      this.connectionTimeout = connectionTimeout;
      return this;
    }

    public PooledDatabaseOptionsBuilder leakDetectionThreshold(final int leakDetectionThreshold) {
      this.leakDetectionThreshold = leakDetectionThreshold;
      return this;
    }

    public PooledDatabaseOptions build() {
      return new PooledDatabaseOptions(minIdleConnections, maxConnections, connectionTimeout, leakDetectionThreshold,
          dataSourceProperties,
          Objects.requireNonNull(options, "options"));
    }

    public HikariPooledDatabase createHikariDatabase() {
      return new HikariPooledDatabase(this.build());
    }
  }
}