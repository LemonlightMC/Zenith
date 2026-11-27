package com.lemonlightmc.zenith.commands.arguments;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.actions.CommandContext;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;

/**
 * Argument types for parsing enum-backed values.
 */
public class EnumArguments {

  /**
   * Generic argument for parsing enum values.
   *
   * <p>
   * Existing simple enum wrappers in the codebase that can be represented by this
   * argument (kept for compatibility): {@code GameModeArgument},
   * {@code EnvironmentArgument}, {@code LookAnchorArgument}, and other
   * single-enum wrappers where no custom parsing/suggestion behavior is needed.
   * </p>
   *
   * @param <E> the enum type
   */
  public static class EnumArgument<E extends Enum<E>> extends Argument<E, EnumArgument<E>, CommandSender> {
    private final Class<E> enumClass;
    private final Function<E, String> mapper;
    private final boolean caseInsensitive;

    /**
     * Creates a case-insensitive enum argument using {@link Enum#name()}.
     *
     * @param name      the argument name
     * @param enumClass the enum class
     */
    public EnumArgument(final String name, final Class<E> enumClass) {
      this(name, enumClass, Enum::name, true);
    }

    /**
     * Creates an enum argument.
     *
     * @param name            the argument name
     * @param enumClass       the enum class
     * @param mapper          maps enum values to suggestion/input strings
     * @param caseInsensitive whether matching ignores case
     */
    public EnumArgument(final String name, final Class<E> enumClass, final Function<E, String> mapper,
        final boolean caseInsensitive) {
      super(name, Objects.requireNonNull(enumClass, "Enum class cant be null"), ArgumentType.ENUM);
      this.enumClass = enumClass;
      this.mapper = Objects.requireNonNullElseGet(mapper, () -> Enum::name);
      this.caseInsensitive = caseInsensitive;
      withSuggestions(info -> Arrays.stream(enumClass.getEnumConstants()).map(this.mapper).toList());
    }

    /**
     * Creates an enum argument using {@link Enum#name()}.
     *
     * @param name      the argument name
     * @param enumClass the enum class
     * @param <E>       the enum type
     * @return a new enum argument
     */
    public static <E extends Enum<E>> EnumArgument<E> of(final String name, final Class<E> enumClass) {
      return new EnumArgument<>(name, enumClass);
    }

    @Override
    public EnumArgument<E> self() {
      return this;
    }

    public Class<E> getEnumClass() {
      return enumClass;
    }

    @Override
    public E parseArgument(final CommandContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String value = reader.readString();
      for (final E constant : enumClass.getEnumConstants()) {
        final String mapped = mapper.apply(constant);
        if (mapped == null) {
          continue;
        }
        if (caseInsensitive ? mapped.equalsIgnoreCase(value) : mapped.equals(value)) {
          return constant;
        }
      }
      for (final E constant : enumClass.getEnumConstants()) {
        if (caseInsensitive ? constant.name().equalsIgnoreCase(value) : constant.name().equals(value)) {
          return constant;
        }
      }
      throw createError(reader, value);
    }
  }
}
