package com.lemonlightmc.zenith.commands.arguments;

import java.time.Duration;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.loot.LootTable;
import org.bukkit.loot.LootTables;

import com.lemonlightmc.zenith.additive.time.DurationParser;
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.LookAnchor;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.math.MathOperation;

/**
 * Miscellaneous argument types for common Bukkit and utility values.
 */
public class MiscArguments {

  /**
   * Argument that resolves a Bukkit {@link World} by name.
   */
  public static class WorldArgument extends Argument<World, WorldArgument, CommandSender> {

    /** Suggestion names for currently loaded worlds. */
    public static final String[] NAMES = Bukkit.getWorlds().stream().map((final World w) -> {
      return w == null ? null : w.getName();
    }).toArray(String[]::new);

    /**
     * Creates a new world argument.
     *
     * @param name the argument name
     */
    public WorldArgument(final String name) {
      super(name, World.class, ArgumentType.WORLD);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public WorldArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a world.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed world
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public World parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Bukkit.getWorld(reader.readString()));
    }
  }

  /**
   * Argument that parses a duration value.
   */
  public static class TimeArgument extends Argument<Duration, TimeArgument, CommandSender> {

    /**
     * Creates a new time argument.
     *
     * @param name the argument name
     */
    public TimeArgument(final String name) {
      super(name, Duration.class, ArgumentType.TIME);
      withSuggestions("true", "false");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public TimeArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a {@link Duration}.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed duration
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Duration parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Duration.ofMillis(DurationParser.parse(reader.readString()));
    }
  }

  /**
   * Argument that resolves a {@link NamespacedKey} from string input.
   */
  public static class NamespacedKeyArgument extends Argument<NamespacedKey, NamespacedKeyArgument, CommandSender> {

    /**
     * Creates a new namespaced key argument.
     *
     * @param name the argument name
     */
    public NamespacedKeyArgument(final String name) {
      super(name, NamespacedKey.class, ArgumentType.NAMESPACED_KEY);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public NamespacedKeyArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a namespaced key.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed namespaced key
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public NamespacedKey parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(NamespacedKey.fromString(reader.readString()));
    }
  }

  /**
   * Argument that resolves a {@link BlockState} from block data input.
   */
  public static class BlockStateArgument extends Argument<BlockState, BlockStateArgument, CommandSender> {

    /**
     * Creates a new block state argument.
     *
     * @param name the argument name
     */
    public BlockStateArgument(final String name) {
      super(name, BlockState.class, ArgumentType.BLOCKSTATE);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public BlockStateArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a block state.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed block state
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public BlockState parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects
          .requireNonNull(Objects.requireNonNull(Bukkit.createBlockData(reader.readString())).createBlockState());
    }
  }

  /**
   * Argument that resolves {@link BlockData} from string input.
   */
  public static class BlockDataArgument extends Argument<BlockData, BlockDataArgument, CommandSender> {

    /**
     * Creates a new block data argument.
     *
     * @param name the argument name
     */
    public BlockDataArgument(final String name) {
      super(name, BlockData.class, ArgumentType.BLOCKDATA);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public BlockDataArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as block data.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed block data
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public BlockData parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Bukkit.createBlockData(reader.readString()));
    }
  }

  /**
   * Argument that resolves a {@link LootTable} by name.
   */
  public static class LootTableArgument extends Argument<LootTable, LootTableArgument, CommandSender> {

    /** Suggestion names for available loot tables. */
    public static final String[] NAMES = _mapArray(LootTables.values());

    /**
     * Creates a new loot table argument.
     *
     * @param name the argument name
     */
    public LootTableArgument(final String name) {
      super(name, LootTable.class, ArgumentType.LOOT_TABLE);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public LootTableArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a loot table.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed loot table
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public LootTable parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Objects.requireNonNull(LootTables.valueOf(reader.readString())).getLootTable());
    }
  }

  /**
   * Argument that resolves a world {@link Environment} by name.
   */
  public static class EnvironmentArgument extends Argument<Environment, EnvironmentArgument, CommandSender> {

    /** Suggestion names for available environments. */
    public static final String[] NAMES = _mapArray(Environment.values());

    /**
     * Creates a new environment argument.
     *
     * @param name the argument name
     */
    public EnvironmentArgument(final String name) {
      super(name, Environment.class, ArgumentType.ENVIRONMENT);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public EnvironmentArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a world environment.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed environment
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Environment parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Environment.valueOf(reader.readString()));
    }
  }

  /**
   * Argument that resolves a {@link GameMode} by name.
   */
  public static class GameModeArgument extends Argument<GameMode, GameModeArgument, CommandSender> {

    /** Suggestion names for available game modes. */
    public static final String[] NAMES = _mapArray(GameMode.values());

    /**
     * Creates a new game mode argument.
     *
     * @param name the argument name
     */
    public GameModeArgument(final String name) {
      super(name, GameMode.class, ArgumentType.GAME_MODE);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public GameModeArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a game mode.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed game mode
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public GameMode parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(GameMode.valueOf(reader.readString()));
    }
  }

  /**
   * Argument that resolves a {@link LookAnchor} by name.
   */
  public static class LookAnchorArgument extends Argument<LookAnchor, LookAnchorArgument, CommandSender> {

    /** Suggestion names for available look anchors. */
    public static final String[] NAMES = _mapArray(LookAnchor.values());

    /**
     * Creates a new look anchor argument.
     *
     * @param name the argument name
     */
    public LookAnchorArgument(final String name) {
      super(name, LookAnchor.class, ArgumentType.LOOK_ANCHOR);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public LookAnchorArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a look anchor.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed look anchor
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public LookAnchor parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(LookAnchor.valueOf(reader.readString()));
    }
  }

  /**
   * Argument that resolves a {@link MathOperation} by name.
   */
  public static class MathOperationArgument extends Argument<MathOperation, MathOperationArgument, CommandSender> {

    /** Suggestion names for available math operations. */
    public static final String[] NAMES = _mapArray(MathOperation.values());

    /**
     * Creates a new math operation argument.
     *
     * @param name the argument name
     */
    public MathOperationArgument(final String name) {
      super(name, MathOperation.class, ArgumentType.MATH_OPERATION);
      withSuggestions("true", "false");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public MathOperationArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a math operation.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed math operation
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public MathOperation parseArgument(final CommandSource<CommandSender> source,
        final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(MathOperation.fromString(reader.readString()));
    }
  }

  /**
   * Maps an array of values to their string representations.
   *
   * @param registry the values to map
   * @param <T> the value type
   * @return an array of string representations
   */
  public static <T> String[] _mapArray(final T[] registry) {
    final String[] arr = new String[registry.length];
    for (int i = 0; i < registry.length; i++) {
      final T t = registry[i];
      if (t != null) {
        arr[i] = t.toString();
      }
    }
    return arr;
  }
}
