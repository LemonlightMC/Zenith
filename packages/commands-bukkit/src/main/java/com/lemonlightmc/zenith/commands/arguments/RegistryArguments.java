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
import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
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
    public BiomeArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a biome.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed biome
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Biome parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Registry.BIOME.getOrThrow(NamespacedKey.fromString(reader.readString()));
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
    public SoundArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a sound.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed sound
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Sound parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Registry.SOUNDS.getOrThrow(NamespacedKey.fromString(reader.readString()));
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
    public ParticleArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as particle data.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed particle data
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public ParticleData<?> parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return new ParticleData<>(Registry.PARTICLE_TYPE.getOrThrow(NamespacedKey.fromString(reader.readString())), null);
    }
  }

  /**
   * Argument that resolves a {@link PotionEffectType} from the effect registry.
   */
  public static class PotionEffectArgument extends Argument<PotionEffectType, PotionEffectArgument, CommandSender> {

    /** Suggestion names for available potion effect types. */
    public static final String[] NAMES = _mapRegistry(Registry.EFFECT);

    /**
     * Creates a new potion effect argument.
     *
     * @param name the argument name
     */
    public PotionEffectArgument(final String name) {
      super(name, PotionEffectType.class, ArgumentType.POTION_EFFECT);
      withSuggestions(NAMES);
    }

    /**
     * Returns this argument instance.
     *
     * @return this argument
     */
    @Override
    public PotionEffectArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a potion effect type.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed potion effect type
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public PotionEffectType parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.EFFECT.getOrThrow(NamespacedKey.fromString(reader.readString()));
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
    public EnchantmentArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as an enchantment.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed enchantment
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Enchantment parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.ENCHANTMENT.getOrThrow(Objects.requireNonNull(NamespacedKey.fromString(reader.readString())));
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
    public AdvancementArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as an advancement.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed advancement
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Advancement parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects
          .requireNonNull(Bukkit.getAdvancement(Objects.requireNonNull(NamespacedKey.fromString(reader.readString()))));
    }
  }

  /**
   * Argument that resolves an {@link ItemStack} by material name and optional amount.
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
    public ItemStackArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as an item stack.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed item stack
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public ItemStack parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      final String[] value = reader.readList(':');
      if (value.length == 0) {
        throw createError(reader, Arrays.toString(value));
      }
      final Material mat = Objects.requireNonNull(Material.getMaterial(value[0]));
      if (value.length > 1) {
        final int amount = NumberConversions.parseInt(value[1]);
        return new ItemStack(mat, amount);
      }
      return new ItemStack(mat, 1);
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
    public MaterialArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a material.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed material
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Material parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Material.getMaterial(reader.readString()));
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
    public StructureArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a structure.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed structure
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Structure parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.STRUCTURE.getOrThrow(NamespacedKey.fromString(reader.readString()));
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
    public AttributeArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as an attribute.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed attribute
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public Attribute parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.ATTRIBUTE.getOrThrow(NamespacedKey.fromString(reader.readString()));
    }
  }

  /**
   * Argument that resolves a {@link StructureType} from the structure type registry.
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
    public StructureTypeArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as a structure type.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed structure type
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public StructureType parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.STRUCTURE_TYPE.getOrThrow(NamespacedKey.fromString(reader.readString()));
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
    public EntityTypeArgument getInstance() {
      return this;
    }

    /**
     * Parses the argument value as an entity type.
     *
     * @param source the command source
     * @param reader the string reader
     * @param key the argument key
     * @return the parsed entity type
     * @throws CommandSyntaxException if parsing fails
     */
    @Override
    public EntityType parseArgument(final CommandSource<CommandSender> source,
        final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(EntityType.valueOf(reader.readString()));
    }
  }

  /**
   * Maps registry entries to their string keys.
   *
   * @param registry the registry to map
   * @param <T> the keyed registry entry type
   * @return an array of registry key strings
   */
  public final static <T extends Keyed> String[] _mapRegistry(final Registry<T> registry) {
    return registry.stream().map((final T b) -> {
      final NamespacedKey key = b.getKey();
      return key == null ? null : key.toString();
    }).filter((s) -> s != null).toArray(String[]::new);
  }
}
