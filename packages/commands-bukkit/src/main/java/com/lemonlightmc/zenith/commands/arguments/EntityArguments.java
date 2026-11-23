package com.lemonlightmc.zenith.commands.arguments;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import com.lemonlightmc.zenith.utils.actions.ConditionContext;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;

/**
 * Argument types for resolving Bukkit entities and player profiles.
 */
public class EntityArguments {
  /**
   * Argument that resolves an exact online player by name.
   */
  public static class PlayerArgument extends Argument<Player, PlayerArgument, CommandSender> {

    /**
     * Creates a new player argument.
     *
     * @param name the argument name
     */
    public PlayerArgument(final String name) {
      super(name, Player.class, ArgumentType.PLAYER);
      withSuggestions((info) -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());

    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public PlayerArgument self() {
      return this;
    }

    /**
     * Parses the argument value as an online player.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the resolved player, or {@code null} if not found
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Player parseArgument(final ConditionContext<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Bukkit.getPlayerExact(reader.readString());
    }
  }

  /**
   * Argument that resolves the {@link PlayerProfile} of an exact online player.
   */
  public static class PlayerProfileArgument extends Argument<PlayerProfile, PlayerProfileArgument, CommandSender> {
    /**
     * Creates a new player profile argument.
     *
     * @param name the argument name
     */
    public PlayerProfileArgument(final String name) {
      super(name, PlayerProfile.class, ArgumentType.PLAYERPROFILE);
      withSuggestions((info) -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public PlayerProfileArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a player profile.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the resolved player profile, or {@code null} if the player is not found
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public PlayerProfile parseArgument(final ConditionContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final Player player = Bukkit.getPlayerExact(reader.readString());
      return player == null ? null : player.getPlayerProfile();
    }
  }

  /**
   * Argument that resolves entities from an exact player name or a Bukkit entity selector.
   */
  @SuppressWarnings("rawtypes")
  public static class EntitySelectorArgument extends Argument<List, EntitySelectorArgument, CommandSender> {

    /**
     * Creates a new entity selector argument.
     *
     * @param name the argument name
     */
    public EntitySelectorArgument(final String name) {
      super(name, List.class, ArgumentType.ENTITY_SELECTOR);
      withSuggestions((info) -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());

    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public EntitySelectorArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a list of selected entities.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the selected entities
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public List<Entity> parseArgument(final ConditionContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String str = reader.readString();
      final Player player = Bukkit.getPlayerExact(str);
      if (player != null) {
        return List.of(player);
      }
      return Bukkit.selectEntities(source.sender(), str);
    }
  }
}
