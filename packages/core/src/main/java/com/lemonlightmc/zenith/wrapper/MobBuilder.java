package com.lemonlightmc.zenith.wrapper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.messages.MessageFormatter;

public class MobBuilder implements Builder<LivingEntity> {
  private EntityType type;
  private Location location;
  private String customName;
  private double health;
  private List<ItemStack> armorContents = new ArrayList<>();
  private ItemStack heldItem;
  private List<PotionEffect> potionEffects = new ArrayList<>();
  private boolean showParticles = true;
  private boolean isBaby;

  private MobBuilder() {
  }

  public static MobBuilder create() {
    return new MobBuilder();
  }

  public MobBuilder type(EntityType type) {
    this.type = type;
    return this;
  }

  public MobBuilder location(Location location) {
    this.location = location;
    return this;
  }

  public MobBuilder customName(String customName) {
    this.customName = MessageFormatter.format(customName, true, false);
    return this;
  }

  public MobBuilder health(double health) {
    return this;
  }

  public MobBuilder armorContents(Material... materials) {
    for (int i = 0; i < materials.length; i++) {
      this.armorContents.add(new ItemStack(materials[i]));
    }
    return this;
  }

  public MobBuilder heldItem(Material material) {
    this.heldItem = new ItemStack(material);
    return this;
  }

  public MobBuilder showParticles(boolean showParticles) {
    this.showParticles = showParticles;
    return this;
  }

  public MobBuilder potionEffect(PotionEffectType type, int durationSeconds, int amplifier) {
    PotionEffect potionEffect = new PotionEffect(type, durationSeconds * 20, amplifier, false, this.showParticles);
    this.potionEffects.add(potionEffect);
    return this;
  }

  public MobBuilder isBaby(boolean isBaby) {
    this.isBaby = isBaby;
    return this;
  }

  @SuppressWarnings("null")
  public LivingEntity spawn() {
    this.validate();
    Entity spawnedEntity = this.location.getWorld().spawnEntity(this.location, this.type);
    if (!(spawnedEntity instanceof LivingEntity)) {
      throw new IllegalStateException("Spawned entity is not a LivingEntity");
    }
    LivingEntity mob = (LivingEntity) spawnedEntity;
    if (this.customName != null) {
      mob.setCustomName(this.customName);
      mob.setCustomNameVisible(true);
    }

    if (this.health > 0.0D) {
      mob.setHealth(this.health);
    }

    EntityEquipment equipment = mob.getEquipment();
    if (equipment != null) {
      equipment.setArmorContents((ItemStack[]) this.armorContents.toArray(new ItemStack[0]));
    }

    if (this.heldItem != null) {
      equipment.setItemInMainHand(this.heldItem);
    }

    for (PotionEffect effect : this.potionEffects) {
      mob.addPotionEffect(effect);
    }

    if (mob instanceof Ageable) {
      Ageable ageable = (Ageable) mob;
      ageable.setAdult();
      if (this.isBaby) {
        ageable.setBaby();
      }
    }
    return mob;
  }

  private void validate() {
    if (this.type == null || this.location == null) {
      throw new IllegalStateException("Type and location must be set before spawning the mob.");
    }
  }

  @Override
  public LivingEntity build() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'build'");
  }
}