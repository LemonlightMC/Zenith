package com.lemonlightmc.zenith.commands.arguments;

import java.util.EnumSet;
import java.util.Objects;

import org.bukkit.Axis;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.LocationType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException.CommandSyntaxExceptionContainer;
import com.lemonlightmc.zenith.math.Location2D;
import com.lemonlightmc.zenith.math.Rotation;
import com.lemonlightmc.zenith.math.ranges.DoubleRange;
import com.lemonlightmc.zenith.math.ranges.FloatRange;
import com.lemonlightmc.zenith.math.ranges.IntegerRange;
import com.lemonlightmc.zenith.math.ranges.LongRange;

public class NumberArguments {
  private static final CommandSyntaxExceptionContainer INVALID_RANGE = new CommandSyntaxExceptionContainer(
      value -> "Invalid Range '" + value + "'");
  private static final CommandSyntaxExceptionContainer VALUE_TOO_LOW = new CommandSyntaxExceptionContainer(
      value -> "Value '" + value + "' is too low ");
  private static final CommandSyntaxExceptionContainer VALUE_TOO_HIGH = new CommandSyntaxExceptionContainer(
      value -> "Value '" + value + "' is too high ");

  public static class BoolArgument extends Argument<Boolean, BoolArgument, CommandSender> {

    public BoolArgument(final String name) {
      super(name, Boolean.class, ArgumentType.BOOLEAN);
      withSuggestions("true", "false");
    }

    @Override
    public BoolArgument getInstance() {
      return this;
    }

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

  public static class IntegerArgument extends Argument<Integer, IntegerArgument, CommandSender> {
    private final IntegerRange range;

    public IntegerArgument(final String name) {
      this(name, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public IntegerArgument(final String name, final int min) {
      this(name, min, Integer.MAX_VALUE);
    }

    public IntegerArgument(final String name, final int min, final int max) {
      super(name, Integer.class, ArgumentType.INTEGER);
      this.range = new IntegerRange(min, max);
    }

    @Override
    public IntegerArgument getInstance() {
      return this;
    }

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

  public static class LongArgument extends Argument<Long, LongArgument, CommandSender> {
    private final LongRange range;

    public LongArgument(final String name) {
      this(name, Long.MAX_VALUE, Long.MAX_VALUE);
    }

    public LongArgument(final String name, final long min) {
      this(name, min, Long.MAX_VALUE);
    }

    public LongArgument(final String name, final long min, final long max) {
      super(name, Long.class, ArgumentType.LONG);
      this.range = new LongRange(min, max);
    }

    @Override
    public LongArgument getInstance() {
      return this;
    }

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

  public static class FloatArgument extends Argument<Float, FloatArgument, CommandSender> {
    private final FloatRange range;

    public FloatArgument(final String name) {
      this(name, Float.MAX_VALUE, Float.MAX_VALUE);
    }

    public FloatArgument(final String name, final float min) {
      this(name, min, Float.MAX_VALUE);
    }

    public FloatArgument(final String name, final float min, final float max) {
      super(name, Float.class, ArgumentType.FLOAT);
      this.range = new FloatRange(min, max);
    }

    @Override
    public FloatArgument getInstance() {
      return this;
    }

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

  public static class DoubleArgument extends Argument<Double, DoubleArgument, CommandSender> {
    private final DoubleRange range;

    public DoubleArgument(final String name) {
      this(name, Double.MAX_VALUE, Double.MAX_VALUE);
    }

    public DoubleArgument(final String name, final double min) {
      this(name, min, Double.MAX_VALUE);
    }

    public DoubleArgument(final String name, final double min, final double max) {
      super(name, Double.class, ArgumentType.DOUBLE);
      this.range = new DoubleRange(min, max);
    }

    @Override
    public DoubleArgument getInstance() {
      return this;
    }

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

  public static class IntegerRangeArgument extends Argument<IntegerRange, IntegerRangeArgument, CommandSender> {

    public IntegerRangeArgument(final String name) {
      super(name, IntegerRange.class, ArgumentType.RANGE_INT);
    }

    @Override
    public IntegerRangeArgument getInstance() {
      return this;
    }

    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    @Override
    public IntegerRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return IntegerRange.from(reader.readRange());
    }
  }

  public static class LongRangeArgument extends Argument<LongRange, LongRangeArgument, CommandSender> {

    public LongRangeArgument(final String name) {
      super(name, LongRange.class, ArgumentType.RANGE_LONG);
    }

    @Override
    public LongRangeArgument getInstance() {
      return this;
    }

    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    @Override
    public LongRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return LongRange.from(reader.readRange());
    }
  }

  public static class FloatRangeArgument extends Argument<FloatRange, FloatRangeArgument, CommandSender> {

    public FloatRangeArgument(final String name) {
      super(name, FloatRange.class, ArgumentType.RANGE_FLOAT);
    }

    @Override
    public FloatRangeArgument getInstance() {
      return this;
    }

    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    @Override
    public FloatRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return FloatRange.from(reader.readRange());
    }
  }

  public static class DoubleRangeArgument extends Argument<DoubleRange, DoubleRangeArgument, CommandSender> {

    public DoubleRangeArgument(final String name) {
      super(name, DoubleRange.class, ArgumentType.RANGE_DOUBLE);
    }

    @Override
    public DoubleRangeArgument getInstance() {
      return this;
    }

    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return INVALID_RANGE.createWithContext(reader, value);
    }

    @Override
    public DoubleRange parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return DoubleRange.from(reader.readRange());
    }
  }

  public static class LocationArgument extends Argument<Location, LocationArgument, CommandSender> {

    public static final int MAX_COORDINATE = 30_000_000;
    private static final CommandSyntaxExceptionContainer OUTOFBOUNDS_LOCATION = new CommandSyntaxExceptionContainer(
        value -> "Location is out of bounds'" + value + "' (max " + MAX_COORDINATE + ")");

    private final LocationType type;
    private final boolean centered;

    public LocationArgument(final String nodeName) {
      this(nodeName, LocationType.BLOCK_POSITION);
    }

    public LocationArgument(final String nodeName, final LocationType type) {
      this(nodeName, type, true);
    }

    public LocationArgument(final String nodeName, final LocationType type, final boolean centerPosition) {
      super(nodeName, Location.class, ArgumentType.LOCATION);
      this.type = type;
      this.centered = centerPosition;
      withSuggestions("~", "~ ~", "~ ~ ~");
    }

    @Override
    public LocationArgument getInstance() {
      return this;
    }

    public LocationType getLocationType() {
      return type;
    }

    public boolean getCentered() {
      return centered;
    }

    private double centerPos(final double pos, final boolean center) {
      return type == LocationType.BLOCK_POSITION ? Math.floor(pos)
          : center && Math.floor(pos) == pos ? pos + 0.5d : pos;
    }

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

  public static class Location2DArgument extends Argument<Location2D, Location2DArgument, CommandSender> {

    public static final int MAX_COORDINATE = 30_000_000;
    private static final CommandSyntaxExceptionContainer OUTOFBOUNDS_LOCATION = new CommandSyntaxExceptionContainer(
        value -> "Location is out of bounds'" + value + "' (max " + MAX_COORDINATE + ")");
    private final LocationType type;
    private final boolean centered;

    public Location2DArgument(final String nodeName) {
      this(nodeName, LocationType.BLOCK_POSITION);
    }

    public Location2DArgument(final String nodeName, final LocationType type) {
      this(nodeName, type, true);
    }

    public Location2DArgument(final String nodeName, final LocationType type, final boolean centerPosition) {
      super(nodeName, Location2D.class, ArgumentType.LOCATION);
      this.type = type;
      this.centered = centerPosition;
      withSuggestions("~", "~ ~");
    }

    @Override
    public Location2DArgument getInstance() {
      return this;
    }

    public LocationType getLocationType() {
      return type;
    }

    public boolean getCentered() {
      return centered;
    }

    private double centerPos(final double pos) {
      return type == LocationType.BLOCK_POSITION ? Math.floor(pos)
          : centered && Math.floor(pos) == pos ? pos + 0.5d : pos;
    }

    @Override
    public Location2D parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final double x = centerPos(reader.readDouble());
      final double z = centerPos(reader.readDouble());
      if (x > MAX_COORDINATE || x < -MAX_COORDINATE) {
        throw OUTOFBOUNDS_LOCATION.createWithContext(reader, x);
      }
      if (z > MAX_COORDINATE || z < -MAX_COORDINATE) {
        throw OUTOFBOUNDS_LOCATION.createWithContext(reader, z);
      }
      return new Location2D(source.world(), x, z);
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

  public static class AxisArgument extends Argument<Axis, AxisArgument, CommandSender> {
    public AxisArgument(final String name) {
      super(name, Axis.class, ArgumentType.AXIS);
      withSuggestions("x", "y", "z");
    }

    @Override
    public AxisArgument getInstance() {
      return this;
    }

    @Override
    public Axis parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Axis.valueOf(reader.readString());
    }
  }

  @SuppressWarnings("rawtypes")
  public static class MultiAxisArgument extends Argument<EnumSet, MultiAxisArgument, CommandSender> {

    public MultiAxisArgument(final String name) {
      super(name, EnumSet.class, ArgumentType.AXIS);
      withSuggestions("x", "xy", "xyz", "xzy", "xz", "y", "yx", "yxz", "yzx", "yz", "z", "zx", "zxy", "zyx", "zy");
    }

    @Override
    public MultiAxisArgument getInstance() {
      return this;
    }

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

  public static class RotationArgument extends Argument<Rotation, RotationArgument, CommandSender> {

    public RotationArgument(final String name) {
      super(name, Rotation.class, ArgumentType.ROTATION);
      withSuggestions("0", "90", "180", "270", "-90", "-180", "-270");
    }

    @Override
    public RotationArgument getInstance() {
      return this;
    }

    @Override
    public Rotation parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return new Rotation(reader.readDouble(), reader.readDouble());
    }
  }

}
