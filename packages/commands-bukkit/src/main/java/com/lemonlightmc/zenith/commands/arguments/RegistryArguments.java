package com.lemonlightmc.zenith.commands.arguments;

import java.util.Arrays;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.generator.structure.Structure;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.commands.executors.CommandContext;
import com.lemonlightmc.zenith.commands.arguments.MiscArguments.TimeArgument;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.items.PotionEffect;
import com.lemonlightmc.zenith.unfinished.ParticleData;

/**
 * Argument types that resolve values from Bukkit registries and related APIs.
 */
public class RegistryArguments {
  /**
   * Argument that resolves a {@link Biome} from the biome registry.
   */
  public static class BiomeArgument extends Argument<Biome, BiomeArgument, CommandSender> {

    /** Suggestion names for available biomes. */
    public static final String[] NAMES = _mapRegistry(Registry.BIOME);

    /**
     * Creates a new biome argument.
     *
     * @param name the argument name
     */
    public BiomeArgument(final String name) {
      super(name, Biome.class, ArgumentType.BIOME);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public BiomeArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a biome.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed biome
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Biome parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return resolveRegistry(Registry.BIOME, reader, this);
    }
  }

  /**
   * Argument that resolves a {@link Sound} from the sound registry.
   */
  public static class SoundArgument extends Argument<Sound, SoundArgument, CommandSender> {

    /** Suggestion names for available sounds. */
    public static final String[] NAMES = _mapRegistry(Registry.SOUNDS);

    /**
     * Creates a new sound argument.
     *
     * @param name the argument name
     */
    public SoundArgument(final String name) {
      super(name, Sound.class, ArgumentType.SOUND);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public SoundArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a sound.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed sound
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Sound parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return resolveRegistry(Registry.SOUNDS, reader, this);
    }
  }

  /**
   * Argument that resolves a particle type and wraps it in {@link ParticleData}.
   */
  @SuppressWarnings("rawtypes")
  public static class ParticleArgument extends Argument<ParticleData, ParticleArgument, CommandSender> {

    /** Suggestion names for available particle types. */
    public static final String[] NAMES = _mapRegistry(Registry.PARTICLE_TYPE);

    /**
     * Creates a new particle argument.
     *
     * @param name the argument name
     */
    public ParticleArgument(final String name) {
      super(name, ParticleData.class, ArgumentType.PARTICLE);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public ParticleArgument self() {
      return this;
    }

    /**
     * Parses the argument value as particle data.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed particle data
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public ParticleData<?> parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return new ParticleData<>(resolveRegistry(Registry.PARTICLE_TYPE, reader, this), null);
    }
  }

  /**
   * Argument that parses a Zenith {@link PotionEffect}.
   *
   * <p>
   * Format: {@code <type> [duration] [amplifier] [ambient] [particles] [icon]}.
   * Duration supports multi-unit values like {@code 1m30s4t}; amplifier is 0-255.
   * </p>
   */
  public static class PotionEffectArgument extends Argument<PotionEffect, PotionEffectArgument, CommandSender> {

    /** Suggestion names for available potion effect types. */
    public static final String[] NAMES = _mapRegistry(Registry.EFFECT);

    /**
     * Creates a new potion effect argument.
     *
     * @param name the argument name
     */
    public PotionEffectArgument(final String name) {
      super(name, PotionEffect.class, ArgumentType.POTION_EFFECT);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public PotionEffectArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a Zenith potion effect.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed potion effect
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public PotionEffect parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String rawType = reader.readString();
      final NamespacedKey typeKey = NamespacedKey.fromString(rawType);
      if (typeKey == null) {
        throw createError(reader, rawType);
      }
      final PotionEffectType type = Registry.EFFECT.get(typeKey);
      if (type == null) {
        throw createError(reader, rawType);
      }

      long durationTicks = 200L;
      int amplifier = 0;
      boolean ambient = true;
      boolean particles = true;
      boolean icon = true;

      if (reader.canRead()) {
        reader.skipWhitespace();
      }
      if (reader.canRead()) {
        final String rawDuration = reader.readString();
        final Long millis = TimeArgument.parseDurationMillis(rawDuration);
        if (millis == null) {
          throw createError(reader, rawDuration);
        }
        durationTicks = millis < 0 ? PotionEffect.INFINITE_DURATION : Math.max(0L, millis / 50L);
      }
      if (reader.canRead()) {
        reader.skipWhitespace();
      }
      if (reader.canRead()) {
        final String rawAmplifier = reader.readString();
        try {
          amplifier = Integer.parseInt(rawAmplifier);
        } catch (final NumberFormatException ex) {
          throw createError(reader, rawAmplifier);
        }
        if (amplifier < 0 || amplifier > 255) {
          throw createError(reader, rawAmplifier);
        }
      }
      if (reader.canRead()) {
        reader.skipWhitespace();
      }
      if (reader.canRead()) {
        ambient = readBooleanToken(reader);
      }
      if (reader.canRead()) {
        reader.skipWhitespace();
      }
      if (reader.canRead()) {
        particles = readBooleanToken(reader);
      }
      if (reader.canRead()) {
        reader.skipWhitespace();
      }
      if (reader.canRead()) {
        icon = readBooleanToken(reader);
      }

      return new PotionEffect(type, durationTicks, amplifier, ambient, particles, icon);
    }

    private boolean readBooleanToken(final StringReader reader) throws CommandSyntaxException {
      final String raw = reader.readString();
      if ("true".equalsIgnoreCase(raw) || "1".equals(raw) || "yes".equals(raw)) {
        return true;
      }
      if ("false".equalsIgnoreCase(raw) || "0".equals(raw) || "no".equals(raw)) {
        return false;
      }
      throw createError(reader, raw);
    }
  }

  /**
   * Argument that resolves an {@link Enchantment} from the enchantment registry.
   */
  public static class EnchantmentArgument extends Argument<Enchantment, EnchantmentArgument, CommandSender> {

    /** Suggestion names for available enchantments. */
    public static final String[] NAMES = _mapRegistry(Registry.ENCHANTMENT);

    /**
     * Creates a new enchantment argument.
     *
     * @param name the argument name
     */
    public EnchantmentArgument(final String name) {
      super(name, Enchantment.class, ArgumentType.ENCHANTMENT);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public EnchantmentArgument self() {
      return this;
    }

    /**
     * Parses the argument value as an enchantment.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed enchantment
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Enchantment parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return resolveRegistry(Registry.ENCHANTMENT, reader, this);
    }
  }

  /**
   * Argument that resolves an {@link Advancement} by key.
   */
  public static class AdvancementArgument extends Argument<Advancement, AdvancementArgument, CommandSender> {

    /** Suggestion names for available advancements. */
    @SuppressWarnings("deprecation")
    public static final String[] NAMES = _mapRegistry(Registry.ADVANCEMENT);

    /**
     * Creates a new advancement argument.
     *
     * @param name the argument name
     */
    public AdvancementArgument(final String name) {
      super(name, Advancement.class, ArgumentType.ADVANCEMENT);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public AdvancementArgument self() {
      return this;
    }

    /**
     * Parses the argument value as an advancement.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed advancement
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Advancement parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = reader.readString();
      final NamespacedKey keyNs = NamespacedKey.fromString(raw.toLowerCase(java.util.Locale.ROOT));
      if (keyNs == null) {
        throw createError(reader, raw);
      }
      final Advancement advancement = Bukkit.getAdvancement(keyNs);
      if (advancement == null) {
        throw createError(reader, raw);
      }
      return advancement;
    }
  }

  /**
   * Argument that resolves an {@link ItemStack} by material name and optional
   * amount.
   *
   * <p>
   * Supported formats: {@code material}, {@code material:amount},
   * {@code material amount}, and {@code namespace:material amount}.
   * </p>
   */
  public static class ItemStackArgument extends Argument<ItemStack, ItemStackArgument, CommandSender> {

    /** Suggestion names for available item materials. */
    public static final String[] NAMES = _mapRegistry(Registry.ITEM);

    /**
     * Creates a new item stack argument.
     *
     * @param name the argument name
     */
    public ItemStackArgument(final String name) {
      super(name, ItemStack.class, ArgumentType.ITEMSTACK);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public ItemStackArgument self() {
      return this;
    }

    /**
     * Parses the argument value as an item stack.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed item stack
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public ItemStack parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = readResourceToken(reader);
      if (raw.isEmpty()) {
        throw createError(reader, raw);
      }

      String materialToken = raw;
      Integer amount = null;

      final int lastColon = raw.lastIndexOf(':');
      if (lastColon > 0 && lastColon < raw.length() - 1) {
        final String tail = raw.substring(lastColon + 1);
        if (isInteger(tail)) {
          amount = NumberConversions.parseInt(tail);
          materialToken = raw.substring(0, lastColon);
        }
      }

      if (amount == null && reader.canRead()) {
        reader.skipWhitespace();
        final int cursor = reader.getCursor();
        if (reader.canRead()) {
          final String maybeAmount = reader.readString();
          if (isInteger(maybeAmount)) {
            amount = NumberConversions.parseInt(maybeAmount);
          } else {
            reader.setCursor(cursor);
          }
        }
      }

      final Material material = resolveMaterial(materialToken);
      if (material == null || material.isAir()) {
        throw createError(reader, materialToken);
      }
      final int finalAmount = amount == null ? 1 : amount;
      if (finalAmount <= 0) {
        throw createError(reader, String.valueOf(finalAmount));
      }
      return new ItemStack(material, finalAmount);
    }

    private static boolean isInteger(final String value) {
      if (value == null || value.isEmpty()) {
        return false;
      }
      for (int i = 0; i < value.length(); i++) {
        final char c = value.charAt(i);
        if (c < '0' || c > '9') {
          return false;
        }
      }
      return true;
    }

    private static Material resolveMaterial(final String raw) {
      if (raw == null || raw.isEmpty()) {
        return null;
      }
      Material material = Material.matchMaterial(raw);
      if (material != null) {
        return material;
      }
      final NamespacedKey key = NamespacedKey.fromString(raw.toLowerCase(java.util.Locale.ROOT));
      if (key != null) {
        material = Registry.MATERIAL.get(key);
      }
      return material;
    }

    private static String readResourceToken(final StringReader reader) throws CommandSyntaxException {
      if (!reader.canRead()) {
        return "";
      }
      final char next = reader.peek();
      if (StringReader.isQuote(next)) {
        return reader.readString();
      }
      final int start = reader.getCursor();
      while (reader.canRead() && !Character.isWhitespace(reader.peek())) {
        reader.skip();
      }
      return reader.getString().substring(start, reader.getCursor());
    }
  }

  /**
   * Argument that resolves a {@link Material} by name.
   */
  public static class MaterialArgument extends Argument<Material, MaterialArgument, CommandSender> {

    /** Suggestion names for available materials. */
    public static final String[] NAMES = _mapRegistry(Registry.MATERIAL);

    /**
     * Creates a new material argument.
     *
     * @param name the argument name
     */
    public MaterialArgument(final String name) {
      super(name, Material.class, ArgumentType.MATERIAL);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public MaterialArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a material.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed material
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Material parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = reader.readString();
      Material material = Material.matchMaterial(raw);
      if (material == null) {
        final NamespacedKey namespacedKey = NamespacedKey.fromString(raw.toLowerCase(java.util.Locale.ROOT));
        if (namespacedKey != null) {
          material = Registry.MATERIAL.get(namespacedKey);
        }
      }
      if (material == null) {
        throw createError(reader, raw);
      }
      return material;
    }
  }

  /**
   * Argument that resolves a {@link Structure} from the structure registry.
   */
  public static class StructureArgument extends Argument<Structure, StructureArgument, CommandSender> {

    /** Suggestion names for available structures. */
    public static final String[] NAMES = _mapRegistry(Registry.STRUCTURE);

    /**
     * Creates a new structure argument.
     *
     * @param name the argument name
     */
    public StructureArgument(final String name) {
      super(name, Structure.class, ArgumentType.STRUCTURE);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public StructureArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a structure.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed structure
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Structure parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return resolveRegistry(Registry.STRUCTURE, reader, this);
    }
  }

  /**
   * Argument that resolves an {@link Attribute} from the attribute registry.
   */
  public static class AttributeArgument extends Argument<Attribute, AttributeArgument, CommandSender> {

    /** Suggestion names for available attributes. */
    public static final String[] NAMES = _mapRegistry(Registry.ATTRIBUTE);

    /**
     * Creates a new attribute argument.
     *
     * @param name the argument name
     */
    public AttributeArgument(final String name) {
      super(name, Attribute.class, ArgumentType.ATTRIBUTE);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public AttributeArgument self() {
      return this;
    }

    /**
     * Parses the argument value as an attribute.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed attribute
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Attribute parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return resolveRegistry(Registry.ATTRIBUTE, reader, this);
    }
  }

  /**
   * Argument that resolves a {@link StructureType} from the structure type
   * registry.
   */
  public static class StructureTypeArgument extends Argument<StructureType, StructureTypeArgument, CommandSender> {

    /** Suggestion names for available structure types. */
    public static final String[] NAMES = _mapRegistry(Registry.STRUCTURE_TYPE);

    /**
     * Creates a new structure type argument.
     *
     * @param name the argument name
     */
    public StructureTypeArgument(final String name) {
      super(name, StructureType.class, ArgumentType.STRUCTURE_TYPE);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public StructureTypeArgument self() {
      return this;
    }

    /**
     * Parses the argument value as a structure type.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed structure type
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public StructureType parseArgument(final CommandContext<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return resolveRegistry(Registry.STRUCTURE_TYPE, reader, this);
    }
  }

  /**
   * Argument that resolves an {@link EntityType} by name.
   */
  public static class EntityTypeArgument extends Argument<EntityType, EntityTypeArgument, CommandSender> {

    /** Suggestion names for available entity types. */
    public static final String[] NAMES = _mapRegistry(Registry.ENTITY_TYPE);

    /**
     * Creates a new entity type argument.
     *
     * @param name the argument name
     */
    public EntityTypeArgument(final String name) {
      super(name, EntityType.class, ArgumentType.ENTITY_TYPE);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public EntityTypeArgument self() {
      return this;
    }

    /**
     * Parses the argument value as an entity type.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key    the argument key
     * @return the parsed entity type
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public EntityType parseArgument(final CommandContext<CommandSender> source,
        final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String raw = reader.readString();
      final NamespacedKey namespacedKey = NamespacedKey.fromString(raw.toLowerCase(java.util.Locale.ROOT));
      EntityType type = namespacedKey != null ? Registry.ENTITY_TYPE.get(namespacedKey) : null;
      if (type == null) {
        try {
          type = EntityType.valueOf(raw.toUpperCase(java.util.Locale.ROOT));
        } catch (final IllegalArgumentException ignored) {
        }
      }
      if (type == null) {
        throw createError(reader, raw);
      }
      return type;
    }
  }

  private static <T extends Keyed> T resolveRegistry(final Registry<T> registry, final StringReader reader,
      final Argument<?, ?, CommandSender> arg) throws CommandSyntaxException {
    final String raw = reader.readString();
    final NamespacedKey key = NamespacedKey.fromString(raw.toLowerCase(java.util.Locale.ROOT));
    if (key == null) {
      throw arg.createError(reader, raw);
    }
    final T value = registry.get(key);
    if (value == null) {
      throw arg.createError(reader, raw);
    }
    return value;
  }

  /**
   * Maps registry entries to their string keys.
   *
   * @param registry the registry to map
   * @param <T>      the keyed registry entry type
   * @return an array of registry key strings
   */
  public final static <T extends Keyed> String[] _mapRegistry(final Registry<T> registry) {
    return registry.stream().map((final T b) -> {
      final NamespacedKey key = b.getKey();
      return key == null ? null : key.toString();
    }).filter((s) -> s != null).toArray(String[]::new);
  }
}
