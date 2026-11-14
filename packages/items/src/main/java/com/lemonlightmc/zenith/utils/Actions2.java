package com.lemonlightmc.zenith.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.items.Firework;
import com.lemonlightmc.zenith.items.Potion;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.wrapper.Actions.Action;
import com.lemonlightmc.zenith.wrapper.Conditions.ConditionSet;

public class Actions2 {

    public static class PotionEffectAction extends Action<Player> {

        private final Potion potion;
        private final boolean clear;
        private final boolean broadcast;

        public PotionEffectAction(final Potion potion) {
            this(potion, false, false, null);
        }

        public PotionEffectAction(final Potion potion, final boolean clear,
                final boolean broadcast, final ConditionSet<Player> conditions) {
            super(conditions);
            this.potion = potion;
            this.clear = clear;
            this.broadcast = broadcast;
        }

        public static PotionEffectAction clear(final Potion potion) {
            return new PotionEffectAction(potion, true, false, null);
        }

        public static PotionEffectAction clearAll() {
            return new PotionEffectAction(null, true, false, null);
        }

        @Override
        protected void run(final Player player) {
            if (broadcast) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    apply(p);
                }
            } else if (player != null) {
                apply(player);
            }
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

    public static class FireworkAction extends Action<Player> {

        private final Firework firework;
        private final boolean broadcast;

        public FireworkAction(final Firework firework) {
            this(firework, false, null);
        }

        public FireworkAction(
                final Firework firework, final boolean broadcast, final ConditionSet<Player> conditions) {
            super(conditions);
            this.firework = firework;
            this.broadcast = broadcast;
        }

        @Override
        protected void run(final Player player) {
            if (broadcast) {
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    spawn(p.getLocation());
                }
            } else if (player != null) {
                spawn(player.getLocation());
            }
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
