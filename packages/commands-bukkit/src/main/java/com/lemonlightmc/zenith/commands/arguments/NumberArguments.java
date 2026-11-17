package com.lemonlightmc.zenith.commands.arguments;

import java.util.EnumSet;
import java.util.Objects;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.additive.math.ranges.DoubleRange;
import com.lemonlightmc.zenith.additive.math.ranges.FloatRange;
import com.lemonlightmc.zenith.additive.math.ranges.IntegerRange;
import com.lemonlightmc.zenith.additive.math.ranges.LongRange;
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.LocationType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException.CommandSyntaxExceptionContainer;
import com.lemonlightmc.zenith.math.Rotation;

/**
 * Argument types for numeric values, ranges, locations, axes, and rotations.
 */
public class NumberArguments {
  private static final CommandSyntaxExceptionContainer INVALID_RANGE = new CommandSyntaxExceptionContainer(
      value -> "Invalid Range '" + value + "'");
  private static final CommandSyntaxExceptionContainer VALUE_TOO_LOW = new CommandSyntaxExceptionContainer(
      value -> "Value '" + value + "' is too low ");
  private static final CommandSyntaxExceptionContainer VALUE_TOO_HIGH = new CommandSyntaxExceptionContainer(
      value -> "Value '" + value + "' is too high ");

  /**
   * Argument that parses boolean values.
   */
  public static class BoolArgument extends Argument<Boolean, BoolArgument, CommandSender> {

    /**
     * Creates a new boolean argument.
     *
     * @param name the argument name
     */
    public BoolArgument(final String name) {
      super(name, Boolean.class, ArgumentType.BOOLEAN);
      withSuggestions("true", "false");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public BoolArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a boolean.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed boolean value
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Boolean parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      reader.point();
      try {
        return reader.readBoolean();
      } catch (final CommandSyntaxException e) {
        reader.resetCursor();
        throw e;
      } catch (final Exception e) {
        reader.resetCursor();
        throw e;
      }
    }
  }

  /**
   * Argument that parses integer values within an optional range.
   */
  public static class IntegerArgument extends Argument<Integer, IntegerArgument, CommandSender> {
    private final IntegerRange range;

    /**
     * Creates a new integer argument with no effective bounds.
     *
     * @param name the argument name
     */
    public IntegerArgument(final String name) {
      this(name, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Creates a new integer argument with a minimum bound.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     */
    public IntegerArgument(final String name, final int min) {
      this(name, min, Integer.MAX_VALUE);
    }

    /**
     * Creates a new integer argument with minimum and maximum bounds.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     */
    public IntegerArgument(final String name, final int min, final int max) {
      super(name, Integer.class, ArgumentType.INTEGER);
      this.range = new IntegerRange(min, max);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public IntegerArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as an integer.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed integer value
     * @throws CommandSyntaxException if parsing fails or the value is out of range
     */
    @Override
    public Integer parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final int value = reader.readInt();
      if (range.isLower(value)) {
        throw VALUE_TOO_LOW.createWithContext(reader, value, range.getMin());
      }
      if (range.isHigher(value)) {
        throw VALUE_TOO_HIGH.createWithContext(reader, value, range.getMax());
      }
      return value;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + range.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final IntegerArgument other = (IntegerArgument) obj;
      return range.equals(other.range);
    }

    @Override
    public String toString() {
      return toStringWithMore("range=" + range.toString());
    }
  }

  /**
   * Argument that parses long values within an optional range.
   */
  public static class LongArgument extends Argument<Long, LongArgument, CommandSender> {
    private final LongRange range;

    /**
     * Creates a new long argument with no effective bounds.
     *
     * @param name the argument name
     */
    public LongArgument(final String name) {
      this(name, Long.MAX_VALUE, Long.MAX_VALUE);
    }

    /**
     * Creates a new long argument with a minimum bound.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     */
    public LongArgument(final String name, final long min) {
      this(name, min, Long.MAX_VALUE);
    }

    /**
     * Creates a new long argument with minimum and maximum bounds.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     */
    public LongArgument(final String name, final long min, final long max) {
      super(name, Long.class, ArgumentType.LONG);
      this.range = new LongRange(min, max);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public LongArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a long.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed long value
     * @throws CommandSyntaxException if parsing fails or the value is out of range
     */
    @Override
    public Long parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final long value = reader.readLong();
      if (range.isLower(value)) {
        throw VALUE_TOO_LOW.createWithContext(reader, value, range.getMin());
      }
      if (range.isHigher(value)) {
        throw VALUE_TOO_HIGH.createWithContext(reader, value, range.getMax());
      }
      return value;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + range.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final LongArgument other = (LongArgument) obj;
      return range.equals(other.range);
    }

    @Override
    public String toString() {
      return toStringWithMore("range=" + range.toString());
    }
  }

  /**
   * Argument that parses float values within an optional range.
   */
  public static class FloatArgument extends Argument<Float, FloatArgument, CommandSender> {
    private final FloatRange range;

    /**
     * Creates a new float argument with no effective bounds.
     *
     * @param name the argument name
     */
    public FloatArgument(final String name) {
      this(name, Float.MAX_VALUE, Float.MAX_VALUE);
    }

    /**
     * Creates a new float argument with a minimum bound.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     */
    public FloatArgument(final String name, final float min) {
      this(name, min, Float.MAX_VALUE);
    }

    /**
     * Creates a new float argument with minimum and maximum bounds.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     */
    public FloatArgument(final String name, final float min, final float max) {
      super(name, Float.class, ArgumentType.FLOAT);
      this.range = new FloatRange(min, max);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public FloatArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a float.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed float value
     * @throws CommandSyntaxException if parsing fails or the value is out of range
     */
    @Override
    public Float parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final float value = reader.readFloat();
      if (range.isLower(value)) {
        throw VALUE_TOO_LOW.createWithContext(reader, value, range.getMin());
      }
      if (range.isHigher(value)) {
        throw VALUE_TOO_HIGH.createWithContext(reader, value, range.getMax());
      }
      return value;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + range.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final FloatArgument other = (FloatArgument) obj;
      return range.equals(other.range);
    }

    @Override
    public String toString() {
      return toStringWithMore("range=" + range.toString());
    }
  }

  /**
   * Argument that parses double values within an optional range.
   */
  public static class DoubleArgument extends Argument<Double, DoubleArgument, CommandSender> {
    private final DoubleRange range;

    /**
     * Creates a new double argument with no effective bounds.
     *
     * @param name the argument name
     */
    public DoubleArgument(final String name) {
      this(name, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    /**
     * Creates a new double argument with a minimum bound.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     */
    public DoubleArgument(final String name, final double min) {
      this(name, min, Double.MAX_VALUE);
    }

    /**
     * Creates a new double argument with minimum and maximum bounds.
     *
     * @param name the argument name
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     */
    public DoubleArgument(final String name, final double min, final double max) {
      super(name, Double.class, ArgumentType.DOUBLE);
      this.range = new DoubleRange(min, max);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public DoubleArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a double.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed double value
     * @throws CommandSyntaxException if parsing fails or the value is out of range
     */
    @Override
    public Double parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final double value = reader.readDouble();
      if (range.isLower(value)) {
        throw VALUE_TOO_LOW.createWithContext(reader, value, range.getMin());
      }
      if (range.isHigher(value)) {
        throw VALUE_TOO_HIGH.createWithContext(reader, value, range.getMax());
      }
      return value;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + range.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final DoubleArgument other = (DoubleArgument) obj;
      return range.equals(other.range);
    }

    @Override
    public String toString() {
      return toStringWithMore("range=" + range.toString());
    }
  }

  /**
   * Argument that parses an {@link IntegerRange}.
   */
  public static class IntegerRangeArgument extends Argument<IntegerRange, IntegerRangeArgument, CommandSender> {

    /**
     * Creates a new integer range argument.
     *
     * @param name the argument name
     */
    public IntegerRangeArgument(final String name) {
      super(name, IntegerRange.class, ArgumentType.RANGE_INT);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public IntegerRangeArgument getInstance() {
      return this;
    }

    /**
     * Creates a syntax exception for an invalid range value.
     *
     * @param reader the string reader providing parse context
     * @param value the invalid value
     * @return the created syntax exception
     */
    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    /**
     * Parses the argument value as an integer range.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed integer range
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public IntegerRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return IntegerRange.from(reader.readRange());
    }
  }

  /**
   * Argument that parses a {@link LongRange}.
   */
  public static class LongRangeArgument extends Argument<LongRange, LongRangeArgument, CommandSender> {

    /**
     * Creates a new long range argument.
     *
     * @param name the argument name
     */
    public LongRangeArgument(final String name) {
      super(name, LongRange.class, ArgumentType.RANGE_LONG);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public LongRangeArgument getInstance() {
      return this;
    }

    /**
     * Creates a syntax exception for an invalid range value.
     *
     * @param reader the string reader providing parse context
     * @param value the invalid value
     * @return the created syntax exception
     */
    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    /**
     * Parses the argument value as a long range.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed long range
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public LongRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return LongRange.from(reader.readRange());
    }
  }

  /**
   * Argument that parses a {@link FloatRange}.
   */
  public static class FloatRangeArgument extends Argument<FloatRange, FloatRangeArgument, CommandSender> {

    /**
     * Creates a new float range argument.
     *
     * @param name the argument name
     */
    public FloatRangeArgument(final String name) {
      super(name, FloatRange.class, ArgumentType.RANGE_FLOAT);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public FloatRangeArgument getInstance() {
      return this;
    }

    /**
     * Creates a syntax exception for an invalid range value.
     *
     * @param reader the string reader providing parse context
     * @param value the invalid value
     * @return the created syntax exception
     */
    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    /**
     * Parses the argument value as a float range.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed float range
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public FloatRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return FloatRange.from(reader.readRange());
    }
  }

  /**
   * Argument that parses a {@link DoubleRange}.
   */
  public static class DoubleRangeArgument extends Argument<DoubleRange, DoubleRangeArgument, CommandSender> {

    /**
     * Creates a new double range argument.
     *
     * @param name the argument name
     */
    public DoubleRangeArgument(final String name) {
      super(name, DoubleRange.class, ArgumentType.RANGE_DOUBLE);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public DoubleRangeArgument getInstance() {
      return this;
    }

    /**
     * Creates a syntax exception for an invalid range value.
     *
     * @param reader the string reader providing parse context
     * @param value the invalid value
     * @return the created syntax exception
     */
    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    /**
     * Parses the argument value as a double range.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed double range
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public DoubleRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return DoubleRange.from(reader.readRange());
    }
  }

  /**
   * Argument that parses a {@link Location} value.
   */
  public static class LocationArgument extends Argument<Location, LocationArgument, CommandSender> {

    /** Maximum absolute coordinate value accepted by this argument. */
    public static final int MAX_COORDINATE = 30_000_000;
    private static final CommandSyntaxExceptionContainer OUTOFBOUNDS_LOCATION = new CommandSyntaxExceptionContainer(
        value -> "Location is out of bounds'" + value + "' (max " + MAX_COORDINATE + ")");

    private final LocationType type;
    private final boolean centered;

    /**
     * Creates a new block-position location argument.
     *
     * @param nodeName the argument name
     */
    public LocationArgument(final String nodeName) {
      this(nodeName, LocationType.BLOCK_POSITION);
    }

    /**
     * Creates a new location argument with the specified location type.
     *
     * @param nodeName the argument name
     * @param type the location type
     */
    public LocationArgument(final String nodeName, final LocationType type) {
      this(nodeName, type, true);
    }

    /**
     * Creates a new location argument.
     *
     * @param nodeName the argument name
     * @param type the location type
     * @param centerPosition {@code true} to center block-aligned coordinates when applicable
     */
    public LocationArgument(final String nodeName, final LocationType type, final boolean centerPosition) {
      super(nodeName, Location.class, ArgumentType.LOCATION);
      this.type = type;
      this.centered = centerPosition;
      withSuggestions("~", "~ ~", "~ ~ ~");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public LocationArgument getInstance() {
      return this;
    }

    /**
     * Returns the configured location type.
     *
     * @return the location type
     */
    public LocationType getLocationType() {
      return type;
    }

    /**
     * Returns whether parsed positions are centered when applicable.
     *
     * @return {@code true} if positions are centered
     */
    public boolean getCentered() {
      return centered;
    }

    private double centerPos(final double pos, final boolean center) {
      return type == LocationType.BLOCK_POSITION ? Math.floor(pos)
          : center && Math.floor(pos) == pos ? pos + 0.5d : pos;
    }

    /**
     * Parses the argument value as a location.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed location
     * @throws CommandSyntaxException if parsing fails or coordinates are out of bounds
     */
    @Override
    public Location parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final double x = centerPos(reader.readDouble(), centered);
      final double y = centerPos(reader.readDouble(), false);
      final double z = centerPos(reader.readDouble(), centered);
      if (x > MAX_COORDINATE || x < -MAX_COORDINATE) {
        throw OUTOFBOUNDS_LOCATION.createWithContext(reader, x);
      }
      if (y > MAX_COORDINATE || y < -MAX_COORDINATE) {
        throw OUTOFBOUNDS_LOCATION.createWithContext(reader, y);
      }

      if (z > MAX_COORDINATE || z < -MAX_COORDINATE) {
        throw OUTOFBOUNDS_LOCATION.createWithContext(reader, z);
      }
      return new Location(source.world(), x, y, z);
    }

    @Override
    public int hashCode() {
      final int result = 31 * super.hashCode() + ((type == null) ? 0 : type.hashCode());
      return 31 * result + (centered ? 1231 : 1237);
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final LocationArgument other = (LocationArgument) obj;
      return type == other.type && centered == other.centered;
    }

    @Override
    public String toString() {
      return toStringWithMore("type=" + type + ", centered=" + centered);
    }
  }

  /**
   * Argument that parses a single {@link Axis} value.
   */
  public static class AxisArgument extends Argument<Axis, AxisArgument, CommandSender> {

    /**
     * Creates a new axis argument.
     *
     * @param name the argument name
     */
    public AxisArgument(final String name) {
      super(name, Axis.class, ArgumentType.AXIS);
      withSuggestions("x", "y", "z");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public AxisArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as an axis.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed axis
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Axis parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Axis.valueOf(reader.readString());
    }
  }

  /**
   * Argument that parses multiple {@link Axis} values into an {@link EnumSet}.
   */
  @SuppressWarnings("rawtypes")
  public static class MultiAxisArgument extends Argument<EnumSet, MultiAxisArgument, CommandSender> {

    /**
     * Creates a new multi-axis argument.
     *
     * @param name the argument name
     */
    public MultiAxisArgument(final String name) {
      super(name, EnumSet.class, ArgumentType.AXIS);
      withSuggestions("x", "xy", "xyz", "xzy", "xz", "y", "yx", "yxz", "yzx", "yz", "z", "zx", "zxy", "zyx", "zy");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public MultiAxisArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a set of axes.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed axis set
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public EnumSet<Axis> parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String value = reader.readString();
      final EnumSet<Axis> set = EnumSet.noneOf(Axis.class);
      for (int i = 0; i < value.length(); i++) {
        set.add(Objects.requireNonNull(Axis.valueOf(value.substring(i, i + 1))));
      }
      return set;
    }
  }

  /**
   * Argument that parses a {@link Rotation} value.
   */
  public static class RotationArgument extends Argument<Rotation, RotationArgument, CommandSender> {

    /**
     * Creates a new rotation argument.
     *
     * @param name the argument name
     */
    public RotationArgument(final String name) {
      super(name, Rotation.class, ArgumentType.ROTATION);
      withSuggestions("0", "90", "180", "270", "-90", "-180", "-270");
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public RotationArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a rotation.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed rotation
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Rotation parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return new Rotation(reader.readDouble(), reader.readDouble());
    }
  }

}
