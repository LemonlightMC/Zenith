package com.lemonlightmc.zenith.items;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntitySnapshot;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.components.BlocksAttacksComponent;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.inventory.meta.components.EquippableComponent;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.JukeboxPlayableComponent;
import org.bukkit.inventory.meta.components.ToolComponent;
import org.bukkit.inventory.meta.components.UseCooldownComponent;
import org.bukkit.inventory.meta.components.WeaponComponent;
import org.bukkit.inventory.meta.components.consumable.ConsumableComponent;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;

import com.lemonlightmc.zenith.interfaces.Builder;

public class ItemStackBuilder implements Builder<ItemStack> {
  protected ItemStack item;
  protected ItemMeta meta;

  public ItemStackBuilder(final ItemStack item) {
    this.item = item;
    this.meta = item.getItemMeta();
  }

  public ItemStackBuilder(final Material material, final int amount) {
    this.item = new ItemStack(material, amount);
    this.meta = item.getItemMeta();
  }

  public ItemStackBuilder(final Material material) {
    this.item = new ItemStack(material);
    this.meta = item.getItemMeta();
  }

  public static ItemStackBuilder of(final Material material) {
    return new ItemStackBuilder(material);
  }

  public static ItemStackBuilder of(final Material material, final int amount) {
    return new ItemStackBuilder(material, amount);
  }

  public static ItemStackBuilder of(final ItemStack item) {
    return new ItemStackBuilder(item);
  }

  @Override
  public ItemStack build() {
    item.setItemMeta(meta);
    return item;
  }

  public int amount() {
    return item.getAmount();
  }

  public ItemStackBuilder amount(final int amount) {
    item.setAmount(amount);
    return this;
  }

  public boolean hasName() {
    return meta != null && this.meta.hasDisplayName();
  }

  public String name() {
    return meta.getDisplayName();
  }

  public ItemStackBuilder name(final String name) {
    meta.setDisplayName(name);
    return this;
  }

  public List<String> description() {
    return meta.getLore() == null ? List.of() : meta.getLore();
  }

  public boolean hasDescription() {
    return meta.getLore() != null;
  }

  public ItemStackBuilder description(final String... description) {
    meta.setLore(List.of(description));
    return this;
  }

  public ItemStackBuilder description(final List<String> description) {
    meta.setLore(description);
    return this;
  }

  public ItemStackBuilder description(final String description, final int index) {
    if (meta.hasLore() && meta.getLore() != null && meta.getLore().size() > index) {
      meta.getLore().set(index, description);
    } else {
      meta.setLore(List.of(description));
    }
    return this;
  }

  public ItemStackBuilder appendDescription(final String... lore) {
    if (meta.hasLore() && meta.getLore() != null) {
      meta.getLore().addAll(List.of(lore));
    } else {
      meta.setLore(List.of(lore));
    }
    return this;
  }

  public Set<NamespacedKey> tags() {
    return meta.getPersistentDataContainer().getKeys();
  }

  public <T, Z> Z tag(final NamespacedKey key, final PersistentDataType<T, Z> type) {
    return meta.getPersistentDataContainer().get(key, type);
  }

  public <T, Z> Z tagOrDefault(final NamespacedKey key, final PersistentDataType<T, Z> type, final Z value) {
    return meta.getPersistentDataContainer().getOrDefault(key, type, value);
  }

  public <T, Z> ItemStackBuilder tag(final NamespacedKey key,
      final PersistentDataType<T, Z> type, final Z data) {
    meta.getPersistentDataContainer().set(key, type, data);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final String value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final List<String> value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.LIST.strings(), value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final int value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final int[] value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.INTEGER_ARRAY, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final double value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final long value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final long[] value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.LONG_ARRAY, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final boolean value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final byte value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final byte[] value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE_ARRAY, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final short value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.SHORT, value);
    return this;
  }

  public ItemStackBuilder tag(final NamespacedKey key, final PersistentDataContainer value) {
    meta.getPersistentDataContainer().set(key, PersistentDataType.TAG_CONTAINER, value);
    return this;
  }

  public Set<ItemFlag> flags() {
    return meta.getItemFlags();
  }

  public ItemStackBuilder flags(final ItemFlag... flags) {
    meta.addItemFlags(flags);
    return this;
  }

  public AttributeModifier attributeDamage() {
    return getAttribute(Attribute.ATTACK_DAMAGE).getFirst();
  }

  public ItemStackBuilder attributeDamage(final AttributeModifier damage) {
    return setAttribute(Attribute.ATTACK_DAMAGE, damage);
  }

  public ItemStackBuilder attributeDamage(final double damage) {
    return setAttribute(Attribute.ATTACK_DAMAGE, damage);
  }

  public AttributeModifier attributeHealth() {
    return getAttribute(Attribute.MAX_HEALTH).getFirst();
  }

  public ItemStackBuilder attributeHealth(final AttributeModifier health) {
    return setAttribute(Attribute.MAX_HEALTH, health);
  }

  public ItemStackBuilder attributeHealth(final double health) {
    return setAttribute(Attribute.MAX_HEALTH, health);
  }

  public List<AttributeModifier> getAttribute(final Attribute attribute) {
    return List.copyOf(meta.getAttributeModifiers(attribute));
  }

  public ItemStackBuilder setAttribute(final Attribute attribute, final AttributeModifier value) {
    meta.removeAttributeModifier(attribute);
    meta.addAttributeModifier(attribute, value);
    return this;
  }

  public ItemStackBuilder setAttribute(final Attribute attribute, final double value) {
    setAttribute(attribute, new AttributeModifier(attribute.getKeyOrThrow(), value - 1,
        AttributeModifier.Operation.ADD_NUMBER, item.getType().getEquipmentSlot().getGroup()));
    return this;
  }

  public ItemStackBuilder setAttribute(final Attribute attribute, final double value, final EquipmentSlotGroup slot) {
    setAttribute(attribute, new AttributeModifier(attribute.getKeyOrThrow(), value - 1,
        AttributeModifier.Operation.ADD_NUMBER, slot));
    return this;
  }

  public ItemStackBuilder multiplyAttribute(final Attribute attribute, final double value) {
    meta.addAttributeModifier(attribute, new AttributeModifier(attribute.getKeyOrThrow(), value,
        AttributeModifier.Operation.MULTIPLY_SCALAR_1, item.getType().getEquipmentSlot().getGroup()));
    return this;
  }

  public ItemStackBuilder multiplyAttribute(final Attribute attribute, final double value,
      final EquipmentSlotGroup slot) {
    meta.addAttributeModifier(attribute, new AttributeModifier(attribute.getKeyOrThrow(), value,
        AttributeModifier.Operation.MULTIPLY_SCALAR_1, slot));
    return this;
  }

  public int enchantable() {
    return meta.getEnchantable();
  }

  public ItemStackBuilder enchantable(final int enchantable) {
    meta.setEnchantable(enchantable);
    return this;
  }

  public boolean enchantGlint() {
    return meta.getEnchantmentGlintOverride();
  }

  public ItemStackBuilder enchantGlint(final boolean override) {
    meta.setEnchantmentGlintOverride(override);
    return this;
  }

  public Map<Enchantment, Integer> enchants() {
    return item.getEnchantments();
  }

  public int getEnchantLevel(final Enchantment enchantment) {
    return item.getEnchantmentLevel(enchantment);
  }

  public boolean hasEnchant(final Enchantment enchantment) {
    return item.containsEnchantment(enchantment);
  }

  public boolean hasEnchant(final Enchantment enchantment, final int level) {
    return item.containsEnchantment(enchantment) && item.getEnchantmentLevel(enchantment) == level;
  }

  public ItemStackBuilder removeEnchant(final Enchantment enchantment) {
    item.removeEnchantment(enchantment);
    return this;
  }

  public ItemStackBuilder clearEnchants() {
    item.removeEnchantments();
    return this;
  }

  public ItemStackBuilder enchant(final Enchantment enchantment) {
    item.addEnchantment(enchantment, 1);
    return this;
  }

  public ItemStackBuilder enchant(final Enchantment enchantment, final int level) {
    item.addEnchantment(enchantment, level);
    return this;
  }

  public ItemStackBuilder enchants(final Map<Enchantment, Integer> enchantments) {
    item.addEnchantments(enchantments);
    return this;
  }

  public ItemStackBuilder enchants(final List<Enchantment> enchantments) {
    for (final Enchantment enchantment : enchantments) {
      item.addEnchantment(enchantment, 1);
    }
    return this;
  }

  public ItemStackBuilder enchantUnsafe(final Enchantment enchantment) {
    item.addUnsafeEnchantment(enchantment, 1);
    return this;
  }

  public ItemStackBuilder enchantUnsafe(final Enchantment enchantment, final int level) {
    item.addUnsafeEnchantment(enchantment, level);
    return this;
  }

  public ItemStackBuilder enchantsUnsafe(final Map<Enchantment, Integer> enchantments) {
    item.addUnsafeEnchantments(enchantments);
    return this;
  }

  public ItemStackBuilder enchantsUnsafe(final List<Enchantment> enchantments) {
    for (final Enchantment enchantment : enchantments) {
      item.addUnsafeEnchantment(enchantment, 1);
    }
    return this;
  }

  public ItemStackBuilder customEffect(final PotionEffectType effect, final int duration, final int level) {
    return this.customEffect(new PotionEffect(effect, duration, level));
  }

  public ItemStackBuilder customEffect(final PotionEffect effect) {
    if (isPotion()) {
      ((PotionMeta) meta).addCustomEffect(effect, true);
    }
    return this;
  }

  public ItemStackBuilder type(final Material material) {
    item.setType(material);
    return this;
  }

  public ItemStackBuilder model(final CustomModelDataComponent customModelData) {
    meta.setCustomModelDataComponent(customModelData);
    return this;
  }

  public ItemStackBuilder unbreakable() {
    return unbreakable(true);
  }

  public ItemStackBuilder unbreakable(final boolean value) {
    meta.setUnbreakable(value);
    return this;
  }

  public ItemStackBuilder breakSound(final Sound sound) {
    meta.setBreakSound(sound);
    return this;
  }

  public ItemStackBuilder consumable(final ConsumableComponent component) {
    meta.setConsumable(component);
    return this;
  }

  public ItemStackBuilder food(final FoodComponent component) {
    meta.setFood(component);
    return this;
  }

  public ItemStackBuilder equippable(final EquippableComponent component) {
    meta.setEquippable(component);
    return this;
  }

  public ItemStackBuilder damageResistant(final Tag<DamageType> tag) {
    meta.setDamageResistant(tag);
    return this;
  }

  public ItemStackBuilder glider(final boolean isGlider) {
    meta.setGlider(isGlider);
    return this;
  }

  public ItemStackBuilder jukebox(final JukeboxPlayableComponent component) {
    meta.setJukeboxPlayable(component);
    return this;
  }

  public ItemStackBuilder maxStackSize(final int size) {
    meta.setMaxStackSize(size);
    return this;
  }

  public ItemStackBuilder rarity(final ItemRarity rarity) {
    meta.setRarity(rarity);
    return this;
  }

  public ItemStackBuilder tool(final ToolComponent component) {
    meta.setTool(component);
    return this;
  }

  public ItemStackBuilder weapon(final WeaponComponent component) {
    meta.setWeapon(component);
    return this;
  }

  public ItemStackBuilder useCooldown(final UseCooldownComponent component) {
    meta.setUseCooldown(component);
    return this;
  }

  public ItemStackBuilder useCooldown(final ItemStack item) {
    meta.setUseRemainder(item);
    return this;
  }

  public ItemStackBuilder tooltip(final NamespacedKey tooltipStyle) {
    meta.setTooltipStyle(tooltipStyle);
    return this;
  }

  public ItemStackBuilder hideTooltip(final boolean hide) {
    meta.setHideTooltip(hide);
    return this;
  }

  public ItemStackBuilder blocksAttacks(final BlocksAttacksComponent component) {
    meta.setBlocksAttacks(component);
    return this;
  }

  public ItemStackBuilder setSkull(final PlayerProfile profile) {
    if (isSkull()) {
      ((SkullMeta) meta).setOwnerProfile(profile);
    }
    return this;
  }

  public ItemStackBuilder durability(final int durability) {
    if (isDurable()) {
      ((Damageable) meta).setDamage(durability);
    }
    return this;
  }

  public ItemStackBuilder repairable(final int cost) {
    if (isRepairable()) {
      ((Repairable) meta).setRepairCost(cost);
      ;
    }
    return this;
  }

  public ItemStackBuilder trim(final ArmorTrim trim) {
    if (isArmor()) {
      ((ArmorMeta) meta).setTrim(trim);
    }
    return this;
  }

  public ItemStackBuilder spawnable(final EntitySnapshot trim) {
    if (isSpawnable()) {
      ((SpawnEggMeta) meta).setSpawnedEntity(trim);
    }
    return this;
  }

  public ItemStackBuilder shieldDye(final DyeColor color) {
    if (isShield()) {
      ((ShieldMeta) meta).setBaseColor(color);
    }
    return this;
  }

  public ItemStackBuilder pattern(final Pattern pattern, final int idx) {
    if (isBanner()) {
      ((BannerMeta) meta).setPattern(idx, pattern);
    }
    return this;
  }

  public ItemStackBuilder pattern(final Pattern pattern) {
    if (isBanner()) {
      ((BannerMeta) meta).addPattern(pattern);
    }
    return this;
  }

  public ItemStackBuilder patterns(final List<Pattern> pattern) {
    if (isBanner()) {
      ((BannerMeta) meta).setPatterns(pattern);
    }
    return this;
  }

  public ItemStackBuilder fireworkPower(final int power) {
    if (isFirework()) {
      ((FireworkMeta) meta).setPower(power);
    }
    return this;
  }

  public ItemStackBuilder fireworkEffect(final FireworkEffect effect) {
    if (isFirework()) {
      ((FireworkMeta) meta).addEffect(effect);
    }
    return this;
  }

  public ItemStackBuilder fireworkEffect(final FireworkEffect... effect) {
    if (isFirework()) {
      ((FireworkMeta) meta).addEffects(effect);
    }
    return this;
  }

  public boolean isRepairable() {
    return meta instanceof Repairable;
  }

  public boolean isDurable() {
    return meta instanceof Damageable;
  }

  public boolean isArmor() {
    return meta instanceof ArmorMeta;
  }

  public boolean isLeatherArmor() {
    return meta instanceof LeatherArmorMeta;
  }

  public boolean isShield() {
    return meta instanceof ShieldMeta;
  }

  public boolean isCrossbow() {
    return meta instanceof CrossbowMeta;
  }

  public boolean isCompass() {
    return meta instanceof CompassMeta;
  }

  public boolean isBundle() {
    return meta instanceof BundleMeta;
  }

  public boolean isColorableArmor() {
    return meta instanceof ColorableArmorMeta;
  }

  public boolean isSpawnable() {
    return meta instanceof SpawnEggMeta;
  }

  public boolean isBanner() {
    return meta instanceof BannerMeta;
  }

  public boolean isFireworkEffect() {
    return meta instanceof FireworkEffectMeta;
  }

  public boolean isFirework() {
    return meta instanceof FireworkMeta;
  }

  public boolean isBook() {
    return meta instanceof BookMeta;
  }

  public boolean isWritable() {
    return meta instanceof WritableBookMeta;
  }

  public boolean isKnowledgeable() {
    return meta instanceof KnowledgeBookMeta;
  }

  public boolean isPotion() {
    return meta instanceof PotionMeta;
  }

  public boolean isEnchantmentStore() {
    return meta instanceof EnchantmentStorageMeta;
  }

  public boolean isMap() {
    return meta instanceof MapMeta;
  }

  public boolean isOminousBottle() {
    return meta instanceof OminousBottleMeta;
  }

  public boolean isAxolotlBucket() {
    return meta instanceof AxolotlBucketMeta;
  }

  public boolean isTropicalFishBucket() {
    return meta instanceof TropicalFishBucketMeta;
  }

  public boolean isSuspiciousStew() {
    return meta instanceof SuspiciousStewMeta;
  }

  public boolean isMusicInstrument() {
    return meta instanceof MusicInstrumentMeta;
  }

  public boolean isSkull() {
    return meta instanceof SkullMeta;
  }

  public boolean isSpawnEgg() {
    return meta instanceof SpawnEggMeta;
  }
}
