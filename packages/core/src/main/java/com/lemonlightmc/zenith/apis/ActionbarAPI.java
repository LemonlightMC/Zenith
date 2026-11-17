package com.lemonlightmc.zenith.apis;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.StringUtils.Replaceable;
import com.lemonlightmc.zenith.messages.MessageFormatter;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Utility methods for sending formatted action bar messages to players.
 *
 * <p>Messages are formatted with {@link MessageFormatter} and delivered through the Spigot action bar channel.
 */
public class ActionbarAPI {

  /** Tracks repeating action bar tasks per player. */
  private static final Map<UUID, BukkitTask> PENDING_MESSAGES = new HashMap<>();

  /**
   * Broadcasts a formatted action bar message to all online players.
   *
   * @param msg the message to format and send
   * @param replaceables values to substitute into the message
   */
  public static void broadcast(
      final String msg,
      final Replaceable... replaceables) {
    for (final Player player : Bukkit.getOnlinePlayers()) {
      send(player, msg, replaceables);
    }
  }

  /**
   * Sends a formatted action bar message to a player.
   *
   * @param p the player to receive the message
   * @param msg the message to format and send
   * @param replaceables values to substitute into the message
   */
  public static void send(
      final Player p,
      final String msg,
      final Replaceable... replaceables) {
    p.spigot().sendMessage(
        ChatMessageType.ACTION_BAR,
        TextComponent.fromLegacy(MessageFormatter.format(msg, false, true, replaceables)));
  }

  /**
   * Sends a formatted action bar message to a player for a specified duration.
   *
   * <p>If a previous timed action bar task exists for the player, it is cancelled before scheduling a new one.
   *
   * @param p the player to receive the message
   * @param msg the message to format and send
   * @param duration the display duration control used by this implementation
   * @param replaceables values to substitute into the message
   */
  public static void send(
      final Player p,
      String msg,
      final int duration,
      final Replaceable... replaceables) {
    if (PENDING_MESSAGES.containsKey(p.getUniqueId())) {
      final BukkitTask task = PENDING_MESSAGES.get(p.getUniqueId());
      if (task != null) {
        task.cancel();
      }
    }
    msg = MessageFormatter.format(msg, false, true, replaceables);
    if (msg == null || msg.length() == 0) {
      return;
    }
    final BaseComponent component = TextComponent.fromLegacy(msg);
    if (duration == -1) {
      // Send once and do not schedule repeating updates
      p
          .spigot()
          .sendMessage(
              ChatMessageType.ACTION_BAR,
              component);
      return;
    }

    final BukkitTask messageTask = new BukkitRunnable() {
      private int count = 0;

      @Override
      public void run() {
        if (count >= (duration - 3)) {
          this.cancel();
        }
        p
            .spigot()
            .sendMessage(
                ChatMessageType.ACTION_BAR,
                component);
        count++;
      }
    }
        .runTaskTimer(ZenithProvider.instance(), 0L, 20L);
    PENDING_MESSAGES.put(p.getUniqueId(), messageTask);
  }
}
