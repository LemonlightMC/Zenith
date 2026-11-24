package com.lemonlightmc.zenith.actions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.internal.ActionSerializer;

/**
 * Represents an executable action that can be conditionally run for a player
 * context.
 */
public abstract class Action<S extends CommandSender> {

    public static final ActionSerializer SERIALIZER = ActionSerializer.INSTANCE;

    private final Condition<S> condition;
    private final boolean useSync;

    public Action(final Condition<S> conditions) {
        this(conditions, true);
    }

    public Action(final Condition<S> condition, final boolean useSync) {
        this.condition = condition;
        this.useSync = useSync;
    }

    protected abstract boolean run(ConditionContext<S> context);

    public Condition<S> condition() {
        return condition;
    }

    public boolean isUseSync() {
        return useSync;
    }

    public boolean execute(final S sender) {
        return execute(new ConditionContext<S>(sender));
    }

    public boolean execute(final S sender, final Player player) {
        return execute(new ConditionContext<S>(sender, player));
    }

    public boolean shouldRun(final ConditionContext<S> context) {
        if (context == null || context.isStopped()) {
            return false;
        }
        return condition == null || condition.test(context);
    }

    public boolean execute(final ConditionContext<S> context) {
        if (context == null || context.isStopped()) {
            return false;
        }
        if (condition != null && !condition.test(context)) {
            return true;
        }
        return run(context);
    }

}
