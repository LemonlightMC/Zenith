package com.lemonlightmc.zenith.plugin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import com.lemonlightmc.zenith.actions.Condition;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import com.lemonlightmc.zenith.IZenithPlugin;
import com.lemonlightmc.zenith.ZenithConfig;
import com.lemonlightmc.zenith.actions.Action;
import com.lemonlightmc.zenith.actions.Actions;
import com.lemonlightmc.zenith.actions.Conditions;
import com.lemonlightmc.zenith.additive.files.FileUtils;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;
import com.lemonlightmc.zenith.apis.MessageAPI;
import com.lemonlightmc.zenith.apis.TitleAPI;
import com.lemonlightmc.zenith.apis.TitleAPI.TitleInfo;
import com.lemonlightmc.zenith.apis.bossbar.BossBar;
import com.lemonlightmc.zenith.apis.bossbar.BossBarBuilder;
import com.lemonlightmc.zenith.exceptions.ConfigParsingException;
import com.lemonlightmc.zenith.internal.LocaleSerializer;
import com.lemonlightmc.zenith.scheduler.Scheduler;

import io.leangen.geantyref.TypeToken;

public class ZenithBackend implements com.lemonlightmc.zenith.ZenithBackend {

  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private volatile IZenithPlugin instance;
  private volatile ZenithConfig config;
  private volatile Path pluginsFolder = Path.of("plugins");
  private volatile Path librariesFolder = Path.of("libraries");
  private volatile Path zenithFolder = Path.of("plugins", "zenith");
  private volatile LoggerAdapter zenithLogger;
  private volatile Locale loggerLocale;

  public ZenithBackend(final ZenithPlugin plugin) {
    if (plugin == null) {
      throw new IllegalArgumentException("plugin");
    }
    if (initialized.getAndSet(true)) {
      GlobalLogger.warn("ZenithProvider instance has already been set.");
      return;
    }
    if (!Bukkit.getServer().isPrimaryThread()) {
      initialized.set(false);
      throw new IllegalStateException("ZenithProvider can only be initialized on the primary server thread.");
    }

    instance = plugin;
    pluginsFolder = plugin.getDataFolder().toPath().getParent();
    librariesFolder = pluginsFolder.getParent().resolve("libraries");
    zenithFolder = pluginsFolder.resolve("zenith");
    FileUtils.mkdirs(zenithFolder);

    GlobalLogger.setRootLogger(instance.getLogger());
    reloadConfig();
    GlobalLogger.getRootLogger().setTransformer((msg) -> {
      if (msg == null || msg.length() == 0) {
        return null;
      }
      if (msg.startsWith("messages.")) {
        return instance().getMessageAPI().translate(msg.substring(9), loggerLocale);
      }
      return msg;
    });

    zenithLogger = GlobalLogger.getLogger("Zenith");
    zenithLogger.debug("ZenithProvider initialized (from plugin: " + plugin.getInfo().getFullName() + ")");
  }

  @Override
  public boolean hasInstance() {
    return initialized.get() && instance != null;
  }

  @Override
  public IZenithPlugin instance() {
    final IZenithPlugin current = instance;
    if (current == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return current;
  }

  @Override
  public ZenithConfig config() {
    final ZenithConfig current = config;
    if (current == null) {
      throw new IllegalStateException("ZenithProvider config has not been initialized yet.");
    }
    return current;
  }

  @Override
  public synchronized void reloadConfig() {
    config = loadZenithConfig(zenithFolder.resolve("config.yml"));
    loggerLocale = config.localization.loggerLocale;
    GlobalLogger.setLogLevelMapping(config.logging.logLevels);
    GlobalLogger.setDefaultLogLevel(config.logging.logLevels.getOrDefault("default", Level.INFO));
  }

  @Override
  public Path pluginsFolder() {
    return pluginsFolder;
  }

  @Override
  public Path librariesFolder() {
    return librariesFolder;
  }

  @Override
  public Path zenithFolder() {
    return zenithFolder;
  }

  @Override
  public Server server() {
    return instance().getServer();
  }

  @Override
  public Scheduler scheduler() {
    return instance().getScheduler();
  }

  @Override
  public PluginManager pluginManager() {
    return instance().getPluginManager();
  }

  @Override
  public MessageAPI messageAPI() {
    return instance().getMessageAPI();
  }

  @Override
  public LoggerAdapter pluginLogger() {
    return instance().getSlf4jLogger();
  }

  @Override
  public java.util.logging.Logger bukkitLogger() {
    return Bukkit.getLogger();
  }

  @Override
  public LoggerAdapter zenithLogger() {
    return requireZenithLogger();
  }

  @Override
  public LoggerAdapter zenithLogger(final String subLogger) {
    return GlobalLogger.getLogger(requireZenithLogger(), subLogger);
  }

  @Override
  public LoggerAdapter zenithLogger(final String subLogger, final Level level) {
    return GlobalLogger.getLogger(requireZenithLogger(), subLogger, level);
  }

  @Override
  public LoggerAdapter zenithLogger(final Logger logger, final String subLogger) {
    return GlobalLogger.getLogger(requireZenithLogger(), subLogger);
  }

  @Override
  public LoggerAdapter zenithLogger(final Logger logger, final String subLogger, final Level level) {
    return GlobalLogger.getLogger(requireZenithLogger(), subLogger, level);
  }

  private LoggerAdapter requireZenithLogger() {
    final LoggerAdapter current = zenithLogger;
    if (current == null) {
      throw new IllegalStateException("ZenithProvider instance has not been initialized yet.");
    }
    return current;
  }

  /**
   * Loads a Zenith configuration from the given YAML file path.
   *
   * <p>
   * If the file does not exist, defaults are copied and saved. Missing values in
   * existing files are merged with
   * defaults according to the loader configuration.
   *
   * @param path the configuration file path to load
   * @return the loaded configuration
   * @throws ConfigParsingException if the configuration cannot be loaded or
   *                                parsed
   */
  public static ZenithConfig loadZenithConfig(final Path path) {
    final ObjectMapper.Factory customFactory = ObjectMapper.factoryBuilder()
        .addNodeResolver(NodeResolver.onlyWithSetting())
        .build();

    final YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
        .path(path)
        .indent(2)
        .defaultOptions((opts) -> opts
            .shouldCopyDefaults(true)
            .serializers(build -> build
                .register(TypeToken.get(Locale.class),
                    LocaleSerializer.INSTANCE)
                .register(TypeToken.get(BossBar.class),
                    BossBarBuilder.SERIALIZER)
                .register(TypeToken.get(TitleAPI.TitleInfo.class),
                    TitleInfo.SERIALIZER)
                .register(new TypeToken<Action>() {
                }, Actions.SERIALIZER)
                .register(new TypeToken<Condition>() {
                }, Conditions.SERIALIZER)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.math.MathOperation.class),
                    com.lemonlightmc.zenith.math.MathOperation.MathOperationSerializer.INSTANCE)
                .register(TypeToken.get(java.time.Duration.class),
                    com.lemonlightmc.zenith.internal.DurationSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.math.Position.class),
                    com.lemonlightmc.zenith.math.Position.PositionSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.math.Rotation.class),
                    com.lemonlightmc.zenith.math.Rotation.RotationSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.math.Region.class),
                    com.lemonlightmc.zenith.math.Region.RegionSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.additive.version.Version.class),
                    com.lemonlightmc.zenith.internal.VersionSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.additive.math.Range.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(
                    TypeToken.get(com.lemonlightmc.zenith.additive.math.ranges.IntegerRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.additive.math.ranges.LongRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.additive.math.ranges.DoubleRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.additive.math.ranges.FloatRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.additive.math.ranges.ShortRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(TypeToken.get(
                    com.lemonlightmc.zenith.additive.math.ranges.ByteRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(
                    TypeToken.get(
                        com.lemonlightmc.zenith.additive.math.ranges.BigIntegerRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(
                    TypeToken.get(
                        com.lemonlightmc.zenith.additive.math.ranges.BigDecimalRange.class),
                    com.lemonlightmc.zenith.internal.RangeSerializer.INSTANCE)
                .register(TypeToken.get(org.bukkit.Location.class),
                    com.lemonlightmc.zenith.internal.LocationSerializer.INSTANCE)
                .register(TypeToken.get(org.bukkit.util.Vector.class),
                    com.lemonlightmc.zenith.internal.VectorSerializer.INSTANCE)
                .register(TypeToken.get(org.bukkit.NamespacedKey.class),
                    com.lemonlightmc.zenith.internal.NamespacedKeySerializer.INSTANCE)
                .register(TypeToken.get(java.util.UUID.class),
                    com.lemonlightmc.zenith.internal.UUIDSerializer.INSTANCE)
                .register(TypeToken.get(java.util.regex.Pattern.class),
                    com.lemonlightmc.zenith.internal.PatternSerializer.INSTANCE)
                .register(TypeToken.get(java.nio.file.Path.class),
                    com.lemonlightmc.zenith.internal.PathSerializer.INSTANCE)
                .register(TypeToken.get(java.net.URI.class),
                    com.lemonlightmc.zenith.internal.URISerializer.INSTANCE)
                .register(TypeToken.get(org.bukkit.Color.class),
                    com.lemonlightmc.zenith.internal.ColorSerializer.INSTANCE)
                .register(TypeToken.get(org.bukkit.World.class),
                    com.lemonlightmc.zenith.internal.WorldNameSerializer.INSTANCE)
                .register(TypeToken.get(org.bukkit.Material.class),
                    com.lemonlightmc.zenith.internal.MaterialSerializer.INSTANCE)
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
      throw new ConfigParsingException(
          "An error occurred while loading this configuration: " + e.getMessage(),
          e);
    }
  }

}
