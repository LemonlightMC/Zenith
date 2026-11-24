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
import com.lemonlightmc.zenith.actions.ExecutionContext;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.CommandResult;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.commands.exceptions.InvalidArgumentBranchException;

/**
 * Specialized argument types for branching, commands, literals, switches, and
 * flags.
 */
public class SpecialArguments {

  /**
   * Describes a branch of arguments used by {@link BranchArgument}.
   *
   * @param args the arguments in the branch
   * @param type the branch behavior type
   */
  public static record ArgumentBranch(List<Argument<?, ?, CommandSender>> args, ArgumentBranchType type) {
  }

  /**
   * Defines how a branch behaves during argument parsing.
   */
  public static enum ArgumentBranchType {
    /** Processes the branch once. */
    ONCE(),
    /** Allows the branch to be processed repeatedly. */
    LOOPING(),
    /** Ends parsing after the branch is processed. */
    END();
  }

  /**
   * Marker interface for literal-style argument types.
   */
  public interface LiteralArgumentType {

  }

  /**
   * Argument that groups alternative argument branches for parsing.
   */
  public static class BranchArgument extends Argument<Boolean, BranchArgument, CommandSender> {

    private final List<ArgumentBranch> branches;

    /**
     * Creates a new branch argument.
     *
     * @param name the argument name
     */
    public BranchArgument(final String name) {
      super(name, null, ArgumentType.BRANCH);
      branches = new ArrayList<>();
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public BranchArgument self() {
      return this;
    }

    /**
     * Returns the configured argument branches.
     *
     * @return the argument branches
     */
    public List<ArgumentBranch> getBranches() {
      return branches;
    }

    /**
     * Removes all configured branches.
     *
     * @return this argument
     */
    public BranchArgument clear() {
      branches.clear();
      return this;
    }

    /**
     * Adds a looping branch with the given arguments.
     *
     * @param args the branch arguments
     * @return this argument
     */
    @SuppressWarnings("unchecked")
    public BranchArgument branch(final Argument<?, ?, CommandSender>... args) {
      createBranch(args, ArgumentBranchType.LOOPING);
      return this;
    }

    /**
     * Adds a branch with the given type and arguments.
     *
     * @param type the branch type
     * @param args the branch arguments
     * @return this argument
     */
    @SuppressWarnings("unchecked")
    public BranchArgument branch(final ArgumentBranchType type, final Argument<?, ?, CommandSender>... args) {
      createBranch(args, type);
      return this;
    }

    /**
     * Adds a looping branch with the given arguments.
     *
     * @param args the branch arguments
     * @return this argument
     */
    @SuppressWarnings("unchecked")
    public BranchArgument loopingBranch(final Argument<?, ?, CommandSender>... args) {
      createBranch(args, ArgumentBranchType.LOOPING);
      return this;
    }

    /**
     * Adds a one-time branch with the given arguments.
     *
     * @param args the branch arguments
     * @return this argument
     */
    @SuppressWarnings("unchecked")
    public BranchArgument onceBranch(final Argument<?, ?, CommandSender>... args) {
      createBranch(args, ArgumentBranchType.ONCE);
      return this;
    }

    /**
     * Adds a terminating branch with the given arguments.
     *
     * @param args the branch arguments
     * @return this argument
     */
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

    /**
     * Parses this branch argument.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return {@code null} because branch parsing is handled externally
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Boolean parseArgument(final ExecutionContext<CommandSender> source, final StringReader reader,
        final String key)
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

  /**
   * Argument that parses and resolves a command invocation.
   */
  @SuppressWarnings("rawtypes")
  public static class CommandArgument extends Argument<CommandResult, CommandArgument, CommandSender> {

    /**
     * Creates a new command argument.
     *
     * @param name the argument name
     */
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

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public CommandArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a command result.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed command result, or {@code null} if no command is present
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public CommandResult<Command, CommandSender> parseArgument(final ExecutionContext<CommandSender> source,
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

  /**
   * Argument that matches a single literal token.
   */
  public static class LiteralArgument extends Argument<String, LiteralArgument, CommandSender>
      implements LiteralArgumentType {

    private final String literal;

    /**
     * Creates a new literal argument using the same value as its name and literal.
     *
     * @param literal the literal value
     */
    public LiteralArgument(final String literal) {
      this(literal, literal);
    }

    /**
     * Creates a new literal argument.
     *
     * @param nodeName the argument name
     * @param literal  the literal value
     * @throws IllegalArgumentException if {@code literal} is {@code null} or blank
     */
    public LiteralArgument(final String nodeName, final String literal) {
      super(nodeName, String.class, ArgumentType.LITERAL);

      if (literal == null || literal.isBlank()) {
        throw new IllegalArgumentException("The literal cant be empty");
      }
      this.literal = literal;
      this.setListed(false);
      withSuggestions(literal);
    }

    /**
     * Creates a literal argument using the same value as its name and literal.
     *
     * @param literal the literal value
     * @return a new literal argument
     */
    public static LiteralArgument of(final String literal) {
      return new LiteralArgument(literal);
    }

    /**
     * Creates a literal argument.
     *
     * @param nodeName the argument name
     * @param literal  the literal value
     * @return a new literal argument
     */
    public static LiteralArgument of(final String nodeName, final String literal) {
      return new LiteralArgument(nodeName, literal);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public LiteralArgument self() {
      return this;
    }

    /**
     * Returns the literal value matched by this argument.
     *
     * @return the literal value
     */
    public String getLiteral() {
      return literal;
    }

    /**
     * Returns the help string for this literal argument.
     *
     * @return the literal help string
     */
    @Override
    public String getHelpString() {
      return literal;
    }

    /**
     * Parses the argument value as the configured literal.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the matched literal
     * @throws CommandSyntaxException if the input does not match the literal
     */
    @Override
    public String parseArgument(final ExecutionContext<CommandSender> source, final StringReader reader,
        final String key)
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

  /**
   * Argument that matches one of several fixed literal tokens.
   */
  public static class MultiLiteralArgument extends Argument<String, MultiLiteralArgument, CommandSender>
      implements LiteralArgumentType {

    private final String[] literals;

    /**
     * Creates a new multi-literal argument from a set of literals.
     *
     * @param nodeName the argument name
     * @param literals the literal values
     */
    public MultiLiteralArgument(final String nodeName, final Set<String> literals) {
      this(nodeName, literals.toArray(String[]::new));
    }

    /**
     * Creates a new multi-literal argument.
     *
     * @param nodeName the argument name
     * @param literals the literal values
     * @throws IllegalArgumentException if {@code literals} is {@code null} or empty
     */
    public MultiLiteralArgument(final String nodeName, final String... literals) {
      super(nodeName, String.class, ArgumentType.MULTI_LITERAL);

      if (literals == null || literals.length == 0) {
        throw new IllegalArgumentException("The literals cant be empty");
      }
      this.literals = literals;
      withSuggestions(literals);
    }

    /**
     * Creates a multi-literal argument with a single literal value.
     *
     * @param literal the literal value
     * @return a new multi-literal argument
     */
    public static MultiLiteralArgument of(final String literal) {
      return new MultiLiteralArgument(literal);
    }

    /**
     * Creates a multi-literal argument.
     *
     * @param nodeName the argument name
     * @param literal  the literal values
     * @return a new multi-literal argument
     */
    public static MultiLiteralArgument of(final String nodeName, final String... literal) {
      return new MultiLiteralArgument(nodeName, literal);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public MultiLiteralArgument self() {
      return this;
    }

    /**
     * Returns the literal values matched by this argument.
     *
     * @return the literal values
     */
    public String[] getLiterals() {
      return literals;
    }

    /**
     * Parses the argument value as one of the configured literals.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the matched literal
     * @throws CommandSyntaxException if the input does not match any literal
     */
    @Override
    public String parseArgument(final ExecutionContext<CommandSender> source, final StringReader reader,
        final String key)
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

  /**
   * Argument that matches a literal token from a dynamically supplied set.
   */
  public static class DynamicMultiLiteralArgument extends Argument<String, DynamicMultiLiteralArgument, CommandSender>
      implements LiteralArgumentType {

    private final Function<ExecutionContext<CommandSender>, Collection<String>> literals;

    /**
     * Creates a new dynamic multi-literal argument.
     *
     * @param nodeName the argument name
     * @param literals the function supplying allowed literal values
     * @throws IllegalArgumentException if {@code literals} is {@code null}
     */
    public DynamicMultiLiteralArgument(final String nodeName,
        final Function<ExecutionContext<CommandSender>, Collection<String>> literals) {
      super(nodeName, String.class, ArgumentType.MULTI_LITERAL);

      if (literals == null) {
        throw new IllegalArgumentException("The literals Supplier cant be empty");
      }
      this.literals = literals;
      withSuggestions((info) -> {
        return literals.apply(info.source());
      });
    }

    /**
     * Creates a dynamic multi-literal argument.
     *
     * @param nodeName the argument name
     * @param literal  the function supplying allowed literal values
     * @return a new dynamic multi-literal argument
     */
    public static DynamicMultiLiteralArgument of(final String nodeName,
        final Function<ExecutionContext<CommandSender>, Collection<String>> literal) {
      return new DynamicMultiLiteralArgument(nodeName, literal);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public DynamicMultiLiteralArgument self() {
      return this;
    }

    /**
     * Returns the function that supplies allowed literal values.
     *
     * @return the literal supplier function
     */
    public Function<ExecutionContext<CommandSender>, Collection<String>> getLiteralsFunction() {
      return literals;
    }

    /**
     * Parses the argument value as one of the dynamically supplied literals.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the matched literal
     * @throws CommandSyntaxException if the input does not match any supplied
     *                                literal
     */
    @Override
    public String parseArgument(final ExecutionContext<CommandSender> source, final StringReader reader,
        final String key)
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

  /**
   * Argument that parses switch-style tokens from command input.
   */
  @SuppressWarnings("rawtypes")
  public static class SwitchArgument extends Argument<List, SwitchArgument, CommandSender>
      implements LiteralArgumentType {

    private final String[] switches;

    /**
     * Creates a new switch argument from a set of switch names.
     *
     * @param nodeName the argument name
     * @param switches the switch names
     */
    public SwitchArgument(final String nodeName, final Set<String> switches) {
      this(nodeName, switches.toArray(String[]::new));
    }

    /**
     * Creates a new switch argument.
     *
     * @param nodeName the argument name
     * @param switches the switch names
     * @throws IllegalArgumentException if {@code switches} is {@code null} or empty
     */
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

    /**
     * Creates a switch argument with a single switch name.
     *
     * @param switches the switch name
     * @return a new switch argument
     */
    public static SwitchArgument of(final String switches) {
      return new SwitchArgument(switches);
    }

    /**
     * Creates a switch argument.
     *
     * @param nodeName the argument name
     * @param switches the switch names
     * @return a new switch argument
     */
    public static SwitchArgument of(final String nodeName, final String... switches) {
      return new SwitchArgument(nodeName, switches);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public SwitchArgument self() {
      return this;
    }

    /**
     * Returns the configured switch tokens.
     *
     * @return the switch tokens
     */
    public String[] getSwitches() {
      return switches;
    }

    /**
     * Parses matching switch tokens from the input.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the matched switch tokens
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public List<String> parseArgument(final ExecutionContext<CommandSender> source, final StringReader reader,
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

  /**
   * Argument that parses flag-style tokens and optional values from command
   * input.
   */
  @SuppressWarnings("rawtypes")
  public static class FlagArgument extends Argument<HashMap, FlagArgument, CommandSender>
      implements LiteralArgumentType {

    private final String[] flags;

    /**
     * Creates a new flag argument from a set of flag names.
     *
     * @param nodeName the argument name
     * @param flags    the flag names
     */
    public FlagArgument(final String nodeName, final Set<String> flags) {
      this(nodeName, flags.toArray(String[]::new));
    }

    /**
     * Creates a new flag argument.
     *
     * @param nodeName the argument name
     * @param flags    the flag names
     * @throws IllegalArgumentException if {@code flags} is {@code null} or empty
     */
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

    /**
     * Creates a flag argument with a single flag name.
     *
     * @param flags the flag name
     * @return a new flag argument
     */
    public static FlagArgument of(final String flags) {
      return new FlagArgument(flags);
    }

    /**
     * Creates a flag argument.
     *
     * @param nodeName the argument name
     * @param flags    the flag names
     * @return a new flag argument
     */
    public static FlagArgument of(final String nodeName, final String... flags) {
      return new FlagArgument(nodeName, flags);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public FlagArgument self() {
      return this;
    }

    /**
     * Returns the configured flag tokens.
     *
     * @return the flag tokens
     */
    public String[] getFlags() {
      return flags;
    }

    /**
     * Parses matching flag tokens and their values from the input.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return a map of matched flag tokens to parsed values
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public HashMap<String, String> parseArgument(final ExecutionContext<CommandSender> source,
        final StringReader reader,
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
