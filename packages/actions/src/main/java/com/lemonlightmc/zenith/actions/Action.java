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

    protected abstract boolean run(ConditionContext<S> context);

    /**
     * Holds execution context for a running action chain.
     */
    public static class ActionContext {
        private volatile boolean stopped;
        private final CommandSender sender;
        private final Player player;

        public ActionContext(final CommandSender sender) {
            if (sender == null) {
                throw new IllegalArgumentException("Action Sender cannot be null");
            }
            this.sender = sender;
            if (sender instanceof final Player player2) {
                this.player = player2;
            } else {
                this.player = null;
            }
        }

        public ActionContext(final CommandSender sender, final Player player) {
            this.sender = sender;
            if (sender == null) {
                throw new IllegalArgumentException("Action Sender cannot be null");
            }
            if (player != null) {
                this.player = player;
            } else if (sender instanceof final Player player2) {
                this.player = player2;
            } else {
                this.player = null;
            }
        }

        public Player player() {
            return player;
        }

        public CommandSender sender() {
            return sender;
        }

        public boolean isPlayer() {
            return player != null;
        }

        public boolean isStopped() {
            return stopped;
        }

        public void stop() {
            this.stopped = true;
        }
    }
}
