package com.lemonlightmc.zenith.utils.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.utils.actions.Action.ActionContext;
import com.lemonlightmc.zenith.utils.actions.Actions.DelayAction;

public class ActionManager {
  private static final Logger LOGGER = Logger.getLogger(ActionManager.class.getName());

  private final Map<String, List<Action>> actionsMap = new ConcurrentHashMap<>();

  public ActionManager() {
  }

  public boolean execute(final String key, final Player target) {
    return execute(key, new ActionContext(target));
  }

  public boolean execute(final String key, final ActionContext context) {
    if (key == null || key.isEmpty()) {
      return false;
    }
    if (context == null || context.isStopped() || context.getTarget() == null) {
      return false;
    }
    final List<Action> snapshot = actionsMap.get(key);
    if (snapshot == null || snapshot.isEmpty()) {
      return true;
    }

    GlobalScheduler.runLater(() -> runFrom(context, snapshot, 0), 1);
    return true;
  }

  private void runFrom(final ActionContext context, final List<Action> snapshot, final int startIndex) {
    final int len = snapshot.size();
    for (int i = startIndex; i < len; i++) {
      if (context.isStopped()) {
        return;
      }
      final Action action = snapshot.get(i);
      if (action == null) {
        continue;
      }

      // Delay actions are handled by the manager to preserve ordered execution.
      if (action instanceof DelayAction) {
        final DelayAction delay = (DelayAction) action;
        if (delay.shouldRun(context)) {
          final int next = i + 1;
          if (next < len) {
            GlobalScheduler.runLater(() -> runFrom(context, snapshot, next), delay.getDelayTicks());
          }
          return;
        }
        continue;
      }

      final boolean continueChain;
      if (action.isUseSync()) {
        continueChain = safeExecute(context, action);
      } else {
        // Async actions may complete out of order
        GlobalScheduler.runAsync(() -> safeExecute(context, action));
        continueChain = true;
      }

      if (!continueChain || context.isStopped()) {
        return;
      }
    }
  }

  private boolean safeExecute(final ActionContext context, final Action action) {
    try {
      final boolean result = action.execute(context);
      if (!result) {
        context.stop();
      }
      return result;
    } catch (final Throwable t) {
      LOGGER.log(Level.SEVERE, "Action execution failed: " + action.getClass().getName(), t);
      return true; // isolate errors and continue chain
    }
  }

  public ActionManager addAction(final String key, final Action action) {
    if (key == null || key.isEmpty() || action == null) {
      return this;
    }
    actionsMap.compute(key, (k, snapshot) -> {
      if (snapshot == null) {
        return List.of(action);
      }
      if (snapshot.contains(action)) {
        return snapshot;
      }
      final List<Action> next = new ArrayList<>(snapshot.size() + 1);
      next.addAll(snapshot);
      next.add(action);
      return List.copyOf(next);
    });
    return this;
  }

  @SafeVarargs
  public final ActionManager addActions(final String key, final Action... toAdd) {
    if (key == null || key.isEmpty() || toAdd == null || toAdd.length == 0) {
      return this;
    }
    actionsMap.compute(key, (k, snapshot) -> mergeActions(snapshot, toAdd));
    return this;
  }

  public ActionManager addActions(final String key, final Collection<? extends Action> toAdd) {
    if (key == null || key.isEmpty() || toAdd == null || toAdd.isEmpty()) {
      return this;
    }
    actionsMap.compute(key, (k, snapshot) -> mergeActions(snapshot, toAdd.toArray(new Action[0])));
    return this;
  }

  private static List<Action> mergeActions(final List<Action> snapshot, final Action[] toAdd) {
    if (toAdd == null || toAdd.length == 0) {
      return snapshot;
    }
    List<Action> next = null;
    for (final Action action : toAdd) {
      if (action == null) {
        continue;
      }
      final List<Action> base = next != null ? next : snapshot;
      if (base != null && base.contains(action)) {
        continue;
      }
      if (next == null) {
        next = new ArrayList<>(base == null ? toAdd.length : base.size() + toAdd.length);
        if (base != null) {
          next.addAll(base);
        }
      }
      if (!next.contains(action)) {
        next.add(action);
      }
    }
    return next == null ? snapshot : List.copyOf(next);
  }

  public ActionManager removeActions(final String key, final Action action) {
    if (key == null || key.isEmpty() || action == null) {
      return this;
    }
    actionsMap.computeIfPresent(key, (k, snapshot) -> {
      if (!snapshot.contains(action)) {
        return snapshot;
      }
      if (snapshot.size() == 1) {
        return null;
      }
      final List<Action> next = new ArrayList<>(snapshot.size() - 1);
      for (final Action a : snapshot) {
        if (!action.equals(a)) {
          next.add(a);
        }
      }
      return next.isEmpty() ? null : List.copyOf(next);
    });
    return this;
  }

  @SafeVarargs
  public final ActionManager removeActions(final String key, final Action... actions) {
    if (key == null || key.isEmpty() || actions == null || actions.length == 0) {
      return this;
    }
    actionsMap.computeIfPresent(key, (k, snapshot) -> withoutActions(snapshot, actions));
    return this;
  }

  public ActionManager removeActions(final String key, final Collection<? extends Action> actions) {
    if (key == null || key.isEmpty() || actions == null || actions.isEmpty()) {
      return this;
    }
    actionsMap.computeIfPresent(key, (k, snapshot) -> withoutActions(snapshot, actions.toArray(new Action[0])));
    return this;
  }

  private static List<Action> withoutActions(final List<Action> snapshot, final Action[] toRemove) {
    if (snapshot == null || snapshot.isEmpty() || toRemove == null || toRemove.length == 0) {
      return snapshot;
    }
    boolean changed = false;
    for (final Action action : toRemove) {
      if (action != null && snapshot.contains(action)) {
        changed = true;
        break;
      }
    }
    if (!changed) {
      return snapshot;
    }
    final List<Action> next = new ArrayList<>(snapshot.size());
    outer: for (final Action a : snapshot) {
      for (final Action r : toRemove) {
        if (r != null && r.equals(a)) {
          continue outer;
        }
      }
      next.add(a);
    }
    return next.isEmpty() ? null : List.copyOf(next);
  }

  public boolean hasActions(final String key) {
    final List<Action> snapshot = key == null || key.isEmpty() ? null : actionsMap.get(key);
    return snapshot != null && !snapshot.isEmpty();
  }

  public boolean hasAction(final String key, final Action action) {
    if (key == null || key.isEmpty() || action == null) {
      return false;
    }
    final List<Action> snapshot = actionsMap.get(key);
    return snapshot != null && snapshot.contains(action);
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

  public Map<String, List<Action>> getActions() {
    return Collections.unmodifiableMap(actionsMap);
  }

  public List<Action> getActions(final String key) {
    final List<Action> snapshot = actionsMap.get(key);
    return snapshot == null ? Collections.emptyList() : snapshot;
  }

  public boolean isEmpty() {
    return actionsMap.isEmpty();
  }

  public boolean isEmpty(final String key) {
    final List<Action> snapshot = actionsMap.get(key);
    return snapshot == null || snapshot.isEmpty();
  }

  public int size() {
    return actionsMap.size();
  }

  public int size(final String key) {
    final List<Action> snapshot = actionsMap.get(key);
    return snapshot == null ? 0 : snapshot.size();
  }
}
