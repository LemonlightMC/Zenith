package com.lemonlightmc.zenith.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.items.Firework;
import com.lemonlightmc.zenith.items.Potion;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.utils.actions.Action;
import com.lemonlightmc.zenith.utils.actions.Condition;

/**
 * Collection of action implementations related to items and effects.
 */
public class Actions2 {

    /**
     * Action that applies or clears potion effects for a target player.
     */
    public static class PotionEffectAction extends Action {

        private final Potion potion;
        private final boolean clear;
        private final boolean broadcast;

        /**
         * Creates an action that applies the given potion to the target player.
         *
         * @param potion the potion to apply
         */
        public PotionEffectAction(final Potion potion) {
            this(potion, false, false, null);
        }

        /**
         * Creates a configurable potion action.
         *
         * @param potion the potion to apply or clear
         * @param clear whether the action should clear instead of apply
         * @param broadcast whether the action should affect all online players
         * @param conditions conditions that must be met before execution
         */
        public PotionEffectAction(final Potion potion, final boolean clear,
                final boolean broadcast, final Condition conditions) {
            super(conditions);
            this.potion = potion;
            this.clear = clear;
            this.broadcast = broadcast;
        }

        /**
         * Creates a configurable potion action with an explicit synchronization mode.
         *
         * @param potion the potion to apply or clear
         * @param clear whether the action should clear instead of apply
         * @param broadcast whether the action should affect all online players
         * @param conditions conditions that must be met before execution
         * @param useSync whether the action should run synchronously
         */
        public PotionEffectAction(final Potion potion, final boolean clear,
                final boolean broadcast, final Condition conditions, final boolean useSync) {
            super(conditions, useSync);
            this.potion = potion;
            this.clear = clear;
            this.broadcast = broadcast;
        }

        /**
         * Creates an action that clears the given potion from the target player.
         *
         * @param potion the potion to clear
         * @return a new potion clearing action
         */
        public static PotionEffectAction clear(final Potion potion) {
            return new PotionEffectAction(potion, true, false, null);
        }

        /**
         * Creates an action that clears all active potion effects from the target player.
         *
         * @return a new action that clears all potion effects
         */
        public static PotionEffectAction clearAll() {
            return new PotionEffectAction(null, true, false, null);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean run(final ActionContext context) {
            final Player player = context.player();
            if (broadcast) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    apply(p);
                }
            } else if (player != null) {
                apply(player);
            }
            return true;
        }

        private void apply(final Player target) {
            if (clear) {
                if (potion == null) {
                    for (final org.bukkit.potion.PotionEffect effect : target.getActivePotionEffects()) {
                        target.removePotionEffect(effect.getType());
                    }
                } else {
                    potion.playerRemovePotion(target);
                }
                return;
            }
            if (potion == null) {
                return;
            }
            potion.playerApplyPotion(target);
        }
    }

    /**
     * Action that spawns a firework at a target player's location.
     */
    public static class FireworkAction extends Action {

        private final Firework firework;
        private final boolean broadcast;

        /**
         * Creates an action that spawns the given firework at the target player.
         *
         * @param firework the firework to spawn
         */
        public FireworkAction(final Firework firework) {
            this(firework, false, null);
        }

        /**
         * Creates a configurable firework action.
         *
         * @param firework the firework to spawn
         * @param broadcast whether the action should affect all online players
         * @param conditions conditions that must be met before execution
         */
        public FireworkAction(
                final Firework firework, final boolean broadcast, final Condition conditions) {
            super(conditions);
            this.firework = firework;
            this.broadcast = broadcast;
        }

        /**
         * Creates a configurable firework action with an explicit synchronization mode.
         *
         * @param firework the firework to spawn
         * @param broadcast whether the action should affect all online players
         * @param conditions conditions that must be met before execution
         * @param useSync whether the action should run synchronously
         */
        public FireworkAction(
                final Firework firework, final boolean broadcast, final Condition conditions,
                final boolean useSync) {
            super(conditions, useSync);
            this.firework = firework;
            this.broadcast = broadcast;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean run(final ActionContext context) {
            final Player player = context.player();
            if (broadcast) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    spawn(p.getLocation());
                }
            } else if (player != null) {
                spawn(player.getLocation());
            }
            return true;
        }

        private void spawn(final Location location) {
            if (location == null || location.getWorld() == null) {
                return;
            }
            GlobalScheduler.run(() -> {
                firework.spawnEntity(location);
            });
        }
    }

}
