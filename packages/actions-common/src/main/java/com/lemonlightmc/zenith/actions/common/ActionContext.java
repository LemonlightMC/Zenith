package com.lemonlightmc.zenith.actions.common;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.apis.ChatAPI;
import com.lemonlightmc.zenith.utils.WorldUtils;

public class ActionContext {

    private volatile boolean stopped;
    private final CommandSender sender;
    private final Player player;
    private Entity entity;
    private Location location;

    private static final UUID CONSOLE_UUID = UUID.nameUUIDFromBytes("zenith-console".getBytes());

    public ActionContext(final CommandSender sender) {
        if (sender == null) {
            throw new IllegalArgumentException("Action Sender cannot be null");
        }
        this.sender = sender;
        if (sender instanceof final Player player2) {
            this.player = player2;
        } else {
            this.player = null;
        }
        this.location = WorldUtils.getLocation(sender);
        this.entity = WorldUtils.getEntity(sender);
    }

    public ActionContext(final CommandSender sender, final Player player) {
        this.sender = sender;
        if (sender == null) {
            throw new IllegalArgumentException("Action Sender cannot be null");
        }
        if (player != null) {
            this.player = player;
        } else if (sender instanceof final Player player2) {
            this.player = player2;
        } else {
            this.player = null;
        }
    }

    public Player player() {
        return player;
    }

    /**
     * Returns the underlying sender associated with this context.
     *
     * @return the command sender
     */
    public CommandSender sender() {
        return sender;
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

    public boolean isConsole() {
        return sender instanceof ConsoleCommandSender;
    }

    public boolean isStopped() {
        return stopped;
    }

    public void stop() {
        this.stopped = true;
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

    public boolean hasPermission(final String perm) {
        return perm != null && sender.hasPermission(perm);
    }

    /**
     * Sends a message to the sender.
     *
     * @param str the message to send
     */
    public void sendMessage(final String str) {
        ChatAPI.send(sender, str);
    }

    /**
     * Sends a command error message to the underlying sender.
     *
     * @param e the command exception describing the error
     */
    public void sendError(final Exception e) {
        sendMessage(e.getMessage());
    }

    /**
     * Creates a copy of this source for a different sender while preserving
     * location and entity context.
     *
     * @param <T>       the new sender type
     * @param newSender the sender to associate with the copied context
     * @return a copied context for the new sender
     */
    public <T extends CommandSender> ActionContext copyFor(final T newSender) {
        return new ActionContext(newSender);
    }

    /**
     * Creates a copy of this context.
     *
     * @return a copied context
     */
    public ActionContext copy() {
        return new ActionContext(sender, player);
    }

}
