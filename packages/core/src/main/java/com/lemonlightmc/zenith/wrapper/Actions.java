package com.lemonlightmc.zenith.wrapper;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.lemonlightmc.zenith.apis.ActionbarAPI;
import com.lemonlightmc.zenith.apis.ChatAPI;
import com.lemonlightmc.zenith.apis.TitleAPI;
import com.lemonlightmc.zenith.apis.TitleAPI.TitleInfo;
import com.lemonlightmc.zenith.wrapper.Conditions.ConditionSet;

public class Actions {
  public static abstract class Action<T> {

    private final ConditionSet<T> conditions;

    public Action(final ConditionSet<T> conditions) {
      this.conditions = conditions;
    }

    public ConditionSet<T> getConditions() {
      return conditions;
    }

    public void execute(final T player) {
      if (conditions != null && !conditions.test(player)) {
        return;
      }
      run(player);
    }

    protected abstract void run(T player);
  }

  public static class MessageAction extends Action<CommandSender> {

    private final boolean broadcast;
    private final String message;

    public MessageAction(final String message) {
      this(message, false, null);
    }

    public MessageAction(final String message, final boolean broadcast) {
      this(message, broadcast, null);
    }

    public MessageAction(final String message, final boolean broadcast, final ConditionSet<CommandSender> conditions) {
      super(conditions);
      this.message = message;
      this.broadcast = broadcast;
    }

    @Override
    protected void run(final CommandSender player) {
      if (broadcast) {
        ChatAPI.broadcast(message);
      } else {
        ChatAPI.send(player, message);
      }
    }
  }

  public static class TitleAction extends Action<Player> {

    private final boolean broadcast;
    private final TitleInfo info;

    public TitleAction(final TitleInfo info) {
      this(info, false, null);
    }

    public TitleAction(final TitleInfo info, final boolean broadcast) {
      this(info, broadcast, null);
    }

    public TitleAction(final TitleInfo info, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.info = info;
      this.broadcast = broadcast;
    }

    @Override
    protected void run(final Player player) {
      if (broadcast) {
        TitleAPI.broadcast(info);
      } else {
        TitleAPI.send(player, info);
      }
    }
  }

  public static class ActionbarAction extends Action<CommandSender> {

    private final boolean broadcast;
    private final String message;

    public ActionbarAction(final String message) {
      this(message, false, null);
    }

    public ActionbarAction(final String message, final boolean broadcast) {
      this(message, broadcast, null);
    }

    public ActionbarAction(final String message, final boolean broadcast,
        final ConditionSet<CommandSender> conditions) {
      super(conditions);
      this.message = message;
      this.broadcast = broadcast;
    }

    @Override
    protected void run(final CommandSender player) {
      if (broadcast) {
        ActionbarAPI.broadcast(message);
      } else {
        ActionbarAPI.send(null, message);
      }
    }
  }

  public static class TeleportAction extends Action<Player> {

    private final boolean all;
    private final Location loc;

    public TeleportAction(final Location loc) {
      this(loc, false, null);
    }

    public TeleportAction(final Location loc, final boolean all) {
      this(loc, all, null);
    }

    public TeleportAction(final Location loc, final boolean all, final ConditionSet<Player> conditions) {
      super(conditions);
      this.loc = loc;
      this.all = all;
    }

    @Override
    protected void run(final Player player) {
      if (all) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          p.teleport(loc, TeleportCause.PLUGIN);
        }
      } else {
        player.teleport(loc, TeleportCause.PLUGIN);
      }
    }
  }
}
