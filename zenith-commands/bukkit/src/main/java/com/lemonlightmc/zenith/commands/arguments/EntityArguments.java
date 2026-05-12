package com.lemonlightmc.zenith.commands.arguments;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.profile.PlayerProfile;

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;

public class EntityArguments {
  public static class PlayerArgument extends Argument<Player, PlayerArgument, CommandSender> {

    public PlayerArgument(final String name) {
      super(name, Player.class, ArgumentType.PLAYER);
      withSuggestions((info) -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());

    }

    @Override
    public PlayerArgument getInstance() {
      return this;
    }

    @Override
    public Player parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Bukkit.getPlayerExact(reader.readString());
    }
  }

  public static class PlayerProfileArgument extends Argument<PlayerProfile, PlayerProfileArgument, CommandSender> {
    public PlayerProfileArgument(final String name) {
      super(name, PlayerProfile.class, ArgumentType.PLAYERPROFILE);
      withSuggestions((info) -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());
    }

    @Override
    public PlayerProfileArgument getInstance() {
      return this;
    }

    @Override
    public PlayerProfile parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final Player player = Bukkit.getPlayerExact(reader.readString());
      return player == null ? null : player.getPlayerProfile();
    }
  }

  @SuppressWarnings("rawtypes")
  public static class EntitySelectorArgument extends Argument<List, EntitySelectorArgument, CommandSender> {

    public EntitySelectorArgument(final String name) {
      super(name, List.class, ArgumentType.ENTITY_SELECTOR);
      withSuggestions((info) -> Bukkit.getOnlinePlayers().stream().map(p -> p.getName()).toList());

    }

    @Override
    public EntitySelectorArgument getInstance() {
      return this;
    }

    @Override
    public List<Entity> parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
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
