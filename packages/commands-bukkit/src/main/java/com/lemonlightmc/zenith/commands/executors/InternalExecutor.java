package com.lemonlightmc.zenith.commands.executors;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.commands.CommandAPI;
import com.lemonlightmc.zenith.commands.SimpleCommand;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;

public class InternalExecutor extends Command {

  private final SimpleCommand cmd;
  private final boolean runAsync;

  public InternalExecutor(final SimpleCommand cmd, final boolean runAsync) {
    super(cmd == null ? null : cmd.getKey());
    if (cmd == null) {
      throw new IllegalArgumentException("Command cannot be null");
    }
    this.cmd = cmd;
    super.setAliases(List.copyOf(cmd.getAliases()));
    this.runAsync = runAsync;
  }

  public SimpleCommand getSimpleCommand() {
    return cmd;
  }

  public String getNamespace() {
    return cmd.getNamespace();
  }

  @Override
  public String getLabel() {
    return super.getName();
  }

  @Override
  public boolean setLabel(final String label) {
    if (super.setLabel(label)) {
      cmd.setKey(label);
      return true;
    }
    return false;
  }

  @Override
  public boolean execute(final CommandSender sender, final String label0, final String[] args) {
    if (!CommandAPI.getHandler().shouldHandle(cmd, sender, label0)) {
      return true;
    }
    if (!runAsync) {
      CommandAPI.getHandler().run(cmd, sender, args);
      return true;
    }
    GlobalScheduler
        .runAsync(() -> {
          CommandAPI.getHandler().run(cmd, sender, args);
        });
    return true;
  }

  @Override
  public List<String> tabComplete(
      final CommandSender sender,
      final String label,
      final String[] args,
      final Location location) {
    if (!CommandAPI.getHandler().shouldHandle(cmd, sender, label)) {
      return List.of();
    }
    return CommandAPI.getHandler().tabComplete(cmd, sender, args);
  }

  @Override
  public String toString() {
    return cmd.getName().toString() + "(" + ZenithProvider.instance().getDescription().getFullName() + ")";
  }

  @Override
  public int hashCode() {
    return 31 + cmd.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    return cmd.equals(((InternalExecutor) obj).cmd);
  }
}
