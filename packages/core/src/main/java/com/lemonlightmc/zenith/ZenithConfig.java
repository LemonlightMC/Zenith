package com.lemonlightmc.zenith;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;

import org.slf4j.event.Level;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import org.spongepowered.configurate.serialize.TypeSerializer;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;
import java.lang.reflect.Type;

import com.lemonlightmc.zenith.additive.StringUtils;
import com.lemonlightmc.zenith.exceptions.ConfigParsingException;

import io.leangen.geantyref.TypeToken;

/**
 * The global Zenith configuration, mapped directly from the {@code config.yml}
 * file using
 * Configurate's {@link ObjectMapper}. Every top-level section of the YAML file
 * corresponds to a
 * nested {@link ConfigSerializable} type, and each field below mirrors a node
 * from the file.
 *
 * <p>
 * Fields are declared {@code public} so that consumers can read them directly
 * (e.g.
 * {@code ZenithProvider.config().localization.enabled}).
 *
 * <p>
 * Fields that are missing from an existing config file will be filled with the
 * default values
 * declared on the fields, and (via {@code shouldCopyDefaults}) written back to
 * disk so newly
 * introduced options are always present.
 */
@ConfigSerializable
public class ZenithConfig {

  /** Global Zenith localization settings. */
  @Setting("localization")
  @Comment("Global Zenith localization settings")
  public Localization localization;

  /** Logging Settings */
  @Setting("logging")
  @Comment("Logging Settings")
  public Logging logging;

  /**
   * Formatting strings - customize date, money, location and pagination displays.
   */
  @Setting("formatters")
  @Comment("Formatting Strings - Customize date, money, location displays\nWARNING: Only edit if you know what you're doing!")
  public Formatters formatters;

  /** Update checker settings. */
  @Setting("updatechecker")
  @Comment("Update checker settings")
  public UpdateChecker updateChecker;

  /** STORAGE SETTINGS - controls which storage method is used to store data. */
  @Setting("database")
  @Comment("STORAGE SETTINGS\nControls which storage method will be used to store data.")
  public Database database;

  /**
   * How often the cache is flushed to the database, in seconds. Lower values mean
   * more frequent
   * saves (higher CPU usage) while higher values have a slight crash-risk.
   */
  @Setting("cache-interval")
  @Comment("Cache Flush Interval (seconds)\nHow often cached data is written to database\nLower = frequent saves, more CPU usage | Higher = better performance, slight crash risk\nRecommendations: <20 players: 30s | 20-50: 60s | 50-100: 120-180s | 100+: 300s")
  public int cacheInterval = 30;

  public static ZenithConfig from(final Path path) {
    final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
        .addNodeResolver(NodeResolver.onlyWithSetting())
        .build();

    final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
        .path(path)
        .indent(2)
        .defaultOptions((opts) -> opts
            .shouldCopyDefaults(true)
            .serializers(build -> build
                .register(TypeToken.get(Locale.class), new LocaleSerializer())
                .registerAnnotatedObjects(customFactory)))
        .build();
    final CommentedConfigurationNode root;
    try {
      root = loader.load();
      final ZenithConfig config = root.get(ZenithConfig.class);
      if (Files.notExists(path)) {
        loader.save(root);
      }
      return config;
    } catch (final Exception e) {
      throw new ConfigParsingException("An error occurred while loading this configuration: " + e.getMessage(), e);
    }
  }

  /**
   * Serializes {@link Locale} objects to and from the config file as
   * BCP-47 language
   * tags (e.g. {@code en}, {@code en-US}). Configurate 4.2.0 ships no built-in
   * {@code Locale}
   * serializer, so one is registered explicitly in {@link #from(Path)}.
   */
  private static final class LocaleSerializer implements TypeSerializer<Locale> {
    @Override
    public Locale deserialize(final Type type, final ConfigurationNode node) {
      final String value = node.getString();
      if (value == null || value.isEmpty()) {
        return Locale.ENGLISH;
      }
      return StringUtils.parseLocale(value);
    }

    @Override
    public void serialize(final Type type, final Locale obj, final ConfigurationNode node)
        throws org.spongepowered.configurate.serialize.SerializationException {
      node.set(obj.toLanguageTag());
    }
  }

  /**
   * The localization section. Controls how (and in which language) messages are
   * translated.
   */
  @ConfigSerializable
  public static class Localization {
    /**
     * Whether to use the localization system to translate messages into different
     * languages. If
     * disabled, all messages will be in English.
     */
    @Setting("enabled")
    @Comment("use the localization system to translate messages into different languages\nif you disable this, all messages will be in English")
    public boolean enabled = true;

    /** The default language for the server. */
    @Setting("default-locale")
    @Comment("The default language for the server")
    public Locale defaultLocale = Locale.ENGLISH;

    /**
     * The language used for logging messages. Independent of the default locale for
     * players.
     */
    @Setting("logger-locale")
    @Comment("The language used for logging messages. This is independent of the default locale for players.")
    public Locale loggerLocale = Locale.ENGLISH;

    /**
     * Whether to use the player's locale if available, otherwise fall back to the
     * default language.
     */
    @Setting("use-player-locale")
    @Comment("use the player's locale if available, otherwise fall back to the default language")
    public boolean usePlayerLocale = true;

    /**
     * The list of allowed locales. If a player's locale is not in this list, the
     * default locale is used.
     */
    @Setting("allowed-locales")
    @Comment("The list of allowed locales for the server\nIf a player's locale is not in this list, the default locale will be used")
    public java.util.List<Locale> allowedLocales = java.util.List
        .of("en", "es", "fr", "de", "it", "pt", "ru", "zh", "ja")
        .stream().map(StringUtils::parseLocale).toList();
  }

  /**
   * The logging section. Enables/disables logging and controls its format and
   * per-component levels.
   */
  @ConfigSerializable
  public static class Logging {
    /**
     * Whether to enable logging for Zenith. If disabled, no logs will be written to
     * console or files.
     */
    @Setting("enabled")
    @Comment("Whether to enable logging for Zenith. If disabled, no logs will be written to the console or log files.")
    public boolean enabled = true;

    /** The format applied to each log message. */
    @Setting("format")
    @Comment("The format of the log message")
    public Format format = new Format();

    /** The default log levels for different components of Zenith and the Server. */
    @Setting("loglevels")
    @Comment("the default log levels for different components of Zenith and the Server")
    public Map<String, Level> logLevels = Map.of(
        "default", Level.WARN,
        "core", Level.WARN,
        "database", Level.WARN,
        "commands", Level.WARN,
        "dependency", Level.INFO,
        "updater", Level.INFO,
        "plugin", Level.INFO);

    /** The message formatting options. */
    @ConfigSerializable
    public static class Format {
      @Setting("showLoggerName")
      public boolean showLoggerName = true;
      @Setting("showThreadContext")
      public boolean showThreadContext = true;
      @Setting("showDateTime")
      public boolean showDateTime = true;
      @Setting("dateTimeFormat")
      public String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    }
  }

  /**
   * Formatting strings used to customize date, money, location and pagination
   * displays.
   */
  @ConfigSerializable
  public static class Formatters {
    @Setting("datetime-format")
    public String datetimeFormat = "dd/M/yyyy hh:mm a";
    @Setting("date")
    public String date = "dd/M/yyyy";
    @Setting("time")
    public String time = "hh:mm a";
    @Setting("timezone")
    public String timezone = "GMT";
    @Setting("duration-long")
    @Comment("Removed \"{s} seconds\", you can add it")
    public String durationLong = "{d} days, {h} hours, {m} minutes";
    @Setting("duration-short")
    @Comment("Removed \"{s} seconds\", you can add it")
    public String durationShort = "{d}d {h}h {m}m";
    @Setting("ago-days")
    public String agoDays = "{v} days";
    @Setting("ago-hours")
    public String agoHours = "{v} hours";
    @Setting("ago-minutes")
    public String agoMinutes = "{v} minutes";
    @Setting("ago-seconds")
    public String agoSeconds = "{v} seconds";
    @Setting("balance")
    public String balance = "${balance}";
    @Setting("location")
    public String location = "&6{world} &7(X: &6{x} &7Y: &6{y} &7Z: &6{z}&7)";
    @Setting("chunk")
    public String chunk = "&6{world} &7(X: &6{x} &7Z: &6{z}&7)";
    @Setting("gui-pagination-title")
    public String guiPaginationTitle = "&r{title} &r| &3{current-page}&r/&c{total-pages}";
  }

  /** Update checker settings. */
  @ConfigSerializable
  public static class UpdateChecker {
    /**
     * Whether to check for updates periodically while the server is running.
     *
     * <p>
     * Note: the YAML key in the shipped config is misspelled as {@code enbled} to
     * preserve
     * compatibility with existing files.
     */
    @Setting("enbled")
    @Comment("Wether to check for updates periodically while the server is running")
    public boolean enabled = true;
    /**
     * How often to poll for updates (in hours). Set to 0 to disable the update
     * checker.
     */
    @Setting("interval")
    @Comment("How often to poll for updates (in hours). Set to 0 to disable the update checker")
    public int interval = 24;
    /**
     * The type of notification to send when an update is found. Options: \"full\",
     * \"minimal\", \"console\".
     */
    @Setting("notification-type")
    @Comment("The type of notification to send when an update is found. Options: \"full\", \"minimal\", \"console\"")
    public String notificationType = "full";
    /**
     * Whether to notify players with the \"zenith.update\" permission when an
     * update is found.
     */
    @Setting("notify-on-join")
    @Comment("Whether to notify players with the \"zenith.update\" permission when an update is found")
    public boolean notifyOnJoin = true;
    /** Only notify once after an update is found. */
    @Setting("notify-once")
    @Comment("Only notify once after an update is found")
    public boolean notifyOnce = false;
  }

  /** Storage settings. Controls which storage method is used to store data. */
  @ConfigSerializable
  public static class Database {
    /** The storage method. See the config file for the list of possible options. */
    @Setting("storage-method")
    @Comment("- Possible options:\n"
        + "  |  Remote databases - require connection information to be configured below\n"
        + "  |=> MySQL\n  |=> MariaDB (preferred over MySQL)\n  |=> PostgreSQL\n  |=> MongoDB\n\n"
        + "  |  Flatfile/local database - don't require any extra configuration\n"
        + "  |=> H2 (preferred over SQLite)\n  |=> SQLite\n\n"
        + "  |  Readable & editable text files - don't require any extra configuration\n"
        + "  |=> YAML (.yml files)\n  |=> JSON (.json files)\n  |=> HOCON (.conf files)\n  |=> TOML (.toml files)\n\n"
        + "- A H2 database is the default option.\n"
        + "- If you want to edit data manually in \"traditional\" storage files, we suggest using YAML.")
    public String storageMethod = "h2";
    /**
     * The address and port of the database (standard DB-engine port is used by
     * default).
     */
    @Setting("address")
    @Comment("Define the address and port for the database.\n- The standard DB engine port is used by default (MySQL: 3306, PostgreSQL: 5432, MongoDB: 27017)\n- Specify as \"host:port\" if differs")
    public String address = "localhost";
    /** The name of the database to store data in. */
    @Setting("database")
    @Comment("The name of the database to store data in.\n- This must be created already. Don't worry about this setting if you're using MongoDB.")
    public String database = "data";
    /** Credentials for the database. */
    @Setting("username")
    @Comment("Credentials for the database.")
    public String username = "root";
    /** Credentials for the database. */
    @Setting("password")
    public String password = "";
    /** The prefix for all SQL tables (only applies to remote SQL storage types). */
    @Setting("table-prefix")
    @Comment("The prefix for all SQL tables.\n\n- This only applies for remote SQL storage types (MySQL, MariaDB, etc).\n- Change this if you want to use different tables for different servers.")
    public String tablePrefix = "zenith_";
    /** The MySQL connection pool settings. */
    @Setting("pool-settings")
    @Comment("These settings apply to the MySQL connection pool.\n- The default values will be suitable for the majority of users.\n- Do not change these settings unless you know what you're doing!")
    public PoolSettings poolSettings = new PoolSettings();
    /** Extra connection properties (e.g. utf8 encoding, SSL settings). */
    @Setting("properties")
    @Comment("This setting allows you to define extra properties for connections.\n\nBy default, the following options are set to enable utf8 encoding.\nYou can also use this section to disable SSL connections, by uncommenting the 'useSSL' and 'verifyServerCertificate' options.")
    public Map<String, String> properties = Map.of(
        "useUnicode", "true",
        "characterEncoding", "utf8",
        "serverTimezone", "UTC",
        "allowPublicKeyRetrieval", "true");
    /** The prefix to use for all MongoDB collections. */
    @Setting("mongodb-collection-prefix")
    @Comment("The prefix to use for all MongoDB collections.\n\n- This only applies for the MongoDB storage type.\n- Change this if you want to use different collections for different servers. The default is no prefix.")
    public String mongodbCollectionPrefix = "zenith";
    /** The connection string URI to use to connect to the MongoDB instance. */
    @Setting("mongodb-connection-uri")
    @Comment("The connection string URI to use to connect to the MongoDB instance.\n\n- When configured, this setting will override anything defined in the address, database, username or password fields above.\n- If you have a connection string that starts with 'mongodb://' or 'mongodb+srv://', enter it below.")
    public String mongodbConnectionUri = "mongodb://localhost:27017";

    /** The MySQL connection pool settings. */
    @ConfigSerializable
    public static class PoolSettings {
      @Setting("maximum-pool-size")
      @Comment("Sets the maximum size of the MySQL connection pool.\n- Basically this value will determine the maximum number of actual connections to the database backend.")
      public int maximumPoolSize = 10;
      @Setting("minimum-idle")
      @Comment("Sets the minimum number of idle connections that the pool will try to maintain.\n- For maximum performance and responsiveness to spike demands, it is recommended to not set this value and instead allow the pool to act as a fixed size connection pool.")
      public int minimumIdle = 10;
      @Setting("maximum-lifetime")
      @Comment("This setting controls the maximum lifetime of a connection in the pool in milliseconds.\n- The value should be at least 30 seconds less than any database or infrastructure imposed connection time limit.\n- Default: 1800000 (30 minutes)")
      public int maximumLifetime = 1800000;
      @Setting("keepalive-time")
      @Comment("This setting controls how frequently the pool will 'ping' a connection in order to prevent it from being timed out, measured in milliseconds.\n- The value should be less than maximum-lifetime and greater than 30000 (30 seconds).\n- Setting the value to zero will disable the keepalive functionality.")
      public int keepaliveTime = 0;
      @Setting("connection-timeout")
      @Comment("This setting controls the maximum number of milliseconds that the plugin will wait for a connection from the pool, before timing out.\n- Default: 5000 (5 seconds)")
      public int connectionTimeout = 5000;
    }
  }
}