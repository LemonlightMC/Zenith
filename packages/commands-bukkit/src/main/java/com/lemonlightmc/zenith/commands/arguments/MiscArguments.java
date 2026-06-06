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
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.LookAnchor;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.math.MathOperation;
import com.lemonlightmc.zenith.time.DurationParser;

public class MiscArguments {

  public static class WorldArgument extends Argument<World, WorldArgument, CommandSender> {

    public static final String[] NAMES = Bukkit.getWorlds().stream().map((final World w) -> {
      return w == null ? null : w.getName();
    }).toArray(String[]::new);

    public WorldArgument(final String name) {
      super(name, World.class, ArgumentType.WORLD);
      withSuggestions(NAMES);
    }

    @Override
    public WorldArgument getInstance() {
      return this;
    }

    @Override
    public World parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Bukkit.getWorld(reader.readString()));
    }
  }

  public static class TimeArgument extends Argument<Duration, TimeArgument, CommandSender> {
    public TimeArgument(final String name) {
      super(name, Duration.class, ArgumentType.TIME);
      withSuggestions("true", "false");
    }

    @Override
    public TimeArgument getInstance() {
      return this;
    }

    @Override
    public Duration parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Duration.ofMillis(DurationParser.parse(reader.readString()));
    }
  }

  public static class NamespacedKeyArgument extends Argument<NamespacedKey, NamespacedKeyArgument, CommandSender> {

    public NamespacedKeyArgument(final String name) {
      super(name, NamespacedKey.class, ArgumentType.NAMESPACED_KEY);
    }

    @Override
    public NamespacedKeyArgument getInstance() {
      return this;
    }

    @Override
    public NamespacedKey parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(NamespacedKey.fromString(reader.readString()));
    }
  }

  public static class BlockStateArgument extends Argument<BlockState, BlockStateArgument, CommandSender> {

    public BlockStateArgument(final String name) {
      super(name, BlockState.class, ArgumentType.BLOCKSTATE);
    }

    @Override
    public BlockStateArgument getInstance() {
      return this;
    }

    @Override
    public BlockState parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects
          .requireNonNull(Objects.requireNonNull(Bukkit.createBlockData(reader.readString())).createBlockState());
    }
  }

  public static class BlockDataArgument extends Argument<BlockData, BlockDataArgument, CommandSender> {

    public BlockDataArgument(final String name) {
      super(name, BlockData.class, ArgumentType.BLOCKDATA);
    }

    @Override
    public BlockDataArgument getInstance() {
      return this;
    }

    @Override
    public BlockData parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Bukkit.createBlockData(reader.readString()));
    }
  }

  public static class LootTableArgument extends Argument<LootTable, LootTableArgument, CommandSender> {

    public static final String[] NAMES = _mapArray(LootTables.values());

    public LootTableArgument(final String name) {
      super(name, LootTable.class, ArgumentType.LOOT_TABLE);
      withSuggestions(NAMES);
    }

    @Override
    public LootTableArgument getInstance() {
      return this;
    }

    @Override
    public LootTable parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Objects.requireNonNull(LootTables.valueOf(reader.readString())).getLootTable());
    }
  }

  public static class EnvironmentArgument extends Argument<Environment, EnvironmentArgument, CommandSender> {

    public static final String[] NAMES = _mapArray(Environment.values());

    public EnvironmentArgument(final String name) {
      super(name, Environment.class, ArgumentType.ENVIRONMENT);
      withSuggestions(NAMES);
    }

    @Override
    public EnvironmentArgument getInstance() {
      return this;
    }

    @Override
    public Environment parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Environment.valueOf(reader.readString()));
    }
  }

  public static class GameModeArgument extends Argument<GameMode, GameModeArgument, CommandSender> {

    public static final String[] NAMES = _mapArray(GameMode.values());

    public GameModeArgument(final String name) {
      super(name, GameMode.class, ArgumentType.GAME_MODE);
      withSuggestions(NAMES);
    }

    @Override
    public GameModeArgument getInstance() {
      return this;
    }

    @Override
    public GameMode parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(GameMode.valueOf(reader.readString()));
    }
  }

  public static class LookAnchorArgument extends Argument<LookAnchor, LookAnchorArgument, CommandSender> {

    public static final String[] NAMES = _mapArray(LookAnchor.values());

    public LookAnchorArgument(final String name) {
      super(name, LookAnchor.class, ArgumentType.LOOK_ANCHOR);
      withSuggestions(NAMES);
    }

    @Override
    public LookAnchorArgument getInstance() {
      return this;
    }

    @Override
    public LookAnchor parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(LookAnchor.valueOf(reader.readString()));
    }
  }

  public static class MathOperationArgument extends Argument<MathOperation, MathOperationArgument, CommandSender> {
    public static final String[] NAMES = _mapArray(MathOperation.values());

    public MathOperationArgument(final String name) {
      super(name, MathOperation.class, ArgumentType.MATH_OPERATION);
      withSuggestions("true", "false");
    }

    @Override
    public MathOperationArgument getInstance() {
      return this;
    }

    @Override
    public MathOperation parseArgument(final CommandSource<CommandSender> source,
        final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(MathOperation.fromString(reader.readString()));
    }
  }

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
