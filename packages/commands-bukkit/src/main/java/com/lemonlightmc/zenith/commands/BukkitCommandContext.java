package com.lemonlightmc.zenith.commands;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.apis.ChatAPI;
import com.lemonlightmc.zenith.commands.executors.CommandContext;
import com.lemonlightmc.zenith.commands.executors.RootCommand;
import com.lemonlightmc.zenith.utils.WorldUtils;

public class BukkitCommandContext<S extends CommandSender> extends CommandContext<S> {

    private final Player player;
    private Entity entity;
    private Location location;

    public BukkitCommandContext(RootCommand<?, S> cmd, String label, S sender) {
        super(cmd, label, sender);
        if (sender instanceof final Player player2) {
            this.player = player2;
        } else {
            this.player = null;
        }
        this.location = WorldUtils.getLocation(sender);
        this.entity = WorldUtils.getEntity(sender);
    }

    public BukkitCommandContext(RootCommand<?, S> cmd, String label, S sender, Player player) {
        super(cmd, label, sender);
        if (player != null) {
            this.player = player;
        } else if (sender instanceof final Player player2) {
            this.player = player2;
        } else {
            this.player = null;
        }
    }

    @Override
    public boolean hasPermission(String perm) {
        return perm != null && sender.hasPermission(perm);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends S> CommandContext<T> copyFor(T newSender) {
        return new BukkitCommandContext<T>((RootCommand<?, T>) cmd, label, newSender);

    }

    @Override
    public CommandContext<S> copy() {
        return new BukkitCommandContext<S>(cmd, label, sender);
    }

    @Override
    public void sendMessage(String message) {
        ChatAPI.send(sender, formatMessage(message));
    }

    public Player player() {
        return player;
    }

    /**
     * Checks whether the underlying sender has the specified permission.
     *
     * @param perm the permission node to check, or {@code null} or empty for no
     *             restriction
     * @return {@code true} if the sender has permission or no permission is
     *         required; {@code false} otherwise
     */
    public boolean isPlayer() {
        return player != null;
    }
    public Location location() {
        return location;
    }

    public org.bukkit.World world() {
        return location == null ? null : location.getWorld();
    }

    public Entity entity() {
        return entity;
    }

    public UUID uuid() {
        return this.isConsole() ? CONSOLE_UUID : this.player().getUniqueId();
    }

    public boolean isConsole() {
        return sender instanceof ConsoleCommandSender;
    }
}
