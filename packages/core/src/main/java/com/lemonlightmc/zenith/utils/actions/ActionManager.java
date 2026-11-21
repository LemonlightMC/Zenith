package com.lemonlightmc.zenith.utils.actions;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;
import com.lemonlightmc.zenith.utils.actions.Action.ActionContext;

/**
 * Manages and executes named action chains for player contexts.
 */
public class ActionManager {
  static final LoggerAdapter LOGGER = GlobalLogger.getLogger(ActionManager.class.getName());

  private final Map<String, ActionHolder> actionsMap = new ConcurrentHashMap<>();

  public ActionManager() {
  }

  public boolean execute(final String key, final CommandSender sender) {
    return execute(key, new ActionContext(sender));
  }

  public boolean execute(final String key, final CommandSender sender, final Player player) {
    return execute(key, new ActionContext(sender, player));
  }

  public boolean execute(final String key, final ActionContext context) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    if (context == null || context.isStopped()) {
      return false;
    }
    final ActionHolder snapshot = actionsMap.get(key);
    if (snapshot == null) {
      return true;
    }
    snapshot.execute(context);
    return true;
  }

  public Map<String, ActionHolder> actions() {
    return Collections.unmodifiableMap(actionsMap);
  }

  public ActionHolder getHolder(final String key) {
    return actionsMap.get(key);
  }

  public boolean isEmpty() {
    return actionsMap.isEmpty();
  }

  public boolean isEmpty(final String key) {
    final ActionHolder snapshot = actionsMap.get(key);
    return snapshot == null || snapshot.isEmpty();
  }

  public int size() {
    return actionsMap.size();
  }

  public int size(final String key) {
    final ActionHolder snapshot = actionsMap.get(key);
    return snapshot == null ? 0 : snapshot.size();
  }

  public ActionManager addAction(final String key, final ActionHolder holder) {
    if (key == null || key.isEmpty() || holder == null) {
      return this;
    }
    actionsMap.put(key, holder);
    return this;
  }

  public ActionManager removeActions(final String key) {
    if (key == null || key.isEmpty()) {
      return this;
    }
    actionsMap.remove(key);
    return this;
  }

  public boolean hasActions(final String key) {
    final ActionHolder snapshot = key == null || key.isEmpty() ? null : actionsMap.get(key);
    return snapshot != null && snapshot.hasActions();
  }

  public ActionManager clearActions() {
    actionsMap.clear();
    return this;
  }

  public ActionManager clearActions(final String key) {
    if (key != null) {
      actionsMap.remove(key);
    }
    return this;
  }

}
