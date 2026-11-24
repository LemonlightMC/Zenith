package com.lemonlightmc.zenith.actions;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.actions.Actions.DelayAction;

public record ActionHolder<S extends CommandSender>(List<Action<S>> actions, Condition<S> condition,
        String conditionFailureMsg) {
    public ActionHolder(final List<Action<S>> actions, final Condition<S> condition, final String conditionFailureMsg) {
        this.actions = actions != null && actions.isEmpty() ? null : actions;
        this.condition = condition;
        this.conditionFailureMsg = conditionFailureMsg != null && conditionFailureMsg.isBlank() ? null
                : conditionFailureMsg;
    }

    public ActionHolder(final List<Action<S>> actions, final Condition<S> condition) {
        this(actions != null && actions.isEmpty() ? null : actions, condition, null);
    }

    public ActionHolder(final List<Action<S>> actions) {
        this(actions != null && actions.isEmpty() ? null : actions, null, null);
    }

    public ActionHolder() {
        this(List.of(), null, null);
    }

    public void execute(final ExecutionContext<S> context) {
        if (context == null || context.isStopped()) {
            return;
        }
        GlobalScheduler.runLater(() -> runFrom(context, 0), 1);
    }

    public void execute(final S sender) {
        if (sender == null) {
            return;
        }
        GlobalScheduler.runLater(() -> runFrom(new ExecutionContext<S>(sender), 0), 1);
    }

    public void execute(final S sender, final Player player) {
        if (sender == null) {
            return;
        }
        GlobalScheduler.runLater(() -> runFrom(new ExecutionContext<S>(sender, player), 0), 1);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    void runFrom(final ExecutionContext<S> context, final int startIndex) {
        if (actions == null || actions.isEmpty()) {
            return;
        }
        final int len = actions.size();
        if (condition != null && !condition.test(context)) {
            if (conditionFailureMsg != null) {
                context.sendMessage(conditionFailureMsg);
            }
            return;
        }
        for (int i = startIndex; i < len; i++) {
            if (context.isStopped()) {
                return;
            }
            final Action<S> action = actions.get(i);
            if (action == null) {
                continue;
            }

            // Delay actions are handled by the manager to preserve ordered execution.
            if (action instanceof DelayAction delay) {
                final Condition<CommandSender> delayCondition = delay.condition();
                if (delayCondition == null || ((Condition) delayCondition).test(context)) {
                    final int next = i + 1;
                    if (next < len) {
                        GlobalScheduler.runLater(() -> runFrom(context, next), delay.getDelayTicks());
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

    private boolean safeExecute(final ExecutionContext<S> context, final Action<S> action) {
        try {
            if (action.condition() != null && !action.condition().test(context)) {
                return true;
            }
            final boolean result = action.run(context);
            if (!result) {
                context.stop();
            }
            return result;
        } catch (final Throwable t) {
            ActionManager.LOGGER.warn("Action execution failed: " + action.getClass().getName(), t);
            return true; // isolate errors and continue chain
        }
    }

    public ActionHolder<S> addActions(final Action<S> action) {
        if (action == null || actions == null) {
            return this;
        }
        actions.add(action);
        return this;
    }

    @SafeVarargs
    public final ActionHolder<S> addActions(final Action<S>... toAdd) {
        if (toAdd == null || toAdd.length == 0 || actions == null) {
            return this;
        }
        actions.addAll(List.of(toAdd));
        return this;
    }

    public ActionHolder<S> addActions(final Collection<? extends Action<S>> toAdd) {
        if (toAdd == null || toAdd.isEmpty() || actions == null) {
            return this;
        }
        actions.addAll(toAdd);
        return this;
    }

    public ActionHolder<S> removeActions(final Action<S> action) {
        if (action == null || actions == null) {
            return this;
        }
        actions.remove(action);
        return this;
    }

    @SafeVarargs
    public final ActionHolder<S> removeActions(final Action<S>... toRemove) {
        if (toRemove == null || toRemove.length == 0 || actions == null) {
            return this;
        }
        this.actions.removeAll(List.of(toRemove));
        return this;
    }

    public ActionHolder<S> removeActions(final Collection<? extends Action<S>> toRemove) {
        if (toRemove == null || toRemove.isEmpty() || actions == null) {
            return this;
        }
        actions.removeAll(toRemove);
        return this;
    }

    public ActionHolder<S> clearActions() {
        if (actions != null) {
            actions.clear();
        }
        return this;
    }

    public boolean hasAction(final Action<S> action) {
        return action != null && actions != null && actions.contains(action);
    }

    public boolean hasActions() {
        return actions != null && !actions.isEmpty();
    }

    public boolean isEmpty() {
        return actions == null || actions.isEmpty();
    }

    public List<Action<S>> actions() {
        return actions == null ? List.of() : Collections.unmodifiableList(actions);
    }

    public int size() {
        return actions == null ? 0 : actions.size();
    }
}
