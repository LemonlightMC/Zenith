package com.lemonlightmc.zenith.utils;

import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.lemonlightmc.zenith.apis.ActionbarAPI;
import com.lemonlightmc.zenith.apis.ChatAPI;
import com.lemonlightmc.zenith.apis.TitleAPI;
import com.lemonlightmc.zenith.apis.TitleAPI.TitleInfo;
import com.lemonlightmc.zenith.apis.bossbar.BossBar;
import com.lemonlightmc.zenith.apis.bossbar.BossbarAPI;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.unfinished.Conditions.ConditionSet;

public class Actions {

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

  public static abstract class Action {

    private final ConditionSet<Player> conditions;
    private final boolean useSync;

    public Action(final ConditionSet<Player> conditions) {
      this(conditions, true);
    }

    public Action(final ConditionSet<Player> conditions, final boolean useSync) {
      this.conditions = conditions;
      this.useSync = useSync;
    }

    public ConditionSet<Player> getConditions() {
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
  }

  public static final class StopAction extends Action {
    public StopAction() {
      super(null, true);
    }

    public StopAction(final ConditionSet<Player> conditions) {
      super(conditions, true);
    }

    @Override
    protected boolean run(final ActionContext context) {
      context.stop();
      return false;
    }
  }

  public static class ConsoleCommandAction extends Action {

    private final String command;

    public ConsoleCommandAction(final String command) {
      this(command, null);
    }

    public ConsoleCommandAction(final String command, final ConditionSet<Player> conditions) {
      super(conditions);
      this.command = command;
    }

    public ConsoleCommandAction(final String command, final ConditionSet<Player> conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.command = command;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final CommandSender sender = context.getTarget();
      if (command == null || command.isEmpty()) {
        return true;
      }
      final String parsed = sender instanceof Player
          ? command.replace("%player%", ((Player) sender).getName())
          : command;
      GlobalScheduler.run(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed));
      return true;
    }
  }

  public static class PlayerCommandAction extends Action {

    private final String command;

    public PlayerCommandAction(final String command) {
      this(command, null);
    }

    public PlayerCommandAction(final String command, final ConditionSet<Player> conditions) {
      super(conditions);
      this.command = command;
    }

    public PlayerCommandAction(final String command, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.command = command;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player == null || command == null || command.isEmpty()) {
        return true;
      }
      final String parsed = command.replace("%player%", player.getName());
      GlobalScheduler.run(() -> player.performCommand(parsed));
      return true;
    }
  }

  public static class MessageAction extends Action {

    private final boolean broadcast;
    private final String message;

    public MessageAction(final String message) {
      this(message, false, null);
    }

    public MessageAction(final String message, final boolean broadcast) {
      this(message, broadcast, null);
    }

    public MessageAction(final String message, final boolean broadcast, final ConditionSet<Player> conditions) {
      super(conditions);
      this.message = message;
      this.broadcast = broadcast;
    }

    public MessageAction(final String message, final boolean broadcast, final ConditionSet<Player> conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.message = message;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final CommandSender player = context.getTarget();
      if (broadcast) {
        ChatAPI.broadcast(message);
      } else {
        ChatAPI.send(player, message);
      }
      return true;
    }
  }

  public static class TitleAction extends Action {

    private final boolean broadcast;
    private final TitleInfo info;

    public TitleAction(final TitleInfo info) {
      this(info, false, null);
    }

    public TitleAction(final TitleInfo info, final boolean broadcast) {
      this(info, broadcast, null);
    }

    public TitleAction(final TitleInfo info, final boolean broadcast, final ConditionSet<Player> conditions) {
      super(conditions);
      this.info = info;
      this.broadcast = broadcast;
    }

    public TitleAction(final TitleInfo info, final boolean broadcast, final ConditionSet<Player> conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.info = info;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (info == null) {
        return true;
      }
      if (broadcast) {
        TitleAPI.broadcast(info);
      } else if (player != null) {
        TitleAPI.send(player, info);
      }
      return true;
    }
  }

  public static class ActionbarAction extends Action {

    private final boolean broadcast;
    private final String message;

    public ActionbarAction(final String message) {
      this(message, false, null);
    }

    public ActionbarAction(final String message, final boolean broadcast) {
      this(message, broadcast, null);
    }

    public ActionbarAction(final String message, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.message = message;
      this.broadcast = broadcast;
    }

    public ActionbarAction(final String message, final boolean broadcast,
        final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.message = message;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final CommandSender player = context.getTarget();
      if (broadcast) {
        ActionbarAPI.broadcast(message);
      } else if (player instanceof Player) {
        ActionbarAPI.send((Player) player, message);
      }
      return true;
    }
  }

  public static class TeleportAction extends Action {

    private final boolean all;
    private final Location loc;

    public TeleportAction(final Location loc) {
      this(loc, false, null);
    }

    public TeleportAction(final Location loc, final boolean all) {
      this(loc, all, null);
    }

    public TeleportAction(final Location loc, final boolean all, final ConditionSet<Player> conditions) {
      super(conditions);
      this.loc = loc;
      this.all = all;
    }

    public TeleportAction(final Location loc, final boolean all, final ConditionSet<Player> conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.loc = loc;
      this.all = all;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (loc == null) {
        return true;
      }
      if (all) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          p.teleport(loc, TeleportCause.PLUGIN);
        }
      } else if (player != null) {
        player.teleport(loc, TeleportCause.PLUGIN);
      }
      return true;
    }
  }

  public static class SoundAction extends Action {

    private final String sound;
    private final float volume;
    private final float pitch;
    private final boolean broadcast;

    public SoundAction(final String sound) {
      this(sound, 1f, 1f, false, null);
    }

    public SoundAction(final String sound, final float volume, final float pitch) {
      this(sound, volume, pitch, false, null);
    }

    public SoundAction(final String sound, final float volume, final float pitch, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
      this.broadcast = broadcast;
    }

    public SoundAction(final String sound, final float volume, final float pitch, final boolean broadcast,
        final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.sound = sound;
      this.volume = volume;
      this.pitch = pitch;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (sound == null) {
        return true;
      }
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          play(p);
        }
      } else if (player != null) {
        play(player);
      }
      return true;
    }

    private void play(final Player target) {
      final Sound bukkitSound = resolveSound(sound);
      if (bukkitSound != null) {
        target.playSound(target.getLocation(), bukkitSound, SoundCategory.MASTER, volume, pitch);
      } else {
        target.playSound(target.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
      }
    }

    private static Sound resolveSound(final String name) {
      try {
        return Sound.valueOf(name.toUpperCase().replace('.', '_'));
      } catch (final Exception ignored) {
      }
      final NamespacedKey key = name.contains(":") ? NamespacedKey.fromString(name.toLowerCase())
          : NamespacedKey.minecraft(name.toLowerCase());
      return key == null ? null : Registry.SOUNDS.get(key);
    }
  }

  public static class BossBarAction extends Action {

    private final NamespacedKey key;
    private final String title;
    private final BarColor color;
    private final BarStyle style;
    private final double progress;
    private final int durationTicks;
    private final boolean broadcast;

    public BossBarAction(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
        final double progress, final int durationTicks) {
      this(key, title, color, style, progress, durationTicks, false, null);
    }

    public BossBarAction(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
        final double progress, final int durationTicks, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.key = key;
      this.title = title == null ? "" : title;
      this.color = color == null ? BarColor.PURPLE : color;
      this.style = style == null ? BarStyle.SOLID : style;
      this.progress = Math.max(0.0, Math.min(1.0, progress));
      this.durationTicks = durationTicks;
      this.broadcast = broadcast;
    }

    public BossBarAction(final NamespacedKey key, final String title, final BarColor color, final BarStyle style,
        final double progress, final int durationTicks, final boolean broadcast,
        final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.key = key;
      this.title = title == null ? "" : title;
      this.color = color == null ? BarColor.PURPLE : color;
      this.style = style == null ? BarStyle.SOLID : style;
      this.progress = Math.max(0.0, Math.min(1.0, progress));
      this.durationTicks = durationTicks;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      final NamespacedKey effectiveKey = key != null ? key
          : NamespacedKey.minecraft("zenith_action_" + Math.abs(title.hashCode()));
      BossbarAPI.removeBar(effectiveKey);
      final BossBar bar = new BossBar(effectiveKey, title, color, style).setProgress(progress).setVisible(true);
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          BossbarAPI.addBar(p, bar);
        }
      } else if (player != null) {
        BossbarAPI.addBar(player, bar);
      }
      if (durationTicks > 0) {
        GlobalScheduler.runLater(() -> BossbarAPI.removeBar(effectiveKey), durationTicks);
      }
      return true;
    }
  }

  public static class ParticleAction extends Action {

    private final Particle particle;
    private final int count;
    private final double offsetX;
    private final double offsetY;
    private final double offsetZ;
    private final double speed;
    private final boolean broadcast;

    public ParticleAction(final Particle particle, final int count) {
      this(particle, count, 0.5, 0.5, 0.5, 0.0, false, null);
    }

    public ParticleAction(final Particle particle, final int count, final double offsetX, final double offsetY,
        final double offsetZ, final double speed, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.particle = particle;
      this.count = count;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.offsetZ = offsetZ;
      this.speed = speed;
      this.broadcast = broadcast;
    }

    public ParticleAction(final Particle particle, final int count, final double offsetX, final double offsetY,
        final double offsetZ, final double speed, final boolean broadcast,
        final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.particle = particle;
      this.count = count;
      this.offsetX = offsetX;
      this.offsetY = offsetY;
      this.offsetZ = offsetZ;
      this.speed = speed;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (particle == null) {
        return true;
      }
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          p.spawnParticle(particle, p.getLocation().add(0, 1, 0), count, offsetX, offsetY, offsetZ, speed);
        }
      } else if (player != null) {
        player.spawnParticle(particle, player.getLocation().add(0, 1, 0), count, offsetX, offsetY, offsetZ, speed);
      }
      return true;
    }
  }

  public static class GiveItemAction extends Action {

    private final ItemStack item;

    public GiveItemAction(final ItemStack item) {
      this(item, null);
    }

    public GiveItemAction(final ItemStack item, final ConditionSet<Player> conditions) {
      super(conditions);
      this.item = item;
    }

    public GiveItemAction(final ItemStack item, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.item = item;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player == null || item == null) {
        return true;
      }
      final Map<Integer, ItemStack> leftover = player.getInventory().addItem(item.clone());
      for (final ItemStack drop : leftover.values()) {
        player.getWorld().dropItemNaturally(player.getLocation(), drop);
      }
      return true;
    }
  }

  public static class RemoveItemAction extends Action {

    private final Material material;
    private final int amount;

    public RemoveItemAction(final Material material, final int amount) {
      this(material, amount, null);
    }

    public RemoveItemAction(final Material material, final int amount, final ConditionSet<Player> conditions) {
      super(conditions);
      this.material = material;
      this.amount = Math.max(1, amount);
    }

    public RemoveItemAction(final Material material, final int amount, final ConditionSet<Player> conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.material = material;
      this.amount = Math.max(1, amount);
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player == null || material == null) {
        return true;
      }
      int remaining = amount;
      final ItemStack[] contents = player.getInventory().getContents();
      for (int i = 0; i < contents.length && remaining > 0; i++) {
        final ItemStack stack = contents[i];
        if (stack == null || stack.getType() != material) {
          continue;
        }
        final int take = Math.min(remaining, stack.getAmount());
        stack.setAmount(stack.getAmount() - take);
        remaining -= take;
        if (stack.getAmount() <= 0) {
          contents[i] = null;
        }
      }
      player.getInventory().setContents(contents);
      return true;
    }
  }

  public static class ExperienceAction extends Action {

    private final int amount;

    public ExperienceAction(final int amount) {
      this(amount, null);
    }

    public ExperienceAction(final int amount, final ConditionSet<Player> conditions) {
      super(conditions);
      this.amount = amount;
    }

    public ExperienceAction(final int amount, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player != null) {
        player.giveExp(amount);
      }
      return true;
    }
  }

  public static class LevelAction extends Action {

    private final int amount;

    public LevelAction(final int amount) {
      this(amount, null);
    }

    public LevelAction(final int amount, final ConditionSet<Player> conditions) {
      super(conditions);
      this.amount = amount;
    }

    public LevelAction(final int amount, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player != null) {
        player.setLevel(Math.max(0, player.getLevel() + amount));
      }
      return true;
    }
  }

  public static class HealthAction extends Action {

    private final double amount;

    public HealthAction(final double amount) {
      this(amount, null);
    }

    public HealthAction(final double amount, final ConditionSet<Player> conditions) {
      super(conditions);
      this.amount = amount;
    }

    public HealthAction(final double amount, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player == null) {
        return true;
      }
      final double max = player.getMaxHealth();
      final double newHealth = Math.max(0.0, Math.min(max, player.getHealth() + amount));
      player.setHealth(newHealth);
      return true;
    }
  }

  public static class FoodAction extends Action {

    private final int amount;
    private final float saturation;

    public FoodAction(final int amount) {
      this(amount, 0f, null);
    }

    public FoodAction(final int amount, final float saturation, final ConditionSet<Player> conditions) {
      super(conditions);
      this.amount = amount;
      this.saturation = saturation;
    }

    public FoodAction(final int amount, final float saturation, final ConditionSet<Player> conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
      this.saturation = saturation;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player == null) {
        return true;
      }
      player.setFoodLevel(Math.max(0, Math.min(20, player.getFoodLevel() + amount)));
      if (saturation != 0f) {
        player.setSaturation(Math.max(0f, Math.min(player.getFoodLevel(), player.getSaturation() + saturation)));
      }
      return true;
    }
  }

  public abstract static class CurrencyAction extends Action {

    private final double amount;

    public CurrencyAction(final double amount) {
      this(amount, null);
    }

    public CurrencyAction(final double amount, final ConditionSet<Player> conditions) {
      super(conditions);
      this.amount = amount;
    }

    public CurrencyAction(final double amount, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    public double getAmount() {
      return amount;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (player == null) {
        return true;
      }
      try {
        final RegisteredServiceProvider<?> provider = Bukkit.getServicesManager()
            .getRegistration(Class.forName("net.milkbowl.vault.economy.Economy"));
        if (provider == null) {
          return true;
        }
        final Object economy = provider.getProvider();
        final Class<?> offlinePlayerClass = Class.forName("org.bukkit.OfflinePlayer");
        if (this instanceof AddCurrencyAction) {
          economy.getClass().getMethod("depositPlayer", offlinePlayerClass, double.class).invoke(economy, player,
              amount);
        } else if (this instanceof RemoveCurrencyAction) {
          economy.getClass().getMethod("withdrawPlayer", offlinePlayerClass, double.class).invoke(economy, player,
              amount);
        }
      } catch (final Exception ignored) {
        // Vault economy is optional; silently ignore when unavailable.
      }
      return true;
    }
  }

  public static class AddCurrencyAction extends CurrencyAction {
    public AddCurrencyAction(final double amount) {
      super(amount);
    }

    public AddCurrencyAction(final double amount, final ConditionSet<Player> conditions) {
      super(amount, conditions);
    }

    public AddCurrencyAction(final double amount, final ConditionSet<Player> conditions, final boolean useSync) {
      super(amount, conditions, useSync);
    }
  }

  public static class RemoveCurrencyAction extends CurrencyAction {
    public RemoveCurrencyAction(final double amount) {
      super(amount);
    }

    public RemoveCurrencyAction(final double amount, final ConditionSet<Player> conditions) {
      super(amount, conditions);
    }

    public RemoveCurrencyAction(final double amount, final ConditionSet<Player> conditions, final boolean useSync) {
      super(amount, conditions, useSync);
    }
  }

  public static class DelayAction extends Action {

    private final long delayTicks;

    public DelayAction(final long delayTicks) {
      super(null, true);
      this.delayTicks = Math.max(0L, delayTicks);
    }

    public DelayAction(final long delayTicks, final ConditionSet<Player> conditions) {
      super(conditions, true);
      this.delayTicks = Math.max(0L, delayTicks);
    }

    public long getDelayTicks() {
      return delayTicks;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player target = context.getTarget();
      return true;
    }
  }

  public static class PotionEffectAction extends Action {

    private final PotionEffectType type;
    private final int durationTicks;
    private final int amplifier;
    private final boolean ambient;
    private final boolean particles;
    private final boolean icon;
    private final boolean clear;
    private final boolean broadcast;

    public PotionEffectAction(final PotionEffectType type, final int durationTicks, final int amplifier) {
      this(type, durationTicks, amplifier, false, true, true, false, false, null);
    }

    public PotionEffectAction(final PotionEffectType type, final int durationTicks, final int amplifier,
        final boolean ambient, final boolean particles, final boolean icon, final boolean clear,
        final boolean broadcast, final ConditionSet<Player> conditions) {
      super(conditions);
      this.type = type;
      this.durationTicks = durationTicks;
      this.amplifier = amplifier;
      this.ambient = ambient;
      this.particles = particles;
      this.icon = icon;
      this.clear = clear;
      this.broadcast = broadcast;
    }

    public PotionEffectAction(final PotionEffectType type, final int durationTicks, final int amplifier,
        final boolean ambient, final boolean particles, final boolean icon, final boolean clear,
        final boolean broadcast, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.type = type;
      this.durationTicks = durationTicks;
      this.amplifier = amplifier;
      this.ambient = ambient;
      this.particles = particles;
      this.icon = icon;
      this.clear = clear;
      this.broadcast = broadcast;
    }

    public static PotionEffectAction clear(final PotionEffectType type) {
      return new PotionEffectAction(type, 0, 0, false, true, true, true, false, null);
    }

    public static PotionEffectAction clearAll() {
      return new PotionEffectAction(null, 0, 0, false, true, true, true, false, null);
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
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
        if (type == null) {
          for (final PotionEffect effect : target.getActivePotionEffects()) {
            target.removePotionEffect(effect.getType());
          }
        } else {
          target.removePotionEffect(type);
        }
        return;
      }
      if (type == null) {
        return;
      }
      target.addPotionEffect(new PotionEffect(type, Math.max(1, durationTicks), Math.max(0, amplifier), ambient,
          particles, icon));
    }
  }

  public static class StopSoundAction extends Action {

    private final String sound;
    private final SoundCategory category;
    private final boolean broadcast;

    public StopSoundAction() {
      this(null, null, false, null);
    }

    public StopSoundAction(final String sound) {
      this(sound, null, false, null);
    }

    public StopSoundAction(final String sound, final SoundCategory category, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.sound = sound;
      this.category = category;
      this.broadcast = broadcast;
    }

    public StopSoundAction(final String sound, final SoundCategory category, final boolean broadcast,
        final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.sound = sound;
      this.category = category;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          stop(p);
        }
      } else if (player != null) {
        stop(player);
      }
      return true;
    }

    private void stop(final Player target) {
      if (sound == null || sound.isEmpty()) {
        if (category == null) {
          target.stopAllSounds();
        } else {
          target.stopSound(category);
        }
        return;
      }
      final Sound bukkitSound = resolveSound(sound);
      if (category != null) {
        if (bukkitSound != null) {
          target.stopSound(bukkitSound, category);
        } else {
          target.stopSound(sound, category);
        }
      } else {
        if (bukkitSound != null) {
          target.stopSound(bukkitSound);
        } else {
          target.stopSound(sound);
        }
      }
    }

    private static Sound resolveSound(final String name) {
      try {
        return Sound.valueOf(name.toUpperCase().replace('.', '_'));
      } catch (final Exception ignored) {
      }
      final NamespacedKey key = name.contains(":") ? NamespacedKey.fromString(name.toLowerCase())
          : NamespacedKey.minecraft(name.toLowerCase());
      return key == null ? null : Registry.SOUNDS.get(key);
    }
  }

  public static class MovementAction extends Action {

    private final Vector velocity;
    private final boolean add;
    private final boolean relativeToLook;
    private final boolean broadcast;

    public MovementAction(final Vector velocity) {
      this(velocity, false, false, false, null);
    }

    public MovementAction(final Vector velocity, final boolean add, final boolean relativeToLook,
        final boolean broadcast, final ConditionSet<Player> conditions) {
      super(conditions);
      this.velocity = velocity == null ? new Vector() : velocity.clone();
      this.add = add;
      this.relativeToLook = relativeToLook;
      this.broadcast = broadcast;
    }

    public MovementAction(final Vector velocity, final boolean add, final boolean relativeToLook,
        final boolean broadcast, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.velocity = velocity == null ? new Vector() : velocity.clone();
      this.add = add;
      this.relativeToLook = relativeToLook;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
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
      Vector v = velocity.clone();
      if (relativeToLook) {
        final Vector dir = target.getLocation().getDirection().normalize();
        v = dir.multiply(v.length() == 0 ? 1 : v.length()).setY(velocity.getY());
      }
      if (add) {
        target.setVelocity(target.getVelocity().add(v));
      } else {
        target.setVelocity(v);
      }
    }
  }

  public static class GameModeAction extends Action {

    private final GameMode gameMode;
    private final boolean broadcast;

    public GameModeAction(final GameMode gameMode) {
      this(gameMode, false, null);
    }

    public GameModeAction(final GameMode gameMode, final boolean broadcast, final ConditionSet<Player> conditions) {
      super(conditions);
      this.gameMode = gameMode;
      this.broadcast = broadcast;
    }

    public GameModeAction(final GameMode gameMode, final boolean broadcast, final ConditionSet<Player> conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.gameMode = gameMode;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (gameMode == null) {
        return true;
      }
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          p.setGameMode(gameMode);
        }
      } else if (player != null) {
        player.setGameMode(gameMode);
      }
      return true;
    }
  }

  public static class FlyAction extends Action {

    private final boolean allowFlight;
    private final boolean flying;
    private final boolean broadcast;

    public FlyAction(final boolean allowFlight, final boolean flying) {
      this(allowFlight, flying, false, null);
    }

    public FlyAction(final boolean allowFlight, final boolean flying, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.broadcast = broadcast;
    }

    public FlyAction(final boolean allowFlight, final boolean flying, final boolean broadcast,
        final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
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
      target.setAllowFlight(allowFlight);
      if (target.isFlying() != flying) {
        try {
          target.setFlying(flying && allowFlight);
        } catch (final IllegalArgumentException ignored) {
          // Ignored when flight state cannot be applied.
        }
      }
    }
  }

  public static class InvulnerableAction extends Action {

    private final boolean invulnerable;
    private final int durationTicks;
    private final boolean broadcast;

    public InvulnerableAction(final boolean invulnerable) {
      this(invulnerable, -1, false, null);
    }

    public InvulnerableAction(final boolean invulnerable, final int durationTicks, final boolean broadcast,
        final ConditionSet<Player> conditions) {
      super(conditions);
      this.invulnerable = invulnerable;
      this.durationTicks = durationTicks;
      this.broadcast = broadcast;
    }

    public InvulnerableAction(final boolean invulnerable, final int durationTicks, final boolean broadcast,
        final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.invulnerable = invulnerable;
      this.durationTicks = durationTicks;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
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
      target.setInvulnerable(invulnerable);
      if (durationTicks > 0) {
        final boolean revert = !invulnerable;
        GlobalScheduler.runLater(() -> {
          if (target.isOnline()) {
            target.setInvulnerable(revert);
          }
        }, durationTicks);
      }
    }
  }

  public static class SummonAction extends Action {

    private final EntityType type;
    private final int amount;
    private final Location location;
    private final boolean atPlayer;
    private final Consumer<Entity> customizer;

    public SummonAction(final EntityType type) {
      this(type, 1, null, true, null, null);
    }

    public SummonAction(final EntityType type, final int amount, final Location location, final boolean atPlayer,
        final Consumer<Entity> customizer, final ConditionSet<Player> conditions) {
      super(conditions);
      this.type = type;
      this.amount = Math.max(1, amount);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.customizer = customizer;
    }

    public SummonAction(final EntityType type, final int amount, final Location location, final boolean atPlayer,
        final Consumer<Entity> customizer, final ConditionSet<Player> conditions, final boolean useSync) {
      super(conditions, useSync);
      this.type = type;
      this.amount = Math.max(1, amount);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.customizer = customizer;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.getTarget();
      if (type == null) {
        return true;
      }
      final Location base = atPlayer && player != null ? player.getLocation() : location;
      if (base == null || base.getWorld() == null) {
        return true;
      }
      GlobalScheduler.run(() -> {
        for (int i = 0; i < amount; i++) {
          final Entity entity = base.getWorld().spawnEntity(base, type);
          if (customizer != null) {
            customizer.accept(entity);
          }
        }
      });
      return true;
    }
  }
}
