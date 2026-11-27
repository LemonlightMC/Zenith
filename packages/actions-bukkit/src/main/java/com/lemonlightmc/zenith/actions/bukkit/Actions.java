package com.lemonlightmc.zenith.actions.bukkit;

import com.lemonlightmc.zenith.actions.common.Action;
import com.lemonlightmc.zenith.actions.common.ActionContext;
import com.lemonlightmc.zenith.actions.common.Condition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.lemonlightmc.zenith.apis.ActionbarAPI;
import com.lemonlightmc.zenith.apis.ChatAPI;
import com.lemonlightmc.zenith.apis.TitleAPI;
import com.lemonlightmc.zenith.apis.TitleAPI.TitleInfo;

import com.lemonlightmc.zenith.math.MathOperation;
import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.scheduler.ScheduledTask;

/**
 * Collection of built-in action implementations.
 */
public class Actions {


  private static final Map<String, Sound> SOUND_CACHE = new ConcurrentHashMap<>();
  private static final Map<UUID, ScheduledTask> INVULNERABLE_REVERT_TASKS = new ConcurrentHashMap<>();

  @SuppressWarnings("deprecation")
  private static Sound resolveCachedSound(final String name) {
    if (name == null || name.isEmpty()) {
      return null;
    }
    return SOUND_CACHE.computeIfAbsent(name, n -> {
      try {
        return Sound.valueOf(n.toUpperCase().replace('.', '_'));
      } catch (final Exception ignored) {
      }
      final NamespacedKey key = n.contains(":") ? NamespacedKey.fromString(n.toLowerCase())
          : NamespacedKey.minecraft(n.toLowerCase());
      return key == null ? null : Registry.SOUNDS.get(key);
    });
  }


  public static class ConsoleCommandAction extends Action {

    private final String command;

    public ConsoleCommandAction(final String command) {
      this(command, null);
    }

    public ConsoleCommandAction(final String command, final Condition conditions) {
      super(conditions);
      this.command = command;
    }

    public ConsoleCommandAction(final String command, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.command = command;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (command == null || command.isEmpty()) {
        return true;
      }
      final String parsed = context.player() != null
          ? command.replace("%player%", context.player().getName())
          : command;
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsed);
      return true;
    }
  }

  public static class PlayerCommandAction extends Action {

    private final String command;

    public PlayerCommandAction(final String command) {
      this(command, null);
    }

    public PlayerCommandAction(final String command, final Condition conditions) {
      super(conditions);
      this.command = command;
    }

    public PlayerCommandAction(final String command, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.command = command;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.player();
      if (command == null || command.isEmpty() || player == null) {
        return true;
      }
      return player.performCommand(command.replace("%player%", player.getName()));
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

    public MessageAction(final String message, final boolean broadcast, final Condition conditions) {
      super(conditions);
      this.message = message;
      this.broadcast = broadcast;
    }

    public MessageAction(final String message, final boolean broadcast, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.message = message;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (broadcast) {
        ChatAPI.broadcast(message);
      } else {
        ChatAPI.send(context.player() != null ? context.player() : context.sender(), message);
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

    public TitleAction(final TitleInfo info, final boolean broadcast, final Condition conditions) {
      super(conditions);
      this.info = info;
      this.broadcast = broadcast;
    }

    public TitleAction(final TitleInfo info, final boolean broadcast, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.info = info;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (info == null) {
        return true;
      }
      if (broadcast) {
        TitleAPI.broadcast(info);
      } else {
        if (context.player() != null) {
          TitleAPI.send(context.player(), info);
        }
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
        final Condition conditions) {
      super(conditions);
      this.message = message;
      this.broadcast = broadcast;
    }

    public ActionbarAction(final String message, final boolean broadcast,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.message = message;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (broadcast) {
        ActionbarAPI.broadcast(message);
      } else if (context.player() != null) {
        final Player player = context.player();
        ActionbarAPI.send(player, message);
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

    public TeleportAction(final Location loc, final boolean all, final Condition conditions) {
      super(conditions);
      this.loc = loc;
      this.all = all;
    }

    public TeleportAction(final Location loc, final boolean all, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.loc = loc;
      this.all = all;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (loc == null) {
        return true;
      }
      if (all) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          p.teleport(loc, TeleportCause.PLUGIN);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
        player.teleport(loc, TeleportCause.PLUGIN);
      }
      return true;
    }
  }

  public static class SoundAction extends Action {

    private final String sound;
    private final Sound bukkitSound;
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
        final Condition conditions) {
      super(conditions);
      this.sound = sound;
      this.bukkitSound = resolveCachedSound(sound);
      this.volume = volume;
      this.pitch = pitch;
      this.broadcast = broadcast;
    }

    public SoundAction(final String sound, final float volume, final float pitch, final boolean broadcast,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.sound = sound;
      this.bukkitSound = resolveCachedSound(sound);
      this.volume = volume;
      this.pitch = pitch;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (sound == null) {
        return true;
      }
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          play(p);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
        play(player);
      }
      return true;
    }

    private void play(final Player target) {
      if (bukkitSound != null) {
        target.playSound(target.getLocation(), bukkitSound, SoundCategory.MASTER, volume, pitch);
      } else {
        target.playSound(target.getLocation(), sound, SoundCategory.MASTER, volume, pitch);
      }
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
        final Condition conditions) {
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
        final Condition conditions, final boolean useSync) {
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
      if (particle == null) {
        return true;
      }
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          p.spawnParticle(particle, p.getLocation().add(0, 1, 0), count, offsetX, offsetY, offsetZ, speed);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
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

    public GiveItemAction(final ItemStack item, final Condition conditions) {
      super(conditions);
      this.item = item;
    }

    public GiveItemAction(final ItemStack item, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.item = item;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (item == null) {
        return true;
      }
      final Player player = context.player();
      if (player == null) {
        return false;
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

    public RemoveItemAction(final Material material, final int amount, final Condition conditions) {
      super(conditions);
      this.material = material;
      this.amount = Math.max(1, amount);
    }

    public RemoveItemAction(final Material material, final int amount, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.material = material;
      this.amount = Math.max(1, amount);
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (material == null) {
        return true;
      }
      final Player player = context.player();
      if (player == null) {
        return false;
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

    public ExperienceAction(final int amount, final Condition conditions) {
      super(conditions);
      this.amount = amount;
    }

    public ExperienceAction(final int amount, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.player();
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

    public LevelAction(final int amount, final Condition conditions) {
      super(conditions);
      this.amount = amount;
    }

    public LevelAction(final int amount, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.player();
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

    public HealthAction(final double amount, final Condition conditions) {
      super(conditions);
      this.amount = amount;
    }

    public HealthAction(final double amount, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.player();
      if (player == null) {
        return false;
      }
      final AttributeInstance attribute = player.getAttribute(Attribute.MAX_HEALTH);
      final double newHealth = Math.max(0.0,
          Math.min(attribute == null ? 20 : attribute.getValue(), player.getHealth() + amount));
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

    public FoodAction(final int amount, final float saturation, final Condition conditions) {
      super(conditions);
      this.amount = amount;
      this.saturation = saturation;
    }

    public FoodAction(final int amount, final float saturation, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.amount = amount;
      this.saturation = saturation;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Player player = context.player();
      if (player == null) {
        return false;
      }
      player.setFoodLevel(Math.max(0, Math.min(20, player.getFoodLevel() + amount)));
      if (saturation != 0f) {
        player.setSaturation(Math.max(0f, Math.min(player.getFoodLevel(), player.getSaturation() + saturation)));
      }
      return true;
    }
  }


  public static class StopSoundAction extends Action {

    private final String sound;
    private final Sound bukkitSound;
    private final SoundCategory category;
    private final boolean broadcast;

    public StopSoundAction() {
      this(null, null, false, null);
    }

    public StopSoundAction(final String sound) {
      this(sound, null, false, null);
    }

    public StopSoundAction(final String sound, final SoundCategory category, final boolean broadcast,
        final Condition conditions) {
      super(conditions);
      this.sound = sound;
      this.bukkitSound = resolveCachedSound(sound);
      this.category = category;
      this.broadcast = broadcast;
    }

    public StopSoundAction(final String sound, final SoundCategory category, final boolean broadcast,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.sound = sound;
      this.bukkitSound = resolveCachedSound(sound);
      this.category = category;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          stop(p);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
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
  }

  public static class MovementAction extends Action {

    private final Vector velocity;
    private final MathOperation operation;
    private final boolean useLookDirection;
    private final boolean broadcast;

    public MovementAction(final Vector velocity) {
      this(velocity, MathOperation.ADD, false, false, null);
    }

    public MovementAction(final Vector velocity, final MathOperation operation, final boolean useLookDirection,
        final boolean broadcast, final Condition conditions) {
      super(conditions);
      this.velocity = velocity == null ? new Vector() : velocity.clone();
      this.operation = operation;
      this.useLookDirection = useLookDirection;
      this.broadcast = broadcast;
    }

    public MovementAction(final Vector velocity, final MathOperation operation, final boolean useLookDirection,
        final boolean broadcast, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.velocity = velocity == null ? new Vector() : velocity.clone();
      this.operation = operation;
      this.useLookDirection = useLookDirection;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          apply(p);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
        apply(player);
      }
      return true;
    }

    private void apply(final Player target) {
      Vector v = velocity.clone();
      if (useLookDirection) {
        final Vector dir = target.getLocation().getDirection().normalize();
        v = dir.multiply(v.length() == 0 ? 1 : v.length()).setY(velocity.getY());
      }
      final Vector result = new Vector(operation.apply(target.getVelocity().getX(), v.getX()),
          operation.apply(target.getVelocity().getY(), v.getY()),
          operation.apply(target.getVelocity().getZ(), v.getZ()));
      target.setVelocity(result);
    }
  }

  public static class GameModeAction extends Action {

    private final GameMode gameMode;
    private final boolean broadcast;

    public GameModeAction(final GameMode gameMode) {
      this(gameMode, false, null);
    }

    public GameModeAction(final GameMode gameMode, final boolean broadcast, final Condition conditions) {
      super(conditions);
      this.gameMode = gameMode;
      this.broadcast = broadcast;
    }

    public GameModeAction(final GameMode gameMode, final boolean broadcast, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.gameMode = gameMode;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (gameMode == null) {
        return true;
      }
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          p.setGameMode(gameMode);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
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
        final Condition conditions) {
      super(conditions);
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.broadcast = broadcast;
    }

    public FlyAction(final boolean allowFlight, final boolean flying, final boolean broadcast,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.allowFlight = allowFlight;
      this.flying = flying;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          apply(p);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
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
    private final long durationTicks;
    private final boolean broadcast;

    public InvulnerableAction(final boolean invulnerable) {
      this(invulnerable, null, false, null);
    }

    public InvulnerableAction(final boolean invulnerable, final Duration duration, final boolean broadcast,
        final Condition conditions) {
      super(conditions);
      this.invulnerable = invulnerable;
      this.durationTicks = duration == null ? -1 : duration.getSeconds() * 20;
      this.broadcast = broadcast;
    }

    public InvulnerableAction(final boolean invulnerable, final Duration duration, final boolean broadcast,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.invulnerable = invulnerable;
      this.durationTicks = duration == null ? -1 : duration.getSeconds() * 20;
      this.broadcast = broadcast;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (broadcast) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
          apply(p);
        }
      } else if (context.player() != null) {
        final Player player = context.player();
        apply(player);
      }
      return true;
    }

    private void apply(final Player target) {
      final UUID uuid = target.getUniqueId();
      final ScheduledTask previous = INVULNERABLE_REVERT_TASKS.remove(uuid);
      if (previous != null) {
        previous.cancel();
      }
      target.setInvulnerable(invulnerable);
      if (durationTicks > 0) {
        final boolean revert = !invulnerable;
        final ScheduledTask[] holder = new ScheduledTask[1];
        holder[0] = GlobalScheduler.runLater(() -> {
          INVULNERABLE_REVERT_TASKS.remove(uuid, holder[0]);
          if (target.isOnline()) {
            target.setInvulnerable(revert);
          }
        }, durationTicks);
        INVULNERABLE_REVERT_TASKS.put(uuid, holder[0]);
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
        final Consumer<Entity> customizer, final Condition conditions) {
      super(conditions);
      this.type = type;
      this.amount = Math.max(1, amount);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.customizer = customizer;
    }

    @Override
    protected boolean run(final ActionContext context) {
      if (type == null) {
        return true;
      }
      final Location base;
      if (atPlayer) {
        final Player player = context.player();
        if (player == null) {
          return false;
        }
        base = player.getLocation();
      } else {
        base = location;
      }
      if (base == null || base.getWorld() == null) {
        return true;
      }

      final Runnable task = () -> {
        for (int i = 0; i < amount; i++) {
          final Entity entity = base.getWorld().spawnEntity(base, type);
          if (customizer != null) {
            customizer.accept(entity);
          }
        }
      };
      GlobalScheduler.runAsync(task);
      return true;
    }
  }

  public static class WeatherAction extends Action {

    public enum WeatherType {
      CLEAR,
      RAIN,
      THUNDER
    }

    private final WeatherType type;
    private final String worldName;
    private final int durationTicks;

    public WeatherAction(final WeatherType type) {
      this(type, null, 0, null);
    }

    public WeatherAction(final WeatherType type, final String worldName) {
      this(type, worldName, 0, null);
    }

    public WeatherAction(final WeatherType type, final String worldName, final int durationTicks,
        final Condition conditions) {
      super(conditions);
      this.type = type == null ? WeatherType.CLEAR : type;
      this.worldName = worldName;
      this.durationTicks = Math.max(0, durationTicks);
    }

    public WeatherAction(final WeatherType type, final String worldName, final int durationTicks,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.type = type == null ? WeatherType.CLEAR : type;
      this.worldName = worldName;
      this.durationTicks = Math.max(0, durationTicks);
    }

    @Override
    protected boolean run(final ActionContext context) {
      final World world = resolveWorld(context, worldName);
      if (world == null) {
        return true;
      }
      switch (type) {
        case CLEAR:
          world.setStorm(false);
          world.setThundering(false);
          break;
        case RAIN:
          world.setStorm(true);
          world.setThundering(false);
          break;
        case THUNDER:
          world.setStorm(true);
          world.setThundering(true);
          break;
        default:
          break;
      }
      if (durationTicks > 0) {
        world.setWeatherDuration(durationTicks);
        if (type == WeatherType.THUNDER) {
          world.setThunderDuration(durationTicks);
        }
      }
      return true;
    }
  }

  public static class TimeAction extends Action {

    private final long time;
    private final boolean add;
    private final String worldName;

    public TimeAction(final long time) {
      this(time, false, null, null);
    }

    public TimeAction(final long time, final boolean add, final String worldName) {
      this(time, add, worldName, null);
    }

    public TimeAction(final long time, final boolean add, final String worldName,
        final Condition conditions) {
      super(conditions);
      this.time = time;
      this.add = add;
      this.worldName = worldName;
    }

    public TimeAction(final long time, final boolean add, final String worldName,
        final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.time = time;
      this.add = add;
      this.worldName = worldName;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final World world = resolveWorld(context, worldName);
      if (world == null) {
        return true;
      }
      if (add) {
        world.setTime(world.getTime() + time);
      } else {
        world.setTime(time);
      }
      return true;
    }
  }

  public static class BlockPlaceAction extends Action {

    private final Location location;
    private final boolean atPlayer;
    private final Material material;
    private final BlockData blockData;
    private final boolean applyPhysics;

    public BlockPlaceAction(final Location location, final Material material) {
      this(location, false, material, null, true, null);
    }

    public BlockPlaceAction(final Material material, final boolean atPlayer) {
      this(null, atPlayer, material, null, true, null);
    }

    public BlockPlaceAction(final Location location, final BlockData blockData) {
      this(location, false, null, blockData, true, null);
    }

    public BlockPlaceAction(final Location location, final boolean atPlayer, final Material material,
        final BlockData blockData, final boolean applyPhysics, final Condition conditions) {
      super(conditions);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.material = material;
      this.blockData = blockData;
      this.applyPhysics = applyPhysics;
    }

    public BlockPlaceAction(final Location location, final boolean atPlayer, final Material material,
        final BlockData blockData, final boolean applyPhysics, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.material = material;
      this.blockData = blockData;
      this.applyPhysics = applyPhysics;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Location base = resolveLocation(context, location, atPlayer);
      if (base == null || base.getWorld() == null) {
        return true;
      }
      final Block block = base.getBlock();
      if (blockData != null) {
        block.setBlockData(blockData.clone(), applyPhysics);
      } else if (material != null) {
        block.setType(material, applyPhysics);
      }
      return true;
    }
  }

  public static class BlockBreakAction extends Action {

    private final Location location;
    private final boolean atPlayer;
    private final boolean dropItems;
    private final boolean applyPhysics;

    public BlockBreakAction(final Location location) {
      this(location, false, false, true, null);
    }

    public BlockBreakAction(final boolean atPlayer) {
      this(null, atPlayer, false, true, null);
    }

    public BlockBreakAction(final Location location, final boolean atPlayer, final boolean dropItems,
        final boolean applyPhysics, final Condition conditions) {
      super(conditions);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.dropItems = dropItems;
      this.applyPhysics = applyPhysics;
    }

    public BlockBreakAction(final Location location, final boolean atPlayer, final boolean dropItems,
        final boolean applyPhysics, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.dropItems = dropItems;
      this.applyPhysics = applyPhysics;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Location base = resolveLocation(context, location, atPlayer);
      if (base == null || base.getWorld() == null) {
        return true;
      }
      final Block block = base.getBlock();
      if (dropItems) {
        block.breakNaturally();
      } else {
        block.setType(Material.AIR, applyPhysics);
      }
      return true;
    }
  }

  public static class BlockReplaceAction extends Action {

    private final Location location;
    private final boolean atPlayer;
    private final Material from;
    private final Material to;
    private final BlockData toData;
    private final boolean applyPhysics;

    public BlockReplaceAction(final Location location, final Material from, final Material to) {
      this(location, false, from, to, null, true, null);
    }

    public BlockReplaceAction(final boolean atPlayer, final Material from, final Material to) {
      this(null, atPlayer, from, to, null, true, null);
    }

    public BlockReplaceAction(final Location location, final boolean atPlayer, final Material from, final Material to,
        final BlockData toData, final boolean applyPhysics, final Condition conditions) {
      super(conditions);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.from = from;
      this.to = to;
      this.toData = toData;
      this.applyPhysics = applyPhysics;
    }

    public BlockReplaceAction(final Location location, final boolean atPlayer, final Material from, final Material to,
        final BlockData toData, final boolean applyPhysics, final Condition conditions,
        final boolean useSync) {
      super(conditions, useSync);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.from = from;
      this.to = to;
      this.toData = toData;
      this.applyPhysics = applyPhysics;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Location base = resolveLocation(context, location, atPlayer);
      if (base == null || base.getWorld() == null) {
        return true;
      }
      final Block block = base.getBlock();
      if (from != null && block.getType() != from) {
        return true;
      }
      if (toData != null) {
        block.setBlockData(toData.clone(), applyPhysics);
      } else if (to != null) {
        block.setType(to, applyPhysics);
      } else {
        block.setType(Material.AIR, applyPhysics);
      }
      return true;
    }
  }

  public static class ExplosionAction extends Action {

    private final Location location;
    private final boolean atPlayer;
    private final float power;
    private final boolean setFire;
    private final boolean breakBlocks;

    public ExplosionAction(final float power) {
      this(null, true, power, false, true, null);
    }

    public ExplosionAction(final Location location, final float power) {
      this(location, false, power, false, true, null);
    }

    public ExplosionAction(final Location location, final boolean atPlayer, final float power, final boolean setFire,
        final boolean breakBlocks, final Condition conditions) {
      super(conditions);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.power = Math.max(0f, power);
      this.setFire = setFire;
      this.breakBlocks = breakBlocks;
    }

    public ExplosionAction(final Location location, final boolean atPlayer, final float power, final boolean setFire,
        final boolean breakBlocks, final Condition conditions, final boolean useSync) {
      super(conditions, useSync);
      this.location = location == null ? null : location.clone();
      this.atPlayer = atPlayer;
      this.power = Math.max(0f, power);
      this.setFire = setFire;
      this.breakBlocks = breakBlocks;
    }

    @Override
    protected boolean run(final ActionContext context) {
      final Location base = resolveLocation(context, location, atPlayer);
      if (base == null || base.getWorld() == null) {
        return true;
      }
      base.getWorld().createExplosion(base.getX(), base.getY(), base.getZ(), power, setFire, breakBlocks);
      return true;
    }
  }





  private static World resolveWorld(final ActionContext context, final String worldName) {
    if (worldName != null && !worldName.isEmpty()) {
      return Bukkit.getWorld(worldName);
    }
    final Player player = context.player();
    return player == null ? null : player.getWorld();
  }

  private static Location resolveLocation(final ActionContext context, final Location location,
      final boolean atPlayer) {
    if (atPlayer) {
      final Player player = context.player();
      return player == null ? null : player.getLocation().clone();
    }
    return location == null ? null : location.clone();
  }
}
