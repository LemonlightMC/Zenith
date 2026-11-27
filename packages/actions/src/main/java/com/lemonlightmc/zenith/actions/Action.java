package com.lemonlightmc.zenith.actions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.internal.ActionSerializer;

/**
 * Represents an executable action that can be conditionally run for a player
 * context.
 */
public abstract class Action {

    public static final ActionSerializer SERIALIZER = ActionSerializer.INSTANCE;

    private final Condition condition;
    private final boolean useSync;

    public Action(final Condition conditions) {
        this(conditions, true);
    }

    public Action(final Condition condition, final boolean useSync) {
        this.condition = condition;
        this.useSync = useSync;
    }

    protected abstract boolean run(ActionContext context);

    public Condition condition() {
        return condition;
    }

    public boolean isUseSync() {
        return useSync;
    }

    public boolean execute(final CommandSender sender) {
        return execute(new ActionContext(sender));
    }

    public boolean execute(final CommandSender sender, final Player player) {
        return execute(new ActionContext(sender, player));
    }

    public boolean shouldRun(final ActionContext context) {
        if (context == null || context.isStopped()) {
            return false;
        }
        return condition == null || condition.test(context);
    }

    public boolean execute(final ActionContext context) {
        if (context == null || context.isStopped()) {
            return false;
        }
        if (condition != null && !condition.test(context)) {
            return true;
        }
        return run(context);
    }

}
