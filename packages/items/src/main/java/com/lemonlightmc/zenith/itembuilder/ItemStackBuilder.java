package com.lemonlightmc.zenith.itembuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.inventory.meta.OminousBottleMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.inventory.meta.ShieldMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.SuspiciousStewMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.inventory.meta.WritableBookMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import com.lemonlightmc.zenith.additive.interfaces.Builder;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Fluent builder for creating and modifying {@link ItemStack} instances.
 *
 * <p>Supports typed {@link ItemMeta} configuration, reusable {@link ItemModifier modifiers},
 * and safe material changes. Unless created via {@link #editing(ItemStack)}, factories operate on
 * a copy of the provided input; {@link #build()} returns a detached clone. Meta operations are
 * no-ops when no compatible meta is present.</p>
 *
 * @param <B> the concrete builder type for fluent chaining
 */
@SuppressWarnings("unchecked")
public class ItemStackBuilder<B extends ItemStackBuilder<B>> implements Builder<ItemStack>, Cloneable {

  /** The working item stack. */
  protected ItemStack item;
  /** The working item meta, kept compatible with {@link #item}'s type when possible. */
  protected ItemMeta meta;

  /**
   * Creates a builder around an existing item stack.
   *
   * @param item the item stack to work on (must not be {@code null})
   * @param copy {@code true} to clone the stack before editing
   */
  protected ItemStackBuilder(final ItemStack item, final boolean copy) {
    if (item == null) {
      throw new IllegalArgumentException("item cannot be null");
    }
    this.item = copy ? item.clone() : item;
    this.meta = this.item.getItemMeta();
  }

  /**
   * Creates a builder for a new item stack.
   *
   * @param material the item material
   * @param amount the stack amount
   */
  protected ItemStackBuilder(final Material material, final int amount) {
    if (material == null || material.isAir()) {
      throw new IllegalArgumentException("material cannot be null or air");
    }
    final int safeAmount = Math.max(1, amount);
    this.item = new ItemStack(material, safeAmount);
    this.meta = this.item.getItemMeta();
  }

  /**
   * Creates a builder for a new item stack with amount 1.
   *
   * @param material the item material
   */
  protected ItemStackBuilder(final Material material) {
    this(material, 1);
  }

  /* -------------------- factories -------------------- */

  public static ItemStackBuilder<?> of(final Material material) {
    return new ItemStackBuilder<>(material);
  }

  public static ItemStackBuilder<?> of(final Material material, final int amount) {
    return new ItemStackBuilder<>(material, amount);
  }

  /**
   * Creates a builder from a copy of the given item stack.
   */
  public static ItemStackBuilder<?> of(final ItemStack item) {
    return new ItemStackBuilder<>(item, true);
  }

  /**
   * Creates a builder that mutates the given item stack in place.
   */
  public static ItemStackBuilder<?> editing(final ItemStack item) {
    return new ItemStackBuilder<>(item, false);
  }

  /* -------------------- core -------------------- */

  protected B self() {
    return (B) this;
  }

  /**
   * Returns the working item stack (live).
   */
  public ItemStack item() {
    return item;
  }

  /**
   * Returns the working item meta (may be {@code null} for air-like items).
   */
  public ItemMeta meta() {
    return meta;
  }

  /**
   * Applies the working meta to the working stack if present.
   */
  protected void syncMeta() {
    if (meta != null) {
      item.setItemMeta(meta);
    }
  }

  /**
   * Rebinds the working meta to the current stack type when necessary.
   */
  protected void rebindMeta() {
    if (meta == null) {
      meta = item.getItemMeta();
      return;
    }
    try {
      meta = Bukkit.getItemFactory().asMetaFor(meta, item.getType());
    } catch (final Throwable ignored) {
      meta = item.getItemMeta();
    }
  }

  /**
   * Builds and returns a detached clone of the configured item.
   */
  @Override
  public ItemStack build() {
    syncMeta();
    return item.clone();
  }

  /**
   * Applies the configured state to the given target stack and returns it.
   */
  public ItemStack applyTo(final ItemStack target) {
    if (target == null) {
      throw new IllegalArgumentException("target cannot be null");
    }
    syncMeta();
    target.setType(item.getType());
    target.setAmount(item.getAmount());
    if (meta != null) {
      target.setItemMeta(meta.clone());
    } else {
      target.setItemMeta(null);
    }
    for (final Map.Entry<Enchantment, Integer> e : item.getEnchantments().entrySet()) {
      target.addUnsafeEnchantment(e.getKey(), e.getValue());
    }
    return target;
  }

  /**
   * Creates a deep copy of this builder.
   */
  @Override
  public B clone() {
    try {
      final ItemStackBuilder<?> copy = (ItemStackBuilder<?>) super.clone();
      copy.item = this.item == null ? null : this.item.clone();
      copy.meta = this.meta == null ? null : this.meta.clone();
      return (B) copy;
    } catch (final CloneNotSupportedException e) {
      throw new AssertionError(e);
    }
  }

  /* -------------------- type / amount -------------------- */

  public Material type() {
    return item.getType();
  }

  public B type(final Material material) {
    if (material == null) {
      throw new IllegalArgumentException("material cannot be null");
    }
    item.setType(material);
    rebindMeta();
    return self();
  }

  /**
   * Alias for {@link #type(Material)}.
   */
  public B material(final Material material) {
    return type(material);
  }

  public int amount() {
    return item.getAmount();
  }

  public B amount(final int amount) {
    item.setAmount(amount);
    return self();
  }

  public B amount(final IntUnaryOperator operator) {
    if (operator != null) {
      item.setAmount(operator.applyAsInt(item.getAmount()));
    }
    return self();
  }

  public B amountMax() {
    final int max = item.getType().getMaxStackSize();
    item.setAmount(max > 0 ? max : item.getAmount());
    return self();
  }

  /* -------------------- meta access -------------------- */

  /**
   * Edits the working meta if present.
   */
  public B meta(final Consumer<? super ItemMeta> consumer) {
    if (consumer != null && meta != null) {
      consumer.accept(meta);
    }
    return self();
  }

  /**
   * Edits the working meta if it is of the given type.
   */
  public <M extends ItemMeta> B meta(final Class<M> type, final Consumer<? super M> consumer) {
    if (type != null && consumer != null && type.isInstance(meta)) {
      consumer.accept(type.cast(meta));
    }
    return self();
  }

  /**
   * Edits the working meta if it is of the given type; throws if incompatible or absent.
   */
  public <M extends ItemMeta> B requireMeta(final Class<M> type, final Consumer<? super M> consumer) {
    if (type == null) {
      throw new IllegalArgumentException("meta type cannot be null");
    }
    if (meta == null) {
      throw new IllegalStateException("Item has no meta: " + item.getType());
    }
    if (!type.isInstance(meta)) {
      throw new IllegalStateException("Meta for " + item.getType() + " is " + meta.getClass().getName()
          + ", expected " + type.getName());
    }
    if (consumer != null) {
      consumer.accept(type.cast(meta));
    }
    return self();
  }

  /**
   * Edits the item stack and its meta.
   */
  public B edit(final java.util.function.BiConsumer<ItemStack, ItemMeta> editor) {
    if (editor != null) {
      editor.accept(item, meta);
    }
    return self();
  }

  /**
   * Edits the raw item stack.
   */
  public B editItem(final Consumer<ItemStack> editor) {
    if (editor != null) {
      editor.accept(item);
      this.meta = item.getItemMeta();
    }
    return self();
  }

  /**
   * Replaces the working item stack with the result of the transform (applied to a clone).
   */
  public B replace(final UnaryOperator<ItemStack> transform) {
    if (transform != null) {
      final ItemStack result = transform.apply(item.clone());
      if (result != null) {
        this.item = result;
        this.meta = this.item.getItemMeta();
      }
    }
    return self();
  }

  /* -------------------- conditional composition -------------------- */

  public B when(final boolean condition, final Consumer<? super B> action) {
    if (condition && action != null) {
      action.accept(self());
    }
    return self();
  }

  public B when(final boolean condition, final ItemModifier modifier) {
    if (condition && modifier != null) {
      modifier.apply(this);
    }
    return self();
  }

  public B unless(final boolean condition, final Consumer<? super B> action) {
    return when(!condition, action);
  }

  public B unless(final boolean condition, final ItemModifier modifier) {
    return when(!condition, modifier);
  }

  public B applyIf(final boolean condition, final ItemModifier modifier) {
    return when(condition, modifier);
  }

  public B applyIf(final BooleanSupplier condition, final ItemModifier modifier) {
    return when(condition != null && condition.getAsBoolean(), modifier);
  }

  /* -------------------- name / lore (legacy + component) -------------------- */

  protected static String legacy(final Component component) {
    return component == null ? null : LegacyComponentSerializer.legacySection().serialize(component);
  }

  public boolean hasName() {
    return meta != null && meta.hasDisplayName();
  }

  public String name() {
    return meta != null ? meta.getDisplayName() : null;
  }

  public B name(final String name) {
    if (meta != null) {
      meta.setDisplayName(name);
    }
    return self();
  }

  public B name(final Component name) {
    return name(legacy(name));
  }

  public boolean hasItemName() {
    return meta != null && meta.hasItemName();
  }

  public B itemName(final String name) {
    if (meta != null) {
      meta.setItemName(name);
    }
    return self();
  }

  public B itemName(final Component name) {
    return itemName(legacy(name));
  }

  public boolean hasLore() {
    return meta != null && meta.hasLore();
  }

  public List<String> lore() {
    if (meta == null || meta.getLore() == null) {
      return List.of();
    }
    return List.copyOf(meta.getLore());
  }

  public B lore(final String... lines) {
    if (meta != null) {
      meta.setLore(lines == null ? null : Arrays.asList(lines));
    }
    return self();
  }

  public B lore(final List<String> lines) {
    if (meta != null) {
      meta.setLore(lines == null ? null : new ArrayList<>(lines));
    }
    return self();
  }

  public B lore(final Component... lines) {
    if (meta == null) {
      return self();
    }
    if (lines == null) {
      meta.setLore(null);
      return self();
    }
    final List<String> out = new ArrayList<>(lines.length);
    for (final Component line : lines) {
      out.add(legacy(line));
    }
    meta.setLore(out);
    return self();
  }

  public B loreComponents(final List<? extends Component> lines) {
    if (meta == null) {
      return self();
    }
    if (lines == null) {
      meta.setLore(null);
      return self();
    }
    final List<String> out = new ArrayList<>(lines.size());
    for (final Component line : lines) {
      out.add(legacy(line));
    }
    meta.setLore(out);
    return self();
  }

  public B appendLore(final String... lines) {
    if (meta == null || lines == null || lines.length == 0) {
      return self();
    }
    final List<String> lore = meta.hasLore() && meta.getLore() != null ? new ArrayList<>(meta.getLore())
        : new ArrayList<>();
    lore.addAll(Arrays.asList(lines));
    meta.setLore(lore);
    return self();
  }

  public B appendLore(final Collection<String> lines) {
    if (meta == null || lines == null || lines.isEmpty()) {
      return self();
    }
    final List<String> lore = meta.hasLore() && meta.getLore() != null ? new ArrayList<>(meta.getLore())
        : new ArrayList<>();
    lore.addAll(lines);
    meta.setLore(lore);
    return self();
  }

  public B appendLore(final Component... lines) {
    if (meta == null || lines == null || lines.length == 0) {
      return self();
    }
    final List<String> lore = meta.hasLore() && meta.getLore() != null ? new ArrayList<>(meta.getLore())
        : new ArrayList<>();
    for (final Component line : lines) {
      lore.add(legacy(line));
    }
    meta.setLore(lore);
    return self();
  }

  public B appendLoreComponents(final List<? extends Component> lines) {
    if (meta == null || lines == null || lines.isEmpty()) {
      return self();
    }
    final List<String> lore = meta.hasLore() && meta.getLore() != null ? new ArrayList<>(meta.getLore())
        : new ArrayList<>();
    for (final Component line : lines) {
      lore.add(legacy(line));
    }
    meta.setLore(lore);
    return self();
  }

  public B clearLore() {
    if (meta != null) {
      meta.setLore(null);
    }
    return self();
  }

  /* -------------------- durability / state -------------------- */

  public B unbreakable(final boolean unbreakable) {
    if (meta != null) {
      meta.setUnbreakable(unbreakable);
    }
    return self();
  }

  public B damage(final int damage) {
    if (meta instanceof Damageable d) {
      d.setDamage(damage);
    }
    return self();
  }

  public B maxDamage(final Integer maxDamage) {
    if (meta instanceof Damageable d) {
      d.setMaxDamage(maxDamage);
    }
    return self();
  }

  public B repairCost(final int cost) {
    if (meta instanceof Repairable r) {
      r.setRepairCost(cost);
    }
    return self();
  }

  public B customModelData(final Integer data) {
    if (meta != null) {
      meta.setCustomModelData(data);
    }
    return self();
  }

  public B customModelData(final CustomModelDataComponent component) {
    if (meta != null) {
      meta.setCustomModelDataComponent(component);
    }
    return self();
  }

  public B maxStackSize(final Integer size) {
    if (meta != null) {
      meta.setMaxStackSize(size);
    }
    return self();
  }

  public B hideTooltip(final boolean hide) {
    if (meta != null) {
      meta.setHideTooltip(hide);
    }
    return self();
  }

  public B tooltipStyle(final NamespacedKey style) {
    if (meta != null) {
      meta.setTooltipStyle(style);
    }
    return self();
  }

  public B glider(final boolean glider) {
    if (meta != null) {
      meta.setGlider(glider);
    }
    return self();
  }

  public B enchantmentGlintOverride(final Boolean override) {
    if (meta != null) {
      meta.setEnchantmentGlintOverride(override);
    }
    return self();
  }

  public B glint() {
    return enchantmentGlintOverride(Boolean.TRUE);
  }

  public B glint(final boolean glint) {
    return enchantmentGlintOverride(glint);
  }

  public B resetGlint() {
    return enchantmentGlintOverride(null);
  }

  public B rarity(final org.bukkit.inventory.ItemRarity rarity) {
    if (meta != null) {
      meta.setRarity(rarity);
    }
    return self();
  }

  public B breakSound(final org.bukkit.Sound sound) {
    if (meta != null) {
      meta.setBreakSound(sound);
    }
    return self();
  }

  public B damageResistant(final org.bukkit.Tag<org.bukkit.damage.DamageType> tag) {
    if (meta != null) {
      meta.setDamageResistant(tag);
    }
    return self();
  }

  public B food(final org.bukkit.inventory.meta.components.FoodComponent component) {
    if (meta != null) {
      meta.setFood(component);
    }
    return self();
  }

  public B tool(final org.bukkit.inventory.meta.components.ToolComponent component) {
    if (meta != null) {
      meta.setTool(component);
    }
    return self();
  }

  public B consumable(final org.bukkit.inventory.meta.components.consumable.ConsumableComponent component) {
    if (meta != null) {
      meta.setConsumable(component);
    }
    return self();
  }

  public B equippable(final org.bukkit.inventory.meta.components.EquippableComponent component) {
    if (meta != null) {
      meta.setEquippable(component);
    }
    return self();
  }

  public B weapon(final org.bukkit.inventory.meta.components.WeaponComponent component) {
    if (meta != null) {
      meta.setWeapon(component);
    }
    return self();
  }

  public B blocksAttacks(final org.bukkit.inventory.meta.components.BlocksAttacksComponent component) {
    if (meta != null) {
      meta.setBlocksAttacks(component);
    }
    return self();
  }

  public B useCooldown(final org.bukkit.inventory.meta.components.UseCooldownComponent component) {
    if (meta != null) {
      meta.setUseCooldown(component);
    }
    return self();
  }

  public B jukeboxPlayable(final org.bukkit.inventory.meta.components.JukeboxPlayableComponent component) {
    if (meta != null) {
      meta.setJukeboxPlayable(component);
    }
    return self();
  }

  /* -------------------- flags -------------------- */

  public B flags(final ItemFlag... flags) {
    if (meta != null && flags != null && flags.length > 0) {
      meta.addItemFlags(flags);
    }
    return self();
  }

  public B removeFlags(final ItemFlag... flags) {
    if (meta != null && flags != null && flags.length > 0) {
      meta.removeItemFlags(flags);
    }
    return self();
  }

  public B hideAllFlags() {
    if (meta != null) {
      meta.addItemFlags(ItemFlag.values());
    }
    return self();
  }

  public B hideFlags() {
    return hideAllFlags();
  }

  /* -------------------- enchantments (incl. stored for books) -------------------- */

  public B enchant(final Enchantment enchantment, final int level) {
    if (enchantment == null) {
      return self();
    }
    if (meta instanceof EnchantmentStorageMeta esm) {
      esm.addStoredEnchant(enchantment, level, false);
    } else {
      item.addEnchantment(enchantment, level);
    }
    return self();
  }

  public B enchantUnsafe(final Enchantment enchantment, final int level) {
    if (enchantment == null) {
      return self();
    }
    if (meta instanceof EnchantmentStorageMeta esm) {
      esm.addStoredEnchant(enchantment, level, true);
    } else {
      item.addUnsafeEnchantment(enchantment, level);
    }
    return self();
  }

  public B enchants(final Map<Enchantment, Integer> enchantments) {
    if (enchantments == null || enchantments.isEmpty()) {
      return self();
    }
    if (meta instanceof EnchantmentStorageMeta esm) {
      for (final Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
        if (e.getKey() != null && e.getValue() != null) {
          esm.addStoredEnchant(e.getKey(), e.getValue(), false);
        }
      }
    } else {
      item.addEnchantments(enchantments);
    }
    return self();
  }

  public B enchantsUnsafe(final Map<Enchantment, Integer> enchantments) {
    if (enchantments == null || enchantments.isEmpty()) {
      return self();
    }
    if (meta instanceof EnchantmentStorageMeta esm) {
      for (final Map.Entry<Enchantment, Integer> e : enchantments.entrySet()) {
        if (e.getKey() != null && e.getValue() != null) {
          esm.addStoredEnchant(e.getKey(), e.getValue(), true);
        }
      }
    } else {
      item.addUnsafeEnchantments(enchantments);
    }
    return self();
  }

  public B removeEnchant(final Enchantment enchantment) {
    if (enchantment == null) {
      return self();
    }
    if (meta instanceof EnchantmentStorageMeta esm) {
      esm.removeStoredEnchant(enchantment);
    }
    item.removeEnchantment(enchantment);
    return self();
  }

  public B clearEnchants() {
    if (meta instanceof EnchantmentStorageMeta esm) {
      for (final Enchantment e : new ArrayList<>(esm.getStoredEnchants().keySet())) {
        esm.removeStoredEnchant(e);
      }
    }
    for (final Enchantment e : new ArrayList<>(item.getEnchantments().keySet())) {
      item.removeEnchantment(e);
    }
    return self();
  }

  public B storedEnchant(final Enchantment enchantment, final int level) {
    return storedEnchant(enchantment, level, false);
  }

  public B storedEnchant(final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
    if (meta instanceof EnchantmentStorageMeta esm && enchantment != null) {
      esm.addStoredEnchant(enchantment, level, ignoreLevelRestriction);
    }
    return self();
  }

  public B removeStoredEnchant(final Enchantment enchantment) {
    if (meta instanceof EnchantmentStorageMeta esm && enchantment != null) {
      esm.removeStoredEnchant(enchantment);
    }
    return self();
  }

  public B clearStoredEnchants() {
    if (meta instanceof EnchantmentStorageMeta esm) {
      for (final Enchantment e : new ArrayList<>(esm.getStoredEnchants().keySet())) {
        esm.removeStoredEnchant(e);
      }
    }
    return self();
  }

  /* -------------------- attributes / pdc -------------------- */

  public B attribute(final Attribute attribute, final AttributeModifier modifier) {
    if (meta != null && attribute != null && modifier != null) {
      meta.removeAttributeModifier(attribute);
      meta.addAttributeModifier(attribute, modifier);
    }
    return self();
  }

  public B addAttribute(final Attribute attribute, final AttributeModifier modifier) {
    if (meta != null && attribute != null && modifier != null) {
      meta.addAttributeModifier(attribute, modifier);
    }
    return self();
  }

  public B removeAttribute(final Attribute attribute) {
    if (meta != null && attribute != null) {
      meta.removeAttributeModifier(attribute);
    }
    return self();
  }

  public B clearAttributes() {
    if (meta != null) {
      meta.setAttributeModifiers(null);
    }
    return self();
  }

  public <T, Z> B pdc(final NamespacedKey key, final PersistentDataType<T, Z> type, final Z value) {
    if (meta != null && key != null && type != null) {
      if (value == null) {
        meta.getPersistentDataContainer().remove(key);
      } else {
        meta.getPersistentDataContainer().set(key, type, value);
      }
    }
    return self();
  }

  public B removePdc(final NamespacedKey key) {
    if (meta != null && key != null) {
      meta.getPersistentDataContainer().remove(key);
    }
    return self();
  }

  /* -------------------- discoverability meta helpers -------------------- */

  public B skull(final Consumer<? super SkullMeta> consumer) { return meta(SkullMeta.class, consumer); }
  public B potionMeta(final Consumer<? super PotionMeta> consumer) { return meta(PotionMeta.class, consumer); }
  public B potion(final Consumer<? super PotionMeta> consumer) { return meta(PotionMeta.class, consumer); }
  public B book(final Consumer<? super BookMeta> consumer) { return meta(BookMeta.class, consumer); }
  public B writableBook(final Consumer<? super WritableBookMeta> consumer) { return meta(WritableBookMeta.class, consumer); }
  public B banner(final Consumer<? super BannerMeta> consumer) { return meta(BannerMeta.class, consumer); }
  public B leatherArmor(final Consumer<? super LeatherArmorMeta> consumer) { return meta(LeatherArmorMeta.class, consumer); }
  public B firework(final Consumer<? super FireworkMeta> consumer) { return meta(FireworkMeta.class, consumer); }
  public B fireworkEffect(final Consumer<? super FireworkEffectMeta> consumer) { return meta(FireworkEffectMeta.class, consumer); }
  public B map(final Consumer<? super MapMeta> consumer) { return meta(MapMeta.class, consumer); }
  public B compass(final Consumer<? super CompassMeta> consumer) { return meta(CompassMeta.class, consumer); }
  public B crossbow(final Consumer<? super CrossbowMeta> consumer) { return meta(CrossbowMeta.class, consumer); }
  public B bundle(final Consumer<? super BundleMeta> consumer) { return meta(BundleMeta.class, consumer); }
  public B armor(final Consumer<? super ArmorMeta> consumer) { return meta(ArmorMeta.class, consumer); }
  public B axolotlBucket(final Consumer<? super AxolotlBucketMeta> consumer) { return meta(AxolotlBucketMeta.class, consumer); }
  public B tropicalFishBucket(final Consumer<? super TropicalFishBucketMeta> consumer) { return meta(TropicalFishBucketMeta.class, consumer); }
  public B blockState(final Consumer<? super BlockStateMeta> consumer) { return meta(BlockStateMeta.class, consumer); }
  public B knowledgeBook(final Consumer<? super KnowledgeBookMeta> consumer) { return meta(KnowledgeBookMeta.class, consumer); }
  public B suspiciousStew(final Consumer<? super SuspiciousStewMeta> consumer) { return meta(SuspiciousStewMeta.class, consumer); }
  public B musicInstrument(final Consumer<? super MusicInstrumentMeta> consumer) { return meta(MusicInstrumentMeta.class, consumer); }
  public B ominousBottle(final Consumer<? super OminousBottleMeta> consumer) { return meta(OminousBottleMeta.class, consumer); }
  public B shield(final Consumer<? super ShieldMeta> consumer) { return meta(ShieldMeta.class, consumer); }
  public B damageable(final Consumer<? super Damageable> consumer) { return meta(Damageable.class, consumer); }
  public B repairable(final Consumer<? super Repairable> consumer) { return meta(Repairable.class, consumer); }
  public B enchantmentStorage(final Consumer<? super EnchantmentStorageMeta> consumer) { return meta(EnchantmentStorageMeta.class, consumer); }

  /* -------------------- modifiers -------------------- */

  public B apply(final ItemModifier modifier) {
    if (modifier != null) {
      modifier.apply(this);
    }
    return self();
  }

  public B apply(final ItemModifier... modifiers) {
    if (modifiers != null) {
      for (final ItemModifier modifier : modifiers) {
        if (modifier != null) {
          modifier.apply(this);
        }
      }
    }
    return self();
  }

  public B apply(final Iterable<? extends ItemModifier> modifiers) {
    if (modifiers != null) {
      for (final ItemModifier modifier : modifiers) {
        if (modifier != null) {
          modifier.apply(this);
        }
      }
    }
    return self();
  }

  /**
   * Alias for {@link #apply(ItemModifier...)}.
   */
  public B components(final ItemModifier... modifiers) {
    return apply(modifiers);
  }

  public B components(final ItemModifier modifier) {
    return apply(modifier);
  }

  public B components(final Iterable<? extends ItemModifier> modifiers) {
    return apply(modifiers);
  }

  @Override
  public String toString() {
    return "ItemStackBuilder{" + item + '}';
  }
}
