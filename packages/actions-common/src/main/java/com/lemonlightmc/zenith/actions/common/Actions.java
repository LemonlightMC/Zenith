package com.lemonlightmc.zenith.actions.common;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lemonlightmc.zenith.scheduler.GlobalScheduler;

/**
 * Collection of universal/control-flow action implementations.
 */
public class Actions {

  public static final class StopAction extends Action {
    public StopAction() {
      super(null, true);
    }

    public StopAction(final Condition conditions) {
      super(conditions, true);
    }

    @Override
    protected boolean run(final ActionContext context) {
      context.stop();
      return false;
    }
  }

  public static class DelayAction extends Action {

    private final long delayTicks;

    public DelayAction(final Duration duration) {
      super(null, true);
      this.delayTicks = Math.max(0L, duration.getSeconds() * 20);
    }

    public DelayAction(final Duration duration, final Condition conditions) {
      super(conditions, true);
      this.delayTicks = Math.max(0L, duration.getSeconds() * 20);
    }

    public long getDelayTicks() {
      return delayTicks;
    }

    @Override
    protected boolean run(final ActionContext context) {
      return true;
    }
  }

  public static class LogAction extends Action {

    private static final Logger LOGGER = Logger.getLogger(LogAction.class.getName());
    private final String message;
    private final Level level;
    private final String loggerName;

    public LogAction(final String message) {
      this(message, Level.INFO, null, null);
    }

    public LogAction(final String message, final Level level) {
      this(message, level, null, null);
    }

    public LogAction(final String message, final Level level, final String loggerName) {
      this(message, level, loggerName, null);
    }

    public LogAction(final String message, final Level level, final String loggerName,
        final Condition conditions) {
      super(conditions, true);
      this.message = message == null ? "" : message;
      this.level = level == null ? Level.INFO : level;
      this.loggerName = loggerName;
    }

    public LogAction(final String message, final Level level, final String loggerName,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.message = message == null ? "" : message;
      this.level = level == null ? Level.INFO : level;
      this.loggerName = loggerName;
    }

    @Override
    protected boolean run(final ActionContext context) {
      String out = message;
      if (context.player() != null) {
        final org.bukkit.entity.Player player = context.player();
        out = out.replace("%player%", player.getName())
            .replace("%uuid%", player.getUniqueId().toString())
            .replace("%world%", player.getWorld() == null ? "" : player.getWorld().getName());
      }
      final Logger logger = (loggerName == null || loggerName.isEmpty()) ? LOGGER : Logger.getLogger(loggerName);
      logger.log(level, out);
      return true;
    }
  }

  public static class RandomAction extends Action {

    private final List<Action> actions;

    @SafeVarargs
    public RandomAction(final Action... actions) {
      this(actions == null ? Collections.emptyList() : Arrays.asList(actions), null);
    }

    public RandomAction(final Collection<? extends Action> actions) {
      this(actions, null);
    }

    public RandomAction(final Collection<? extends Action> actions,
        final Condition conditions) {
      super(conditions);
      this.actions = actions == null ? Collections.emptyList() : new ArrayList<>(actions);
    }

    public RandomAction(final Collection<? extends Action> actions,
        final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.actions = actions == null ? Collections.emptyList() : new ArrayList<>(actions);
    }

    @Override
    protected boolean run(final ActionContext context) {
      final List<Action> candidates = new ArrayList<>();
      for (final Action action : actions) {
        if (action != null) {
          candidates.add(action);
        }
      }
      if (candidates.isEmpty()) {
        return true;
      }
      final Action chosen = candidates.get(ThreadLocalRandom.current().nextInt(candidates.size()));
      return chosen.execute(context);
    }
  }

  public static class WeightedRandomAction extends Action {

    private final List<Entry> entries = new ArrayList<>();

    public static final class Entry {
      private final Action action;
      private final double weight;

      public Entry(final Action action, final double weight) {
        this.action = action;
        this.weight = weight;
      }

      public Action getAction() {
        return action;
      }

      public double getWeight() {
        return weight;
      }
    }

    public WeightedRandomAction(final Map<Action, ? extends Number> weightedActions) {
      this(toEntries(weightedActions), null);
    }

    public WeightedRandomAction(final Map<Action, ? extends Number> weightedActions,
        final Condition conditions) {
      this(toEntries(weightedActions), conditions);
    }

    public WeightedRandomAction(final Collection<Entry> weightedEntries) {
      this(weightedEntries, null);
    }

    public WeightedRandomAction(final Collection<Entry> weightedEntries, final Condition conditions) {
      super(conditions);
      addEntries(weightedEntries);
    }

    public WeightedRandomAction(final Collection<Entry> weightedEntries, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      addEntries(weightedEntries);
    }

    private static Collection<Entry> toEntries(final Map<Action, ? extends Number> weightedActions) {
      if (weightedActions == null || weightedActions.isEmpty()) {
        return Collections.emptyList();
      }
      final List<Entry> list = new ArrayList<>();
      for (final Map.Entry<Action, ? extends Number> e : weightedActions.entrySet()) {
        if (e.getKey() != null && e.getValue() != null) {
          list.add(new Entry(e.getKey(), Math.max(0.0, e.getValue().doubleValue())));
        }
      }
      return list;
    }

    private void addEntries(final Collection<Entry> weightedEntries) {
      if (weightedEntries == null) {
        return;
      }
      for (final Entry e : weightedEntries) {
        if (e != null && e.getAction() != null) {
          entries.add(new Entry(e.getAction(), Math.max(0.0, e.getWeight())));
        }
      }
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (entries.isEmpty()) {
        return true;
      }
      double total = 0.0;
      for (final Entry entry : entries) {
        total += entry.getWeight();
      }
      if (total <= 0.0) {
        return true;
      }
      double r = ThreadLocalRandom.current().nextDouble(total);
      for (final Entry entry : entries) {
        r -= entry.getWeight();
        if (r <= 0.0) {
          return entry.getAction() == null ? true : entry.getAction().execute(context);
        }
      }
      final Entry last = entries.get(entries.size() - 1);
      return last.getAction() == null ? true : last.getAction().execute(context);
    }
  }

  public static class RepeatAction extends Action {

    private final List<Action> actions;
    private final int times;
    private final long intervalTicks;
    private final long initialDelayTicks;

    public RepeatAction(final Action action, final int times) {
      this(action == null ? Collections.emptyList() : Collections.singletonList(action), times, 0L, 0L, null);
    }

    @SafeVarargs
    public RepeatAction(final int times, final Action... actions) {
      this(actions == null ? Collections.emptyList() : Arrays.asList(actions), times, 0L, 0L, null);
    }

    public RepeatAction(final Collection<? extends Action> actions, final int times) {
      this(actions, times, 0L, 0L, null);
    }

    public RepeatAction(final Collection<? extends Action> actions, final int times,
        final long intervalTicks,
        final long initialDelayTicks, final Condition conditions) {
      super(conditions);
      this.actions = actions == null ? Collections.emptyList() : new ArrayList<>(actions);
      this.times = Math.max(0, times);
      this.intervalTicks = Math.max(0L, intervalTicks);
      this.initialDelayTicks = Math.max(0L, initialDelayTicks);
    }

    public RepeatAction(final Collection<? extends Action> actions, final int times,
        final long intervalTicks,
        final long initialDelayTicks, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.actions = actions == null ? Collections.emptyList() : new ArrayList<>(actions);
      this.times = Math.max(0, times);
      this.intervalTicks = Math.max(0L, intervalTicks);
      this.initialDelayTicks = Math.max(0L, initialDelayTicks);
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (times <= 0 || actions.isEmpty() || context == null) {
        return true;
      }

      if (intervalTicks <= 0L && initialDelayTicks <= 0L) {
        for (int i = 0; i < times; i++) {
          if (context.isStopped()) {
            return false;
          }
          if (!runOnce(context)) {
            return false;
          }
        }
        return !context.isStopped();
      }

      if (intervalTicks <= 0L) {
        GlobalScheduler.runLater(() -> {
          for (int i = 0; i < times; i++) {
            if (context.isStopped()) {
              return;
            }
            if (!runOnce(context)) {
              return;
            }
          }
        }, initialDelayTicks);
        return true;
      }

      final int[] remaining = new int[] { times };
      final Runnable[] holder = new Runnable[1];
      holder[0] = () -> {
        if (context.isStopped() || remaining[0] <= 0) {
          return;
        }
        remaining[0]--;
        if (!runOnce(context) || context.isStopped() || remaining[0] <= 0) {
          return;
        }
        GlobalScheduler.runLater(holder[0], intervalTicks);
      };
      GlobalScheduler.runLater(holder[0], initialDelayTicks);
      return true;
    }

    private boolean runOnce(final ActionContext context) {
      for (final Action action : actions) {
        if (context.isStopped()) {
          return false;
        }
        if (action == null) {
          continue;
        }
        final boolean ok = action.execute(context);
        if (!ok || context.isStopped()) {
          return false;
        }
      }
      return true;
    }
  }
}
