package com.lemonlightmc.zenith.commands.arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.executors.CommandContext;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException.CommandSyntaxExceptionContainer;

/**
 * Argument types for parsing delimited list values.
 */
public class CollectionsArguments {
  private static final CommandSyntaxExceptionContainer DUPLICATE_ARGUMENTS = new CommandSyntaxExceptionContainer(
      "Duplicate arguments are not allowed");
  private static final CommandSyntaxExceptionContainer INVALID_ENTRY = new CommandSyntaxExceptionContainer(
      (value) -> "Item '" + value + "'' is not allowed in list");

  /**
   * Base argument type for list-based command arguments.
   *
   * @param <I> the concrete argument type
   */
  @SuppressWarnings("rawtypes")
  public static abstract class ListArgument<I extends ListArgument<I>> extends Argument<List, I, CommandSender> {

    /**
     * Creates a new list argument.
     *
     * @param name          the argument name
     * @param primitiveType the list class type
     * @param rawType       the raw argument type
     */
    protected ListArgument(final String name, final Class<List> primitiveType, final ArgumentType rawType) {
      super(name, primitiveType, rawType);
    }
  }

  /**
   * List argument backed by a dynamically supplied set of allowed values.
   *
   * @param <T> the list element type
   */
  @SuppressWarnings("rawtypes")
  public static class DynamicListArgument<T> extends ListArgument<DynamicListArgument<T>> {
    private final char delimiter;
    private final boolean allowDuplicates;
    private final Supplier<List<T>> supplier;
    private final Function<T, String> mapper;

    /**
     * Creates a new dynamic list argument.
     *
     * @param nodeName          the argument name
     * @param delimiter         the delimiter separating list entries
     * @param allowDuplicates   {@code true} to allow duplicate entries
     * @param supplier          the supplier of allowed values
     * @param suggestionsMapper the mapper used to convert values to suggestion
     *                          strings
     * @param type              the raw argument type
     */
    public DynamicListArgument(final String nodeName, final char delimiter, final boolean allowDuplicates,
        final Supplier<List<T>> supplier,
        final Function<T, String> suggestionsMapper, final ArgumentType type) {
      super(nodeName, List.class, type);
      this.delimiter = delimiter;
      this.allowDuplicates = allowDuplicates;
      this.supplier = supplier;
      this.mapper = suggestionsMapper;

      withSuggestions(info -> {
        return supplier.get().stream().map(v -> mapper.apply(v)).toList();
      });
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public DynamicListArgument<T> self() {
      return this;
    }

    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return new CommandSyntaxException(reader, "Invalid List '" + value + "'");
    }

    private List<String> parseKeys(final StringReader reader) throws CommandSyntaxException {
      final List<String> listKeys = new ArrayList<>();
      final Set<String> seen = new HashSet<>();
      final String[] strings = reader.readList(delimiter);
      for (final String str : strings) {
        if (!allowDuplicates && !seen.add(str)) {
          throw DUPLICATE_ARGUMENTS.createWithContext(reader);
        }
        listKeys.add(str);
      }
      return listKeys;
    }

    @Override
    public List<T> parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final List<String> keys = parseKeys(reader);
      final HashMap<String, T> mapping = new HashMap<>();
      for (final T obj : supplier.get()) {
        mapping.put(mapper.apply(obj), obj);
      }
      final List<T> values = new ArrayList<>(keys.size());
      for (final String tempKey : keys) {
        final T v = mapping.get(tempKey);
        if (v == null) {
          throw INVALID_ENTRY.createWithContext(reader, tempKey);
        }
        values.add(v);
      }
      return values;
    }

    @Override
    public int hashCode() {
      int result = super.hashCode();
      result = 31 * result + delimiter;
      result = 31 * result + (allowDuplicates ? 1231 : 1237);
      result = 31 * result + ((supplier == null) ? 0 : supplier.hashCode());
      result = 31 * result + ((mapper == null) ? 0 : mapper.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final DynamicListArgument<?> other = (DynamicListArgument<?>) obj;
      return delimiter == other.delimiter && allowDuplicates == other.allowDuplicates
          && java.util.Objects.equals(supplier, other.supplier)
          && java.util.Objects.equals(mapper, other.mapper);
    }

    @Override
    public String toString() {
      return "DynamicListArgument [delimiter=" + delimiter + ", allowDuplicates=" + allowDuplicates + "]";
    }
  }

  /**
   * List argument backed by a fixed set of allowed values.
   *
   * @param <T> the list element type
   */
  @SuppressWarnings("rawtypes")
  public static class StaticListArgument<T> extends ListArgument<StaticListArgument<T>> {
    private final char delimiter;
    private final boolean allowDuplicates;
    private final List<T> values;
    private final HashMap<String, T> mapping;

    /**
     * Creates a new static list argument.
     *
     * @param nodeName        the argument name
     * @param delimiter       the delimiter separating list entries
     * @param allowDuplicates {@code true} to allow duplicate entries
     * @param values          the allowed values
     * @param mapper          the mapper used to convert values to suggestion
     *                        strings
     * @param type            the raw argument type
     */
    public StaticListArgument(final String nodeName, final char delimiter, final boolean allowDuplicates,
        final List<T> values, final Function<T, String> mapper, final ArgumentType type) {
      super(nodeName, List.class, type);
      this.delimiter = delimiter;
      this.allowDuplicates = allowDuplicates;
      this.values = values;

      mapping = new HashMap<>();
      for (final T obj : values) {
        mapping.put(mapper.apply(obj), obj);
      }
      withSuggestions(info -> {
        return mapping.keySet();
      });
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public StaticListArgument<T> self() {
      return this;
    }

    /**
     * Creates a syntax exception for an invalid list value.
     *
     * @param reader the string reader providing parse context
     * @param value  the invalid value
     * @return the created syntax exception
     */
    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return new CommandSyntaxException(reader, "Invalid List '" + value + "'");
    }

    private List<String> parseKeys(final StringReader reader) throws CommandSyntaxException {
      final List<String> listKeys = new ArrayList<>();
      final Set<String> seen = new HashSet<>();
      final String[] strings = reader.readList(delimiter);
      for (final String str : strings) {
        if (!allowDuplicates && !seen.add(str)) {
          throw DUPLICATE_ARGUMENTS.createWithContext(reader);
        }
        listKeys.add(str);
      }
      return listKeys;
    }

    @Override
    public List<T> parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final List<String> keys = parseKeys(reader);
      final List<T> values = new ArrayList<>(keys.size());
      for (final String tempKey : keys) {
        final T v = mapping.get(tempKey);
        if (v == null) {
          throw INVALID_ENTRY.createWithContext(reader, tempKey);
        }
        values.add(v);
      }
      return values;
    }

    @Override
    public int hashCode() {
      int result = 31 * super.hashCode() + delimiter;
      result = 31 * result + (allowDuplicates ? 1231 : 1237);
      result = 31 * result + ((values == null) ? 0 : values.hashCode());
      result = 31 * result + ((mapping == null) ? 0 : mapping.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final StaticListArgument<?> other = (StaticListArgument<?>) obj;
      return delimiter == other.delimiter && allowDuplicates == other.allowDuplicates
          && java.util.Objects.equals(values, other.values)
          && java.util.Objects.equals(mapping, other.mapping);
    }

    @Override
    public String toString() {
      return "StaticListArgument [delimiter=" + delimiter + ", allowDuplicates=" + allowDuplicates + "]";
    }
  }

  /**
   * Builder for creating list-based command arguments.
   *
   * @param <T> the list element type
   */
  public static class ListArgumentBuilder<T> {

    /** Raw argument type used for greedy list parsing. */
    public static final ArgumentType LIST_GREEDY = ArgumentType.LIST_GREEDY;
    /** Raw argument type used for non-greedy list parsing. */
    public static final ArgumentType LIST_TEXT = ArgumentType.LIST_TEXT;
    /** Default delimiter used to separate list entries. */
    public static final char DEFAULT_DELIMITER = ',';

    private final String name;
    private final char delimiter;
    private boolean allowDuplicates = false;
    private boolean isGreedy = false;
    private boolean isDynamic = false;
    private Supplier<List<T>> supplier;
    private Function<T, String> mapper = t -> String.valueOf(t);

    /**
     * Creates a new list argument builder using the default delimiter.
     *
     * @param name the argument name
     */
    public ListArgumentBuilder(final String name) {
      this(name, DEFAULT_DELIMITER);
    }

    /**
     * Creates a new list argument builder.
     *
     * @param name      the argument name
     * @param delimiter the delimiter separating list entries
     */
    public ListArgumentBuilder(final String name, final char delimiter) {
      this.name = name;
      this.delimiter = delimiter;
    }

    /**
     * Sets whether duplicate list entries are allowed.
     *
     * @param allowDuplicates {@code true} to allow duplicate entries
     * @return this builder
     */
    public ListArgumentBuilder<T> allowDuplicates(final boolean allowDuplicates) {
      this.allowDuplicates = allowDuplicates;
      return this;
    }

    /**
     * Sets the supplier providing the allowed list values.
     *
     * @param list the supplier of allowed values
     * @return this builder
     * @throws IllegalArgumentException if {@code list} is {@code null}
     */
    public ListArgumentBuilder<T> withList(final Supplier<List<T>> list) {
      if (list == null) {
        throw new IllegalArgumentException("List in ListArgument cant be empty!");
      }
      this.supplier = list;
      this.isDynamic = true;
      return this;
    }

    public ListArgumentBuilder<T> greedy(final boolean greedy) {
      this.isGreedy = greedy;
      return this;
    }

    public ListArgumentBuilder<T> dynamic(final boolean dynamic) {
      this.isDynamic = dynamic;
      return this;
    }

    /**
     * Sets the allowed list values.
     *
     * @param list the allowed values
     * @return this builder
     * @throws IllegalArgumentException if {@code list} is {@code null} or empty
     */
    public ListArgumentBuilder<T> withList(final List<T> list) {
      if (list == null || list.size() == 0) {
        throw new IllegalArgumentException("List in ListArgument cant be empty!");
      }
      this.supplier = () -> list;
      return this;

    }

    /**
     * Sets the allowed list values.
     *
     * @param list the allowed values
     * @return this builder
     * @throws IllegalArgumentException if {@code list} is {@code null} or empty
     */
    @SafeVarargs
    public final ListArgumentBuilder<T> withList(final T... list) {
      if (list == null || list.length == 0) {
        throw new IllegalArgumentException("List in ListArgument cant be empty!");
      }
      this.supplier = () -> List.of(list);
      return this;

    }

    /**
     * Configures value-to-string mapping using {@link String#valueOf(Object)}.
     *
     * @return this builder
     */
    public ListArgumentBuilder<T> withStringMapper() {
      this.mapper = t -> String.valueOf(t);
      return this;
    }

    /**
     * Sets the mapper used to convert values to suggestion strings.
     *
     * @param mapper the value mapper
     * @return this builder
     */
    public ListArgumentBuilder<T> withMapper(final Function<T, String> mapper) {
      return withStringTooltipMapper(t -> mapper.apply(t));
    }

    /**
     * Sets the mapper used to convert values to suggestion strings.
     *
     * @param mapper the value mapper
     * @return this builder
     * @throws IllegalArgumentException if {@code mapper} is {@code null}
     */
    public ListArgumentBuilder<T> withStringTooltipMapper(final Function<T, String> mapper) {
      if (mapper == null) {
        throw new IllegalArgumentException("Mapper in ListArgument cant be empty!");
      }
      this.mapper = mapper;
      return this;
    }

    /**
     * Builds the configured list argument.
     *
     * @return the built list argument
     */
    public ListArgument<?> build() {
      if (isDynamic) {
        return new DynamicListArgument<>(name, delimiter, allowDuplicates, supplier, mapper,
            isGreedy ? LIST_GREEDY : LIST_TEXT);
      } else {
        return new StaticListArgument<>(name, delimiter, allowDuplicates, supplier.get(), mapper,
            isGreedy ? LIST_GREEDY : LIST_TEXT);
      }
    }

    @Override
    public int hashCode() {
      int result = java.util.Objects.hash(name, delimiter, allowDuplicates, isGreedy, isDynamic);
      result = 31 * result + ((supplier == null) ? 0 : supplier.hashCode());
      result = 31 * result + ((mapper == null) ? 0 : mapper.hashCode());
      return result;
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof final ListArgumentBuilder<?> other)) {
        return false;
      }
      return delimiter == other.delimiter && allowDuplicates == other.allowDuplicates && isGreedy == other.isGreedy
          && isDynamic == other.isDynamic && java.util.Objects.equals(name, other.name)
          && java.util.Objects.equals(supplier, other.supplier) && java.util.Objects.equals(mapper, other.mapper);
    }

    @Override
    public String toString() {
      return "ListArgumentBuilder [name=" + name + ", delimiter=" + delimiter + ", allowDuplicates=" + allowDuplicates
          + "]";
    }
  }
}
