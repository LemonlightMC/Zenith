package com.lemonlightmc.zenith.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.meta.FireworkMeta;

import com.lemonlightmc.zenith.interfaces.Cloneable;

public class Firework implements Cloneable<Firework>, ConfigurationSerializable {
  private List<FireworkEffect> effects;
  private int power;

  public Firework(final List<FireworkEffect> effects, final int power) {
    this.effects = effects == null ? new ArrayList<>() : effects;
    this.power = power < 0 ? 1 : power;
  }

  public Firework(final List<FireworkEffect> effects) {
    this(effects, 1);
  }

  public Firework(final Firework firework) {
    if (firework == null) {
      this.effects = new ArrayList<>();
      this.power = 1;
      return;
    }
    this.effects = firework.effects == null ? new ArrayList<>() : firework.effects;
    this.power = firework.power < 0 ? 1 : power;
  }

  public org.bukkit.entity.Firework toEntity(final Location loc) {
    if (loc == null) {
      return null;
    }
    final org.bukkit.entity.Firework entity = loc.getWorld().createEntity(loc, org.bukkit.entity.Firework.class);
    final FireworkMeta meta = entity.getFireworkMeta();
    for (final FireworkEffect fireworkEffect : effects) {
      meta.addEffects(fireworkEffect.toBukkit());
    }
    meta.setPower(power);
    return entity;
  }

  public org.bukkit.entity.Firework spawn(final Location loc) {
    if (loc == null) {
      return null;
    }
    return loc.getWorld().addEntity(toEntity(loc));
  }

  public List<FireworkEffect> getEffects() {
    return effects;
  }

  public boolean hasEffects() {
    return effects != null && !effects.isEmpty();
  }

  public Firework clearEffects() {
    if (effects != null) {
      effects.clear();
    }
    return this;
  }

  public Firework removeEffects(final FireworkEffect... effects) {
    if (effects == null || effects.length == 0 || this.effects == null || this.effects.isEmpty()) {
      return this;
    }
    return removeEffects(List.of(effects));
  }

  public Firework removeEffects(final List<FireworkEffect> effects) {
    if (effects == null || effects.isEmpty() || this.effects == null || this.effects.isEmpty()) {
      return this;
    }
    for (final FireworkEffect fireworkEffect : effects) {
      if (fireworkEffect != null) {
        this.effects.remove(fireworkEffect);
      }
    }
    return this;
  }

  public Firework addEffects(final FireworkEffect... effects) {
    if (effects == null || effects.length == 0) {
      return this;
    }
    return addEffects(List.of(effects));
  }

  public Firework addEffects(final List<FireworkEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return this;
    }
    for (final FireworkEffect fireworkEffect : effects) {
      if (fireworkEffect != null) {
        this.effects.add(fireworkEffect);
      }
    }
    return this;
  }

  public Firework setEffects(final FireworkEffect... effects) {
    if (effects != null && effects.length != 0) {
      this.effects = List.of(effects);
    }
    return this;
  }

  public Firework setEffects(final List<FireworkEffect> effects) {
    if (effects == null || effects.isEmpty()) {
      return this;
    }
    this.effects = effects;
    return this;
  }

  public int effectCount() {
    return this.effects.size();
  }

  public int getPower() {
    return this.power;
  }

  public Firework setPower(final int power) {
    this.power = power;
    return this;
  }

  public static Firework deserialize(final Map<String, Object> map) {
    return new Firework(deserializeEffectList(map.get("effects")), (int) map.get("power"));
  }

  @SuppressWarnings("unchecked")
  private static List<FireworkEffect> deserializeEffectList(final Object values) {
    if (values == null || !(values instanceof final List list)) {
      return List.of();
    }
    final List<FireworkEffect> effects = new ArrayList<>();
    for (final Object value : list) {
      if (value instanceof Map) {
        effects.add(FireworkEffect.deserialize((Map<String, Object>) value));
      }
    }
    return effects;
  }

  @Override
  public Map<String, Object> serialize() {
    return Map.<String, Object>of("effects", effects, "power", power);
  }

  @Override
  public Firework clone() {
    return new Firework(this);
  }

  @Override
  public int hashCode() {
    return 31 * (31 + effects.hashCode()) + power;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Firework other = (Firework) obj;
    if (effects == null && other.effects != null) {
      return false;
    }
    return power == other.power && effects.equals(other.effects);
  }

  @Override
  public String toString() {
    return "Firework [effects=" + effects + ", power=" + power + "]";
  }
}
