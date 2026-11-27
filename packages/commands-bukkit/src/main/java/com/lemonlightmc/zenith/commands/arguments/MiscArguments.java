package com.lemonlightmc.zenith.commands.arguments;

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.loot.LootTable;
import org.bukkit.util.Vector;

import com.lemonlightmc.zenith.actions.CommandContext;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.math.MathOperation;
import com.lemonlightmc.zenith.unfinished.LookAnchor;

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
    public WorldArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a world.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed world
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public World parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Bukkit.getWorld(reader.readString()));
    }
  }

  /**
   * Argument that parses a duration value.
   *
   * <p>
   * Supports multi-unit formats such as {@code 1m30s4t}, {@code 2h},
   * {@code 500ms},
   * and {@code 1d2h3m4s5t}. Supported units: ticks ({@code t}), milliseconds
   * ({@code ms}), seconds ({@code s}), minutes ({@code m}), hours ({@code h}),
   * days ({@code d}), weeks ({@code w}), months ({@code mo}), and years
   * ({@code y}).
   * </p>
   */
  public static class TimeArgument extends Argument<Duration, TimeArgument, CommandSender> {

    /**
     * Creates a new time argument.
     *
     * @param name the argument name
     */
    public TimeArgument(final String name) {
      super(name, Duration.class, ArgumentType.TIME);
      withSuggestions("1s", "30s", "1m", "1m30s", "1h", "1d", "1m30s4t", "permanent");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public TimeArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a {@link Duration}.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed duration
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Duration parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = reader.readString();
      final Long millis = parseDurationMillis(raw);
      if (millis == null) {
        throw createError(reader, raw);
      }
      if (millis < 0) {
        return Duration.ofMillis(-1);
      }
      return Duration.ofMillis(millis);
    }

    /**
     * Parses a multi-unit duration string into milliseconds.
     *
     * @param input the duration input
     * @return the parsed milliseconds, {@code -1L} for permanent/infinite, or
     *         {@code null} if invalid
     */
    public static Long parseDurationMillis(final String input) {
      if (input == null) {
        return null;
      }
      String str = input.trim().toLowerCase(Locale.ROOT);
      if (str.isEmpty()) {
        return null;
      }
      if ("permanent".equals(str) || "infinite".equals(str) || "infinity".equals(str) || "-1".equals(str)) {
        return -1L;
      }

      long total = 0L;
      int i = 0;
      boolean found = false;
      while (i < str.length()) {
        if (Character.isWhitespace(str.charAt(i))) {
          i++;
          continue;
        }
        int numStart = i;
        while (i < str.length() && Character.isDigit(str.charAt(i))) {
          i++;
        }
        if (numStart == i) {
          return null;
        }
        final long value;
        try {
          value = Long.parseLong(str.substring(numStart, i));
        } catch (final NumberFormatException e) {
          return null;
        }

        int unitStart = i;
        while (i < str.length() && Character.isLetter(str.charAt(i))) {
          i++;
        }
        final String unit = str.substring(unitStart, i);
        final Long unitMillis = unitToMillis(unit);
        if (unitMillis == null) {
          return null;
        }
        try {
          total = Math.addExact(total, Math.multiplyExact(value, unitMillis));
        } catch (final ArithmeticException e) {
          return null;
        }
        found = true;
      }
      return found ? total : null;
    }

    private static Long unitToMillis(final String unit) {
      return switch (unit) {
        case "t", "tick", "ticks" -> 50L;
        case "ms", "milli", "millis", "millisecond", "milliseconds" -> 1L;
        case "s", "sec", "secs", "second", "seconds" -> 1000L;
        case "m", "min", "mins", "minute", "minutes" -> 60_000L;
        case "h", "hr", "hrs", "hour", "hours" -> 3_600_000L;
        case "d", "day", "days" -> 86_400_000L;
        case "w", "week", "weeks" -> 604_800_000L;
        case "mo", "mon", "month", "months" -> 2_629_800_000L;
        case "y", "yr", "yrs", "year", "years" -> 31_557_600_000L;
        default -> null;
      };
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
    public NamespacedKeyArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a namespaced key.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed namespaced key
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public NamespacedKey parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = readBlockDataToken(reader);
      final NamespacedKey namespacedKey = NamespacedKey.fromString(raw);
      if (namespacedKey == null) {
        throw createError(reader, raw);
      }
      return namespacedKey;
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
    public BlockStateArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a block state.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed block state
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public BlockState parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = readBlockDataToken(reader);
      try {
        return Objects.requireNonNull(Bukkit.createBlockData(raw).createBlockState());
      } catch (final Exception ex) {
        throw createError(reader, raw);
      }
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
    public BlockDataArgument self() {
      return this;
    }

    /**
     * Parses the argument value as block data.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed block data
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public BlockData parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = readBlockDataToken(reader);
      try {
        return Bukkit.createBlockData(raw);
      } catch (final Exception ex) {
        throw createError(reader, raw);
      }
    }
  }

  private static String readBlockDataToken(final StringReader reader) throws CommandSyntaxException {
    if (!reader.canRead()) {
      return "";
    }
    final char next = reader.peek();
    if (StringReader.isQuote(next)) {
      return reader.readString();
    }
    final int start = reader.getCursor();
    while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
      reader.skip();
    }
    return reader.getString().substring(start, reader.getCursor());
  }

  /**
   * Argument that resolves a {@link LootTable} by namespaced key.
   */
  public static class LootTableArgument extends Argument<LootTable, LootTableArgument, CommandSender> {

    /**
     * Creates a new loot table argument.
     *
     * @param name the argument name
     */
    public LootTableArgument(final String name) {
      super(name, LootTable.class, ArgumentType.LOOT_TABLE);
      withSuggestions(info -> Registry.LOOT_TABLES.stream()
          .map(lt -> lt.getKey().toString())
          .toList());
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public LootTableArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a loot table.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed loot table
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public LootTable parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = readBlockDataToken(reader);
      final NamespacedKey namespacedKey = NamespacedKey.fromString(raw);
      if (namespacedKey == null) {
        throw createError(reader, raw);
      }
      final LootTable lootTable = Bukkit.getLootTable(namespacedKey);
      if (lootTable == null) {
        throw createError(reader, raw);
      }
      return lootTable;
    }
  }

  /**
   * Argument that resolves a crafting recipe by namespaced key.
   */
  public static class RecipeArgument extends Argument<org.bukkit.inventory.Recipe, RecipeArgument, CommandSender> {

    /**
     * Creates a new recipe argument.
     *
     * @param name the argument name
     */
    public RecipeArgument(final String name) {
      super(name, org.bukkit.inventory.Recipe.class, ArgumentType.RECIPE);
      withSuggestions(info -> {
        final java.util.ArrayList<String> keys = new java.util.ArrayList<>();
        final java.util.Iterator<org.bukkit.inventory.Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
          final org.bukkit.inventory.Recipe recipe = it.next();
          if (recipe instanceof org.bukkit.Keyed keyed) {
            keys.add(keyed.getKey().toString());
          }
        }
        return keys;
      });
    }

    @Override
    public RecipeArgument self() {
      return this;
    }

    @Override
    public org.bukkit.inventory.Recipe parseArgument(final CommandContext<CommandSender> source,
        final StringReader reader, final String key) throws CommandSyntaxException {
      final String raw = readBlockDataToken(reader);
      final NamespacedKey namespacedKey = NamespacedKey.fromString(raw);
      if (namespacedKey == null) {
        throw createError(reader, raw);
      }
      final org.bukkit.inventory.Recipe recipe = Bukkit.getRecipe(namespacedKey);
      if (recipe == null) {
        throw createError(reader, raw);
      }
      return recipe;
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
    public EnvironmentArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a world environment.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed environment
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Environment parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
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
    public GameModeArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a game mode.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed game mode
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public GameMode parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
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
    public LookAnchorArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a look anchor.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed look anchor
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public LookAnchor parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
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
    public MathOperationArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a math operation.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed math operation
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public MathOperation parseArgument(final CommandContext<CommandSender> source,
        final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(MathOperation.fromString(reader.readString()));
    }
  }

  /**
   * Argument that parses a Bukkit {@link Color}.
   *
   * <p>
   * Supported formats: {@code #RRGGBB}, {@code RRGGBB}, and {@code r,g,b}.
   * </p>
   */
  public static class ColorArgument extends Argument<Color, ColorArgument, CommandSender> {

    /**
     * Creates a new color argument.
     *
     * @param name the argument name
     */
    public ColorArgument(final String name) {
      super(name, Color.class, ArgumentType.COLOR);
      withSuggestions("#FFFFFF", "#000000", "#FF0000", "#00FF00", "#0000FF", "255,255,255", "0,0,0");
    }

    @Override
    public ColorArgument self() {
      return this;
    }

    @Override
    public Color parseArgument(final CommandContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String raw = reader.readString();
      final Color color = parseColor(raw);
      if (color == null) {
        throw createError(reader, raw);
      }
      return color;
    }

    private static Color parseColor(final String input) {
      if (input == null) {
        return null;
      }
      String value = input.trim();
      if (value.isEmpty()) {
        return null;
      }
      if (value.startsWith("#")) {
        value = value.substring(1);
      }
      if (value.matches("[0-9A-Fa-f]{6}")) {
        try {
          final int rgb = Integer.parseInt(value, 16);
          return Color.fromRGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
        } catch (final NumberFormatException ignored) {
          return null;
        }
      }
      final String[] parts = value.split("[,;]");
      if (parts.length == 3) {
        try {
          final int r = Integer.parseInt(parts[0].trim());
          final int g = Integer.parseInt(parts[1].trim());
          final int b = Integer.parseInt(parts[2].trim());
          if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
            return null;
          }
          return Color.fromRGB(r, g, b);
        } catch (final NumberFormatException ignored) {
          return null;
        }
      }
      return null;
    }
  }

  /**
   * Argument that parses a Bukkit {@link Vector}.
   *
   * <p>
   * Supported format: {@code x y z}.
   * </p>
   */
  public static class VectorArgument extends Argument<Vector, VectorArgument, CommandSender> {

    /**
     * Creates a new vector argument.
     *
     * @param name the argument name
     */
    public VectorArgument(final String name) {
      super(name, Vector.class, ArgumentType.VECTOR);
      withSuggestions("0 0 0", "1 1 1", "~ ~ ~");
    }

    @Override
    public VectorArgument self() {
      return this;
    }

    @Override
    public Vector parseArgument(final CommandContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final double x = readVectorComponent(reader);
      skipComponentSeparator(reader);
      final double y = readVectorComponent(reader);
      skipComponentSeparator(reader);
      final double z = readVectorComponent(reader);
      return new Vector(x, y, z);
    }

    private static void skipComponentSeparator(final StringReader reader) throws CommandSyntaxException {
      reader.skipWhitespace();
      if (reader.canRead() && reader.peek() == ',') {
        reader.skip();
      }
      reader.skipWhitespace();
    }

    private double readVectorComponent(final StringReader reader) throws CommandSyntaxException {
      reader.skipWhitespace();
      final int start = reader.getCursor();
      boolean relative = false;
      if (reader.canRead() && reader.peek() == '~') {
        relative = true;
        reader.skip();
        if (!reader.canRead() || Character.isWhitespace(reader.peek()) || reader.peek() == ',') {
          return 0.0D;
        }
      }
      while (reader.canRead() && isVectorNumberChar(reader.peek())) {
        reader.skip();
      }
      final String raw = reader.getString().substring(start, reader.getCursor());
      final String number = relative ? raw.substring(1) : raw;
      if (number.isEmpty() || ".".equals(number) || "-".equals(number) || "+".equals(number)) {
        throw createError(reader, raw.isEmpty() ? "<empty>" : raw);
      }
      try {
        return Double.parseDouble(number);
      } catch (final NumberFormatException ex) {
        throw createError(reader, raw);
      }
    }

    private static boolean isVectorNumberChar(final char c) {
      return (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+';
    }
  }

  /**
   * Argument that resolves a {@link SoundCategory} by name.
   */
  public static class SoundCategoryArgument extends Argument<SoundCategory, SoundCategoryArgument, CommandSender> {

    public SoundCategoryArgument(final String name) {
      super(name, SoundCategory.class, ArgumentType.SOUND_CATEGORY);
      withSuggestions(info -> java.util.Arrays.stream(SoundCategory.values()).map(Enum::name).toList());
    }

    @Override
    public SoundCategoryArgument self() {
      return this;
    }

    @Override
    public SoundCategory parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key) throws CommandSyntaxException {
      final String raw = reader.readString();
      for (final SoundCategory category : SoundCategory.values()) {
        if (category.name().equalsIgnoreCase(raw)) {
          return category;
        }
      }
      throw createError(reader, raw);
    }
  }

  /**
   * Argument that resolves a {@link Difficulty} by name.
   */
  public static class DifficultyArgument extends Argument<Difficulty, DifficultyArgument, CommandSender> {

    public DifficultyArgument(final String name) {
      super(name, Difficulty.class, ArgumentType.DIFFICULTY);
      withSuggestions(info -> java.util.Arrays.stream(Difficulty.values()).map(Enum::name).toList());
    }

    @Override
    public DifficultyArgument self() {
      return this;
    }

    @Override
    public Difficulty parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key) throws CommandSyntaxException {
      final String raw = reader.readString();
      for (final Difficulty difficulty : Difficulty.values()) {
        if (difficulty.name().equalsIgnoreCase(raw)) {
          return difficulty;
        }
      }
      throw createError(reader, raw);
    }
  }

  /**
   * Maps an array of values to their string representations.
   *
   * @param registry the values to map
   * @param <T>      the value type
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
