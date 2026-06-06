package com.lemonlightmc.zenith.commands.arguments;

import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;

public class StringArguments {

  public enum StringType {
    SINGLE_WORD(),
    QUOTABLE_PHRASE(),
    GREEDY_PHRASE();

    StringType() {
    }
  }

  public static class StringArgument extends Argument<String, StringArgument, CommandSender> {
    private final StringType type;

    public StringArgument(final String name) {
      this(name, StringType.SINGLE_WORD);
    }

    public StringArgument(final String name, final StringType type) {
      super(name, String.class, ArgumentType.STRING);
      this.type = type;
      if (type == null) {
        throw new IllegalArgumentException("String Type cant be null");
      }
    }

    public static StringArgument word(final String name) {
      return new StringArgument(name, StringType.SINGLE_WORD);
    }

    public static StringArgument string(final String name) {
      return new StringArgument(name, StringType.QUOTABLE_PHRASE);
    }

    public static StringArgument greedyString(final String name) {
      return new StringArgument(name, StringType.GREEDY_PHRASE);
    }

    @Override
    public StringArgument getInstance() {
      return this;
    }

    public StringType getStringType() {
      return type;
    }

    @Override
    public String parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
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

  public static class TextArgument extends Argument<String, TextArgument, CommandSender> {

    public TextArgument(final String name) {
      super(name, String.class, ArgumentType.TEXT);
    }

    @Override
    public TextArgument getInstance() {
      return this;
    }

    @Override
    public String parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return reader.readString();
    }
  }

  public static class GreedyStringArgument extends Argument<String, GreedyStringArgument, CommandSender> {

    public GreedyStringArgument(final String name) {
      super(name, String.class, ArgumentType.STRING_GREEDY);
    }

    @Override
    public GreedyStringArgument getInstance() {
      return this;
    }

    @Override
    public String parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String text = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      return text;
    }
  }

  public static class ChatColorArgument extends Argument<ChatColor, ChatColorArgument, CommandSender> {

    public ChatColorArgument(final String name) {
      super(name, ChatColor.class, ArgumentType.CHATCOLOR);
      final ArrayList<String> list = new ArrayList<>();
      for (final ChatColor color : ChatColor.values()) {
        list.add(color.name());
      }
      withSuggestions(list);
    }

    @Override
    public ChatColorArgument getInstance() {
      return this;
    }

    @Override
    public ChatColor parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return ChatColor.valueOf(reader.readUnquotedString());
    }
  }

  public static class ChatArgument extends Argument<String, ChatArgument, CommandSender> {

    public ChatArgument(final String name) {
      super(name, String.class, ArgumentType.CHAT);
    }

    @Override
    public ChatArgument getInstance() {
      return this;
    }

    @Override
    public String parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String text = reader.getRemaining();
      reader.setCursor(reader.getTotalLength());
      return text;
    }
  }

}
