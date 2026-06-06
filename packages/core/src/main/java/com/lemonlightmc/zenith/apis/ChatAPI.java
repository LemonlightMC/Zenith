package com.lemonlightmc.zenith.apis;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.utils.StringUtils.Replaceable;

public class ChatAPI {
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

  public static void send(
      final CommandSender sender,
      final String[] messages,
      final Replaceable... replaceables) {
    for (int i = 0; i < messages.length; i++) {
      send(sender, messages[i], replaceables);
    }
  }

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

  public static void broadcast(
      final String[] messages,
      final Replaceable... replaceables) {
    for (int i = 0; i < messages.length; i++) {
      broadcast(messages[i], replaceables);
    }
  }
}
