package com.lemonlightmc.zenith.utils.actions;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.additive.logger.GlobalLogger;
import com.lemonlightmc.zenith.additive.logger.LoggerAdapter;

/**
 * Manages and executes named action chains for player contexts.
 */
public class ActionManager<S extends CommandSender> {
  static final LoggerAdapter LOGGER = GlobalLogger.getLogger(ActionManager.class.getName());

  private final Map<String, ActionHolder<S>> actionsMap = new ConcurrentHashMap<>();

  public ActionManager() {
  }

  public boolean execute(final String key, final S sender) {
    return execute(key, new ConditionContext<S>(sender));
  }

  public boolean execute(final String key, final S sender, final Player player) {
    return execute(key, new ConditionContext<S>(sender, player));
  }

  public boolean execute(final String key, final ConditionContext<S> context) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    if (context == null || context.isStopped()) {
      return false;
    }
    final ActionHolder<S> snapshot = actionsMap.get(key);
    if (snapshot == null) {
      return true;
    }
    snapshot.execute(context);
    return true;
  }

  public Map<String, ActionHolder<S>> actions() {
    return Collections.unmodifiableMap(actionsMap);
  }

  public ActionHolder<S> getHolder(final String key) {
    return actionsMap.get(key);
  }

  public boolean isEmpty() {
    return actionsMap.isEmpty();
  }

  public boolean isEmpty(final String key) {
    final ActionHolder<S> snapshot = actionsMap.get(key);
    return snapshot == null || snapshot.isEmpty();
  }

  public int size() {
    return actionsMap.size();
  }

  public int size(final String key) {
    final ActionHolder<S> snapshot = actionsMap.get(key);
    return snapshot == null ? 0 : snapshot.size();
  }

  public ActionManager<S> addAction(final String key, final ActionHolder<S> holder) {
    if (key == null || key.isEmpty() || holder == null) {
      return this;
    }
    actionsMap.put(key, holder);
    return this;
  }

  public ActionManager<S> removeActions(final String key) {
    if (key == null || key.isEmpty()) {
      return this;
    }
    actionsMap.remove(key);
    return this;
  }

  public boolean hasActions(final String key) {
    final ActionHolder<S> snapshot = key == null || key.isEmpty() ? null : actionsMap.get(key);
    return snapshot != null && snapshot.hasActions();
  }

  public ActionManager<S> clearActions() {
    actionsMap.clear();
    return this;
  }

  public ActionManager<S> clearActions(final String key) {
    if (key != null) {
      actionsMap.remove(key);
    }
    return this;
  }

}
