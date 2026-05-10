package com.lemonlightmc.zenith.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.commands.BukkitCommandResult;
import com.lemonlightmc.zenith.commands.CommandAPI;
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandResult;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.exceptions.InvalidArgumentBranchException;

public class SpecialArguments {

  public static record ArgumentBranch(List<Argument<?, ?, CommandSender>> args, ArgumentBranchType type) {
  }

  public static enum ArgumentBranchType {
    ONCE(),
    LOOPING(),
    END();
  }

  public interface LiteralArgumentType {

  }

  public static class BranchArgument extends Argument<Boolean, BranchArgument, CommandSender> {

    private final List<ArgumentBranch> branches;

    public BranchArgument(final String name) {
      super(name, null, ArgumentType.BRANCH);
      branches = new ArrayList<>();
    }

    @Override
    public BranchArgument getInstance() {
      return this;
    }

    public List<ArgumentBranch> getBranches() {
      return branches;
    }

    public BranchArgument clear() {
      branches.clear();
      return this;
    }

    @SuppressWarnings("unchecked")
    public BranchArgument branch(final Argument<?, ?, CommandSender>... args) {
      createBranch(args, ArgumentBranchType.LOOPING);
      return this;
    }

    @SuppressWarnings("unchecked")
    public BranchArgument branch(final ArgumentBranchType type, final Argument<?, ?, CommandSender>... args) {
      createBranch(args, type);
      return this;
    }

    @SuppressWarnings("unchecked")
    public BranchArgument loopingBranch(final Argument<?, ?, CommandSender>... args) {
      createBranch(args, ArgumentBranchType.LOOPING);
      return this;
    }

    @SuppressWarnings("unchecked")
    public BranchArgument onceBranch(final Argument<?, ?, CommandSender>... args) {
      createBranch(args, ArgumentBranchType.ONCE);
      return this;
    }

    @SuppressWarnings("unchecked")
    public BranchArgument endBranch(final Argument<?, ?, CommandSender>... args) {
      createBranch(args, ArgumentBranchType.END);
      return this;
    }

    private void createBranch(final Argument<?, ?, CommandSender>[] args, final ArgumentBranchType type) {
      if (args == null || args.length == 0 || args[0] == null) {
        throw new IllegalArgumentException("Supplied Arguments to Branch cant be empty");
      }
      if (type == null) {
        throw new IllegalArgumentException("Invalid Argument Branch Type: " + type);
      }
      if (args[0].getArgumentType().equals(ArgumentType.LITERAL)) {
        throw new InvalidArgumentBranchException("invalid literal", args[0].getName());
      }
      branches.add(new ArgumentBranch(List.of(args), type));
    }

    @Override
    public Boolean parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return null;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + branches.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      return branches.equals(((BranchArgument) obj).branches);
    }

    @Override
    public String toString() {
      return toStringWithMore("branches=" + branches);
    }
  }

  @SuppressWarnings("rawtypes")
  public static class CommandArgument extends Argument<CommandResult, CommandArgument, CommandSender> {

    public CommandArgument(final String name) {
      super(name, CommandResult.class, ArgumentType.COMMAND);
      withSuggestions((info) -> {
        final String[] args = info.currentInput().split(" ");
        if (args.length <= 1) {
          return CommandAPI.getKnownCommandMap().keySet();
        }
        return CommandAPI.getCommandMap().tabComplete(info.source().sender(), info.currentInput(),
            info.source().location());
      });
    }

    @Override
    public CommandArgument getInstance() {
      return this;
    }

    @Override
    public CommandResult<Command, CommandSender> parseArgument(final CommandSource<CommandSender> source,
        final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String[] args = reader.getRemaining().split(" ");
      if (args.length == 0) {
        return null;
      }
      final Command cmd = Objects.requireNonNull(CommandAPI.getCommandMap().getCommand(args[0]));
      return new BukkitCommandResult<CommandSender>(cmd,
          args.length == 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length));
    }
  }

  public static class LiteralArgument extends Argument<String, LiteralArgument, CommandSender>
      implements LiteralArgumentType {

    private final String literal;

    public LiteralArgument(final String literal) {
      this(literal, literal);
    }

    public LiteralArgument(final String nodeName, final String literal) {
      super(nodeName, String.class, ArgumentType.LITERAL);

      if (literal == null || literal.isBlank()) {
        throw new IllegalArgumentException("The literal cant be empty");
      }
      this.literal = literal;
      this.setListed(false);
      withSuggestions(literal);
    }

    public static LiteralArgument of(final String literal) {
      return new LiteralArgument(literal);
    }

    public static LiteralArgument of(final String nodeName, final String literal) {
      return new LiteralArgument(nodeName, literal);
    }

    @Override
    public LiteralArgument getInstance() {
      return this;
    }

    public String getLiteral() {
      return literal;
    }

    @Override
    public String getHelpString() {
      return literal;
    }

    @Override
    public String parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String value = reader.readString();
      if (literal.equalsIgnoreCase(value)) {
        return literal;
      }
      throw createError(reader, value);
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + literal.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final LiteralArgument other = (LiteralArgument) obj;
      if (literal == null && other.literal != null) {
        return false;
      }
      return literal.equals(other.literal);
    }

    @Override
    public String toString() {
      return toStringWithMore("literal=" + literal);
    }
  }

  public static class MultiLiteralArgument extends Argument<String, MultiLiteralArgument, CommandSender>
      implements LiteralArgumentType {

    private final String[] literals;

    public MultiLiteralArgument(final String nodeName, final Set<String> literals) {
      this(nodeName, literals.toArray(String[]::new));
    }

    public MultiLiteralArgument(final String nodeName, final String... literals) {
      super(nodeName, String.class, ArgumentType.MULTI_LITERAL);

      if (literals == null || literals.length == 0) {
        throw new IllegalArgumentException("The literals cant be empty");
      }
      this.literals = literals;
      withSuggestions(literals);
    }

    public static MultiLiteralArgument of(final String literal) {
      return new MultiLiteralArgument(literal);
    }

    public static MultiLiteralArgument of(final String nodeName, final String... literal) {
      return new MultiLiteralArgument(nodeName, literal);
    }

    @Override
    public MultiLiteralArgument getInstance() {
      return this;
    }

    public String[] getLiterals() {
      return literals;
    }

    @Override
    public String parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String value = reader.readString();
      for (final String literal : literals) {
        if (literal.equalsIgnoreCase(value)) {
          return literal;
        }
      }
      throw createError(reader, value);
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + Arrays.hashCode(literals);
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final MultiLiteralArgument other = (MultiLiteralArgument) obj;
      return Arrays.equals(literals, other.literals);
    }

    @Override
    public String toString() {
      return toStringWithMore("literals=" + Arrays.toString(literals));
    }
  }

  public static class DynamicMultiLiteralArgument extends Argument<String, DynamicMultiLiteralArgument, CommandSender>
      implements LiteralArgumentType {

    private final Function<CommandSource<CommandSender>, Collection<String>> literals;

    public DynamicMultiLiteralArgument(final String nodeName,
        final Function<CommandSource<CommandSender>, Collection<String>> literals) {
      super(nodeName, String.class, ArgumentType.MULTI_LITERAL);

      if (literals == null) {
        throw new IllegalArgumentException("The literals Supplier cant be empty");
      }
      this.literals = literals;
      withSuggestions((info) -> {
        return literals.apply(info.source());
      });
    }

    public static DynamicMultiLiteralArgument of(final String nodeName,
        final Function<CommandSource<CommandSender>, Collection<String>> literal) {
      return new DynamicMultiLiteralArgument(nodeName, literal);
    }

    @Override
    public DynamicMultiLiteralArgument getInstance() {
      return this;
    }

    public Function<CommandSource<CommandSender>, Collection<String>> getLiteralsFunction() {
      return literals;
    }

    @Override
    public String parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      final String value = reader.readString();
      if (literals.apply(source).contains(value)) {
        return value;
      }
      throw createError(reader, value);
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + literals.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final DynamicMultiLiteralArgument other = (DynamicMultiLiteralArgument) obj;
      return literals.equals(other.literals);
    }

    @Override
    public String toString() {
      return toStringWithMore("literals=" + literals);
    }
  }

  @SuppressWarnings("rawtypes")
  public static class SwitchArgument extends Argument<List, SwitchArgument, CommandSender>
      implements LiteralArgumentType {

    private final String[] switches;

    public SwitchArgument(final String nodeName, final Set<String> switches) {
      this(nodeName, switches.toArray(String[]::new));
    }

    public SwitchArgument(final String nodeName, final String... switches) {
      super(nodeName, List.class, ArgumentType.SWITCH);

      if (switches == null || switches.length == 0) {
        throw new IllegalArgumentException("The switches cant be empty");
      }
      for (int i = 0; i < switches.length; i++) {
        switches[i] = switches[i].trim();
        if (!switches[i].startsWith("--") || !switches[i].startsWith("-")) {
          switches[i] = "-" + switches[i];
        }
      }
      this.switches = switches;
      withSuggestions(switches);
    }

    public static SwitchArgument of(final String switches) {
      return new SwitchArgument(switches);
    }

    public static SwitchArgument of(final String nodeName, final String... switches) {
      return new SwitchArgument(nodeName, switches);
    }

    @Override
    public SwitchArgument getInstance() {
      return this;
    }

    public String[] getSwitches() {
      return switches;
    }

    @Override
    public List<String> parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final List<String> foundSwitches = new ArrayList<>();
      int switchLen = 0;
      for (final String sw : switches) {
        switchLen = sw.length();
        if (reader.canRead(switchLen)
            && reader.getRemaining().startsWith(sw)
            && (reader.getRemaining().length() == switchLen
                || Character.isWhitespace(reader.getRemaining().charAt(switchLen)))) {
          foundSwitches.add(sw);
          reader.setCursor(reader.getCursor() + switchLen);
        }
      }
      return foundSwitches;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + Arrays.hashCode(switches);
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final SwitchArgument other = (SwitchArgument) obj;
      return Arrays.equals(switches, other.switches);
    }

    @Override
    public String toString() {
      return toStringWithMore("switches=" + Arrays.toString(switches));
    }
  }

  @SuppressWarnings("rawtypes")
  public static class FlagArgument extends Argument<HashMap, FlagArgument, CommandSender>
      implements LiteralArgumentType {

    private final String[] flags;

    public FlagArgument(final String nodeName, final Set<String> flags) {
      this(nodeName, flags.toArray(String[]::new));
    }

    public FlagArgument(final String nodeName, final String... flags) {
      super(nodeName, HashMap.class, ArgumentType.FLAG);

      if (flags == null || flags.length == 0) {
        throw new IllegalArgumentException("The flags cant be empty");
      }
      for (int i = 0; i < flags.length; i++) {
        flags[i] = flags[i].trim();
        if (!flags[i].startsWith("--") || !flags[i].startsWith("-")) {
          flags[i] = "-" + flags[i];
        }
      }
      this.flags = flags;
      withSuggestions(flags);
    }

    public static FlagArgument of(final String flags) {
      return new FlagArgument(flags);
    }

    public static FlagArgument of(final String nodeName, final String... flags) {
      return new FlagArgument(nodeName, flags);
    }

    @Override
    public FlagArgument getInstance() {
      return this;
    }

    public String[] getFlags() {
      return flags;
    }

    @Override
    public HashMap<String, String> parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final HashMap<String, String> foundFlags = new HashMap<>();
      int flagLen = 0;
      String remaining;
      for (final String flag : flags) {
        flagLen = flag.length();
        remaining = reader.getRemaining();
        if (!reader.canRead(flagLen)
            || !remaining.startsWith(flag)) {
          continue;
        }
        if (remaining.length() == flagLen) {
          reader.setCursor(reader.getCursor() + flagLen);
          foundFlags.put(flag, "true");
        } else if (Character.isWhitespace(remaining.charAt(flagLen))
            || remaining.charAt(flagLen) == '=') {
          reader.setCursor(reader.getCursor() + flagLen);
          String value = "true";
          if (reader.canRead() && (reader.peek() == '=' || Character.isWhitespace(reader.peek()))) {
            reader.skip();
            value = reader.readString();
          }
          foundFlags.put(flag, value);
        }
      }
      return foundFlags;
    }

    @Override
    public int hashCode() {
      return 31 * super.hashCode() + Arrays.hashCode(flags);
    }

    @Override
    public boolean equals(final Object obj) {
      if (!super.equals(obj)) {
        return false;
      }
      final FlagArgument other = (FlagArgument) obj;
      return Arrays.equals(flags, other.flags);
    }

    @Override
    public String toString() {
      return toStringWithMore("flags=" + Arrays.toString(flags));
    }
  }
}
