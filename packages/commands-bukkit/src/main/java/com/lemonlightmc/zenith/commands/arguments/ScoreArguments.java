package com.lemonlightmc.zenith.commands.arguments;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.lemonlightmc.zenith.utils.actions.ConditionContext;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.unfinished.ScoreboardSlot;

/**
 * Argument types for scoreboard-related values.
 */
public class ScoreArguments {

  /**
   * Argument that resolves a scoreboard {@link Team} by name.
   */
  public static class TeamArgument extends Argument<Team, TeamArgument, CommandSender> {

    /**
     * Creates a new team argument.
     *
     * @param name the argument name
     */
    public TeamArgument(final String name) {
      super(name, Team.class, ArgumentType.TEAM);
      withSuggestions((info) -> Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream()
          .map((v) -> v.getName()).toList());
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public TeamArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a scoreboard team.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed team
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Team parseArgument(final ConditionContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(reader.readString());
    }
  }

  /**
   * Argument that resolves a scoreboard slot identifier.
   */
  public static class ScoreboardSlotArgument extends Argument<ScoreboardSlot, ScoreboardSlotArgument, CommandSender> {

    /**
     * Creates a new scoreboard slot argument.
     *
     * @param name the argument name
     */
    public ScoreboardSlotArgument(final String name) {
      super(name, ScoreboardSlot.class, ArgumentType.SCOREBOARD_SLOT);
      withSuggestions(ScoreboardSlot.keys());
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public ScoreboardSlotArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a scoreboard slot.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed scoreboard slot
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public ScoreboardSlot parseArgument(final ConditionContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return ScoreboardSlot.of(reader.readString());
    }
  }

  /**
   * Argument that resolves a scoreboard objective by name or display slot.
   */
  public static class ObjectiveArgument extends Argument<Objective, ObjectiveArgument, CommandSender> {

    /**
     * Creates a new objective argument.
     *
     * @param name the argument name
     */
    public ObjectiveArgument(final String name) {
      super(name, Objective.class, ArgumentType.OBJECTIVE);
      withSuggestions((info) -> Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().stream()
          .map((v) -> v.getName()).toList());
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public ObjectiveArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a scoreboard objective.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed objective
     * @throws CommandSyntaxException if parsing fails or the objective cannot be resolved
     */
    @Override
    public Objective parseArgument(final ConditionContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String value = reader.readString();
      Objective objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(value);
      if (objective != null) {
        return objective;
      }
      final ScoreboardSlot slot = ScoreboardSlot.of(value);
      if (slot != null) {
        final DisplaySlot slot2 = slot.getDisplaySlot();
        if (slot2 != null) {
          objective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective(slot2);
          if (objective != null) {
            return objective;
          }
        }
      }
      throw createError(reader, value);
    }
  }

  /**
   * Argument that resolves a scoreboard criteria value.
   */
  public static class CriteriaArgument extends Argument<Criteria, CriteriaArgument, CommandSender> {

    /**
     * Creates a new criteria argument.
     *
     * @param name the argument name
     */
    public CriteriaArgument(final String name) {
      super(name, Criteria.class, ArgumentType.CRITERIA);
      withSuggestions((info) -> Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().stream()
          .map((v) -> v.getTrackedCriteria().getName()).toList());
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public CriteriaArgument self() {
      return this;
    }

    /**
     * Parses the argument value as scoreboard criteria.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed criteria
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Criteria parseArgument(final ConditionContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Criteria.create(reader.readString()));
    }
  }
}
