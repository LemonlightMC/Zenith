package com.lemonlightmc.zenith.apis;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.additive.StringUtils.Replaceable;
import com.lemonlightmc.zenith.messages.MessageFormatter;

/**
 * Utility methods for sending formatted chat messages to command senders and online players.
 *
 * <p>Messages are processed through {@link MessageFormatter} for placeholder replacement, optional color formatting, and
 * value substitutions before delivery.
 */
public class ChatAPI {

  /**
   * Sends a formatted message to the specified sender.
   *
   * <p>Placeholders are resolved for the sender, colors are applied when supported, and the provided replacements are
   * applied before the message is sent. Empty or {@code null} messages after formatting are ignored.
   *
   * @param sender the recipient of the message
   * @param msg the message to format and send
   * @param replaceables values to substitute into the message
   */
  public static void send(
      final CommandSender sender,
      String msg,
      final Replaceable... replaceables) {

    msg = MessageFormatter.parsePlaceholder(sender, msg);
    final boolean hasColor = sender instanceof Player || sender instanceof ConsoleCommandSender;
    msg = MessageFormatter.format(msg, hasColor, true, replaceables);

    if (msg == null || msg.length() == 0)
      return;
    sender.sendMessage(msg);
  }

  /**
   * Sends multiple formatted messages to the specified sender in order.
   *
   * <p>Each message is processed individually using {@link #send(CommandSender, String, Replaceable...)}.
   *
   * @param sender the recipient of the messages
   * @param messages the messages to format and send
   * @param replaceables values to substitute into each message
   */
  public static void send(
      final CommandSender sender,
      final String[] messages,
      final Replaceable... replaceables) {
    for (int i = 0; i < messages.length; i++) {
      send(sender, messages[i], replaceables);
    }
  }

  /**
   * Broadcasts a formatted message to all online players.
   *
   * <p>Placeholders and replacements are resolved per player before delivery. Empty or {@code null} messages after
   * formatting are skipped for that player.
   *
   * @param msg the message to format and broadcast
   * @param replaceables values to substitute into the message
   */
  public static void broadcast(String msg, final Replaceable... replaceables) {
    for (final Player p : Bukkit.getOnlinePlayers()) {
      if (p == null) {
        continue;
      }
      msg = MessageFormatter.parsePlaceholder(p, msg);
      msg = MessageFormatter.format(msg, true, true, replaceables);
      if (msg == null || msg.length() == 0) {
        continue;
      }
      p.sendMessage(msg);
    }
  }

  /**
   * Broadcasts multiple formatted messages to all online players in order.
   *
   * <p>Each message is processed individually using {@link #broadcast(String, Replaceable...)}.
   *
   * @param messages the messages to format and broadcast
   * @param replaceables values to substitute into each message
   */
  public static void broadcast(
      final String[] messages,
      final Replaceable... replaceables) {
    for (int i = 0; i < messages.length; i++) {
      broadcast(messages[i], replaceables);
    }
  }
}
