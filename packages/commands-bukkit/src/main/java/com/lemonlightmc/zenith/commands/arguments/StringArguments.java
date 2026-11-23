package com.lemonlightmc.zenith.commands.arguments;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.utils.actions.ConditionContext;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;

/**
 * Argument types for parsing string-based command inputs.
 */
public class StringArguments {

  /**
   * Defines how string arguments are parsed from command input.
   */
  public enum StringType {
    /** Parses a single unquoted word. */
    SINGLE_WORD(),
    /** Parses a single word or a quoted phrase. */
    QUOTABLE_PHRASE(),
    /** Parses the remainder of the input as a single string. */
    GREEDY_PHRASE();

    StringType() {
    }
  }

  /**
   * Argument that parses string values using a configurable {@link StringType}.
   */
  public static class StringArgument extends Argument<String, StringArgument, CommandSender> {
    private final StringType type;

    /**
     * Creates a single-word string argument.
     *
     * @param name the argument name
     */
    public StringArgument(final String name) {
      this(name, StringType.SINGLE_WORD);
    }

    /**
     * Creates a string argument with the specified parsing type.
     *
     * @param name the argument name
     * @param type the string parsing type
     * @throws IllegalArgumentException if {@code type} is {@code null}
     */
    public StringArgument(final String name, final StringType type) {
      super(name, String.class, ArgumentType.STRING);
      this.type = type;
      if (type == null) {
        throw new IllegalArgumentException("String Type cant be null");
      }
    }

    /**
     * Creates a single-word string argument.
     *
     * @param name the argument name
     * @return a new string argument
     */
    public static StringArgument word(final String name) {
      return new StringArgument(name, StringType.SINGLE_WORD);
    }

    /**
     * Creates a quotable string argument.
     *
     * @param name the argument name
     * @return a new string argument
     */
    public static StringArgument string(final String name) {
      return new StringArgument(name, StringType.QUOTABLE_PHRASE);
    }

    /**
     * Creates a greedy string argument.
     *
     * @param name the argument name
     * @return a new string argument
     */
    public static StringArgument greedyString(final String name) {
      return new StringArgument(name, StringType.GREEDY_PHRASE);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public StringArgument self() {
      return this;
    }

    /**
     * Returns the configured string parsing type.
     *
     * @return the string type
     */
    public StringType getStringType() {
      return type;
    }

    @Override
    public String parseArgument(final ConditionContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      if (type == StringType.GREEDY_PHRASE) {
        final String text = reader.getRemaining();
        reader.setCursor(reader.getTotalLength());
        return text;
      } else if (type == StringType.SINGLE_WORD) {
        return reader.readUnquotedString();
      } else {
        return reader.readString();
      }
    }

    public static String escapeIfRequired(final String input) {
      for (final char c : input.toCharArray()) {
        if (!StringReader.isUnquotedString(c)) {
          return escape(input);
        }
      }
      return input;
    }

    public static String escape(final String input) {
      final StringBuilder result = new StringBuilder("\"");
      final int len = input.length();
      for (int i = 0; i < len; i++) {
        final char c = input.charAt(i);
        if (c == '\\' || c == '"') {
          result.append('\\');
        }
        result.append(c);
      }
      return result.append("\"").toString();
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + type.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final StringArgument other = (StringArgument) obj;
      return type == other.type;
    }

    @Override
    public String toString() {
      return toStringWithMore("type=" + type);
    }
  }

  /**
   * Argument that parses a text string value.
   */
  public static class TextArgument extends Argument<String, TextArgument, CommandSender> {

    /**
     * Creates a new text argument.
     *
     * @param name the argument name
     */
    public TextArgument(final String name) {
      super(name, String.class, ArgumentType.TEXT);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public TextArgument self() {
      return this;
    }

    /**
     * Parses the argument value as text.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed text value
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public String parseArgument(final ConditionContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return reader.readString();
    }
  }

  /**
   * Argument that consumes the remainder of the input as a string.
   */
  public static class GreedyStringArgument extends Argument<String, GreedyStringArgument, CommandSender> {

    /**
     * Creates a new greedy string argument.
     *
     * @param name the argument name
     */
    public GreedyStringArgument(final String name) {
      super(name, String.class, ArgumentType.STRING_GREEDY);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public GreedyStringArgument self() {
      return this;
    }

    /**
     * Parses the remaining input as a string value.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the remaining input
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public String parseArgument(final ConditionContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String text = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      return text;
    }
  }

  /**
   * Argument that parses a Bukkit {@link ChatColor} value.
   */
  public static class ChatColorArgument extends Argument<ChatColor, ChatColorArgument, CommandSender> {

    /**
     * Creates a new chat color argument.
     *
     * @param name the argument name
     */
    public ChatColorArgument(final String name) {
      super(name, ChatColor.class, ArgumentType.CHATCOLOR);
      final ArrayList<String> list = new ArrayList<>();
      for (final ChatColor color : ChatColor.values()) {
        list.add(color.name());
      }
      withSuggestions(list);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public ChatColorArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a chat color.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed chat color
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public ChatColor parseArgument(final ConditionContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return ChatColor.valueOf(reader.readUnquotedString());
    }
  }

  /**
   * Argument that consumes the remainder of the input as chat text.
   */
  public static class ChatArgument extends Argument<String, ChatArgument, CommandSender> {

    /**
     * Creates a new chat argument.
     *
     * @param name the argument name
     */
    public ChatArgument(final String name) {
      super(name, String.class, ArgumentType.CHAT);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public ChatArgument self() {
      return this;
    }

    /**
     * Parses the remaining input as chat text.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the remaining chat text
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public String parseArgument(final ConditionContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String text = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      return text;
    }
  }

}
