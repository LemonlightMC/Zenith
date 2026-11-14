package com.lemonlightmc.zenith.utils.actions;

import org.bukkit.entity.Player;

public abstract class Action {

    private final Condition<Player> conditions;
    private final boolean useSync;

    public Action(final Condition<Player> conditions) {
        this(conditions, true);
    }

    public Action(final Condition<Player> conditions, final boolean useSync) {
        this.conditions = conditions;
        this.useSync = useSync;
    }

    public Condition<Player> getConditions() {
        return conditions;
    }

    public boolean isUseSync() {
        return useSync;
    }

    public boolean execute(final Player target) {
        return execute(new ActionContext(target));
    }

    public boolean shouldRun(final ActionContext context) {
        if (context == null || context.isStopped()) {
            return false;
        }
        return conditions != null && !conditions.test(context.getTarget());
    }

    public boolean execute(final ActionContext context) {
        if (context == null || context.isStopped()) {
            return false;
        }
        if (conditions != null && !conditions.test(context.getTarget())) {
            return true;
        }
        return run(context);
    }

    protected abstract boolean run(ActionContext context);

    public static class ActionContext {
        private final Player target;
        private volatile boolean stopped;

        public ActionContext(final Player target) {
            this.target = target;
        }

        public Player getTarget() {
            return target;
        }

        public boolean isStopped() {
            return stopped;
        }

        public void stop() {
            this.stopped = true;
        }
    }
}
