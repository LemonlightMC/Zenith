package com.lemonlightmc.zenith.commands.arguments;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.ScoreboardSlot;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;

public class ScoreArguments {

  public static class TeamArgument extends Argument<Team, TeamArgument, CommandSender> {

    public TeamArgument(final String name) {
      super(name, Team.class, ArgumentType.TEAM);
      withSuggestions((info) -> Bukkit.getScoreboardManager().getMainScoreboard().getTeams().stream()
          .map((v) -> v.getName()).toList());
    }

    @Override
    public TeamArgument getInstance() {
      return this;
    }

    @Override
    public Team parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Bukkit.getScoreboardManager().getMainScoreboard().getTeam(reader.readString());
    }
  }

  public static class ScoreboardSlotArgument extends Argument<ScoreboardSlot, ScoreboardSlotArgument, CommandSender> {

    public ScoreboardSlotArgument(final String name) {
      super(name, ScoreboardSlot.class, ArgumentType.SCOREBOARD_SLOT);
      withSuggestions(ScoreboardSlot.keys());
    }

    @Override
    public ScoreboardSlotArgument getInstance() {
      return this;
    }

    @Override
    public ScoreboardSlot parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return ScoreboardSlot.of(reader.readString());
    }
  }

  public static class ObjectiveArgument extends Argument<Objective, ObjectiveArgument, CommandSender> {

    public ObjectiveArgument(final String name) {
      super(name, Objective.class, ArgumentType.OBJECTIVE);
      withSuggestions((info) -> Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().stream()
          .map((v) -> v.getName()).toList());
    }

    @Override
    public ObjectiveArgument getInstance() {
      return this;
    }

    @Override
    public Objective parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
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

  public static class CriteriaArgument extends Argument<Criteria, CriteriaArgument, CommandSender> {

    public CriteriaArgument(final String name) {
      super(name, Criteria.class, ArgumentType.CRITERIA);
      withSuggestions((info) -> Bukkit.getScoreboardManager().getMainScoreboard().getObjectives().stream()
          .map((v) -> v.getTrackedCriteria().getName()).toList());
    }

    @Override
    public CriteriaArgument getInstance() {
      return this;
    }

    @Override
    public Criteria parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Criteria.create(reader.readString()));
    }
  }
}
