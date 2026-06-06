package com.lemonlightmc.zenith.commands.arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException.CommandSyntaxExceptionContainer;

public class CollectionsArguments {
  private static final CommandSyntaxExceptionContainer DUPLICATE_ARGUMENTS = new CommandSyntaxExceptionContainer(
      "Duplicate arguments are not allowed");
  private static final CommandSyntaxExceptionContainer INVALID_ENTRY = new CommandSyntaxExceptionContainer(
      (value) -> "Item '" + value + "'' is not allowed in list");

  @SuppressWarnings("rawtypes")
  public static abstract class ListArgument<I extends ListArgument<I>> extends Argument<List, I, CommandSender> {

    protected ListArgument(final String name, final Class<List> primitiveType, final ArgumentType rawType) {
      super(name, primitiveType, rawType);
    }
  }

  @SuppressWarnings("rawtypes")
  public static class DynamicListArgument<T> extends ListArgument<DynamicListArgument<T>> {
    private final char delimiter;
    private final boolean allowDuplicates;
    private final Supplier<List<T>> supplier;
    private final Function<T, String> mapper;

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

    @Override
    public DynamicListArgument<T> getInstance() {
      return this;
    }

    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return new CommandSyntaxException(reader, "Invalid List '" + value);
    }

    private Set<String> parseKeys(final StringReader reader) throws CommandSyntaxException {
      final Set<String> listKeys = new HashSet<>();
      final String[] strings = reader.readList(delimiter);
      for (int i = 0; i < strings.length; i++) {
        final String str = strings[i];
        if (!allowDuplicates && listKeys.contains(str)) {
          throw DUPLICATE_ARGUMENTS.createWithContext(reader);
        } else {
          listKeys.add(str);
        }
      }
      return listKeys;

    }

    @Override
    public List<T> parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final Set<String> keys = parseKeys(reader);
      final HashMap<String, T> mapping = new HashMap<>();
      for (final T obj : supplier.get()) {
        mapping.put(mapper.apply(obj), obj);
      }
      final List<T> values = new ArrayList<>();
      for (final String tempKey : keys) {
        final T v = mapping.get(tempKey);
        if (v == null) {
          throw INVALID_ENTRY.createWithContext(reader, v);
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
      final DynamicListArgument other = (DynamicListArgument) obj;
      if (supplier == null && other.supplier != null) {
        return false;
      }
      if (mapper == null && other.mapper != null) {
        return false;
      }
      return delimiter == other.delimiter && allowDuplicates == other.allowDuplicates && supplier.equals(other.supplier)
          && mapper.equals(other.mapper);
    }

    @Override
    public String toString() {
      return "DynamicListArgument [delimiter=" + delimiter + ", allowDuplicates=" + allowDuplicates + "]";
    }
  }

  @SuppressWarnings("rawtypes")
  public static class StaticListArgument<T> extends ListArgument<StaticListArgument<T>> {
    private final char delimiter;
    private final boolean allowDuplicates;
    private final List<T> values;
    private final HashMap<String, T> mapping;

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

    @Override
    public StaticListArgument<T> getInstance() {
      return this;
    }

    @Override
    public CommandSyntaxException createError(final StringReader reader, final String value) {
      return new CommandSyntaxException(reader, "Invalid List '" + value);
    }

    private Set<String> parseKeys(final StringReader reader) throws CommandSyntaxException {
      final Set<String> listKeys = new HashSet<>();
      final String[] strings = reader.readList(delimiter);
      for (int i = 0; i < strings.length; i++) {
        final String str = strings[i];
        if (!allowDuplicates && listKeys.contains(str)) {
          throw DUPLICATE_ARGUMENTS.createWithContext(reader);
        } else {
          listKeys.add(str);
        }
      }
      return listKeys;
    }

    @Override
    public List<T> parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final Set<String> keys = parseKeys(reader);
      final List<T> values = new ArrayList<>();
      for (final String tempKey : keys) {
        final T v = mapping.get(tempKey);
        if (v == null) {
          throw INVALID_ENTRY.createWithContext(reader, v);
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
      final StaticListArgument other = (StaticListArgument) obj;
      if (values == null && other.values != null || mapping == null && other.mapping != null) {
        return false;
      }
      return delimiter == other.delimiter && allowDuplicates == other.allowDuplicates && values.equals(other.values)
          && mapping.equals(other.mapping);
    }

    @Override
    public String toString() {
      return "StaticListArgument [delimiter=" + delimiter + ", allowDuplicates=" + allowDuplicates + "]";
    }
  }

  public static class ListArgumentBuilder<T> {

    public static final ArgumentType LIST_GREEDY = ArgumentType.LIST_GREEDY;
    public static final ArgumentType LIST_TEXT = ArgumentType.LIST_TEXT;
    public static final char DEFAULT_DELIMITER = ',';

    private final String name;
    private final char delimiter;
    private boolean allowDuplicates = false;
    private final boolean isGreedy = false;
    private final boolean isDynamic = false;
    private Supplier<List<T>> supplier;
    private Function<T, String> mapper = t -> String.valueOf(t);

    public ListArgumentBuilder(final String name) {
      this(name, DEFAULT_DELIMITER);
    }

    public ListArgumentBuilder(final String name, final char delimiter) {
      this.name = name;
      this.delimiter = delimiter;
    }

    public ListArgumentBuilder<T> allowDuplicates(final boolean allowDuplicates) {
      this.allowDuplicates = allowDuplicates;
      return this;
    }

    public ListArgumentBuilder<T> withList(final Supplier<List<T>> list) {
      if (list == null) {
        throw new IllegalArgumentException("List in ListArgument cant be empty!");
      }
      this.supplier = list;
      return this;
    }

    public ListArgumentBuilder<T> withList(final List<T> list) {
      if (list == null || list.size() == 0) {
        throw new IllegalArgumentException("List in ListArgument cant be empty!");
      }
      this.supplier = () -> list;
      return this;

    }

    @SafeVarargs
    public final ListArgumentBuilder<T> withList(final T... list) {
      if (list == null || list.length == 0) {
        throw new IllegalArgumentException("List in ListArgument cant be empty!");
      }
      this.supplier = () -> List.of(list);
      return this;

    }

    public ListArgumentBuilder<T> withStringMapper() {
      this.mapper = t -> String.valueOf(t);
      return this;
    }

    public ListArgumentBuilder<T> withMapper(final Function<T, String> mapper) {
      return withStringTooltipMapper(t -> mapper.apply(t));
    }

    public ListArgumentBuilder<T> withStringTooltipMapper(final Function<T, String> mapper) {
      if (mapper == null) {
        throw new IllegalArgumentException("Mapper in ListArgument cant be empty!");
      }
      this.mapper = mapper;
      return this;
    }

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
      final ListArgumentBuilder<?> other = (ListArgumentBuilder<?>) obj;
      if (supplier == null && other.supplier != null) {
        return false;
      }
      if (mapper == null && other.mapper != null) {
        return false;
      }
      return delimiter == other.delimiter && allowDuplicates == other.allowDuplicates && supplier.equals(other.supplier)
          && mapper.equals(other.mapper);
    }

    @Override
    public String toString() {
      return "ListArgumentBuilder [name=" + name + ", delimiter=" + delimiter + ", allowDuplicates=" + allowDuplicates
          + "]";
    }
  }
}
