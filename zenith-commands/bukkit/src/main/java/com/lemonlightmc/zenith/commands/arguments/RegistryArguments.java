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

import com.lemonlightmc.zenith.commands.CommandSource;
import com.lemonlightmc.zenith.commands.argumentsbase.Argument;
import com.lemonlightmc.zenith.commands.argumentsbase.ArgumentType;
import com.lemonlightmc.zenith.commands.argumentsbase.ParticleData;
import com.lemonlightmc.zenith.commands.argumentsbase.StringReader;
import com.lemonlightmc.zenith.commands.exceptions.CommandSyntaxException;
import com.lemonlightmc.zenith.math.NumberConversions;

public class RegistryArguments {
  public static class BiomeArgument extends Argument<Biome, BiomeArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.BIOME);

    public BiomeArgument(final String name) {
      super(name, Biome.class, ArgumentType.BIOME);
      withSuggestions(NAMES);
    }

    @Override
    public BiomeArgument getInstance() {
      return this;
    }

    @Override
    public Biome parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Registry.BIOME.getOrThrow(NamespacedKey.fromString(reader.readString()));
    }
  }

  public static class SoundArgument extends Argument<Sound, SoundArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.SOUNDS);

    public SoundArgument(final String name) {
      super(name, Sound.class, ArgumentType.SOUND);
      withSuggestions(NAMES);
    }

    @Override
    public SoundArgument getInstance() {
      return this;
    }

    @Override
    public Sound parseArgument(final CommandSource<CommandSender> source, final StringReader reader, final String key)
        throws CommandSyntaxException {
      return Registry.SOUNDS.getOrThrow(NamespacedKey.fromString(reader.readString()));
    }
  }

  @SuppressWarnings("rawtypes")
  public static class ParticleArgument extends Argument<ParticleData, ParticleArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.PARTICLE_TYPE);

    public ParticleArgument(final String name) {
      super(name, ParticleData.class, ArgumentType.PARTICLE);
      withSuggestions(NAMES);
    }

    @Override
    public ParticleArgument getInstance() {
      return this;
    }

    @Override
    public ParticleData<?> parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return new ParticleData<>(Registry.PARTICLE_TYPE.getOrThrow(NamespacedKey.fromString(reader.readString())), null);
    }
  }

  public static class PotionEffectArgument extends Argument<PotionEffectType, PotionEffectArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.EFFECT);

    public PotionEffectArgument(final String name) {
      super(name, PotionEffectType.class, ArgumentType.POTION_EFFECT);
      withSuggestions(NAMES);
    }

    @Override
    public PotionEffectArgument getInstance() {
      return this;
    }

    @Override
    public PotionEffectType parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.EFFECT.getOrThrow(NamespacedKey.fromString(reader.readString()));
    }
  }

  public static class EnchantmentArgument extends Argument<Enchantment, EnchantmentArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.ENCHANTMENT);

    public EnchantmentArgument(final String name) {
      super(name, Enchantment.class, ArgumentType.ENCHANTMENT);
      withSuggestions(NAMES);
    }

    @Override
    public EnchantmentArgument getInstance() {
      return this;
    }

    @Override
    public Enchantment parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.ENCHANTMENT.getOrThrow(Objects.requireNonNull(NamespacedKey.fromString(reader.readString())));
    }
  }

  public static class AdvancementArgument extends Argument<Advancement, AdvancementArgument, CommandSender> {

    @SuppressWarnings("deprecation")
    public static final String[] NAMES = _mapRegistry(Registry.ADVANCEMENT);

    public AdvancementArgument(final String name) {
      super(name, Advancement.class, ArgumentType.ADVANCEMENT);
      withSuggestions(NAMES);
    }

    @Override
    public AdvancementArgument getInstance() {
      return this;
    }

    @Override
    public Advancement parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects
          .requireNonNull(Bukkit.getAdvancement(Objects.requireNonNull(NamespacedKey.fromString(reader.readString()))));
    }
  }

  public static class ItemStackArgument extends Argument<ItemStack, ItemStackArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.ITEM);

    public ItemStackArgument(final String name) {
      super(name, ItemStack.class, ArgumentType.ITEMSTACK);
      withSuggestions(NAMES);
    }

    @Override
    public ItemStackArgument getInstance() {
      return this;
    }

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

  public static class MaterialArgument extends Argument<Material, MaterialArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.MATERIAL);

    public MaterialArgument(final String name) {
      super(name, Material.class, ArgumentType.MATERIAL);
      withSuggestions(NAMES);
    }

    @Override
    public MaterialArgument getInstance() {
      return this;
    }

    @Override
    public Material parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(Material.getMaterial(reader.readString()));
    }
  }

  public static class StructureArgument extends Argument<Structure, StructureArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.STRUCTURE);

    public StructureArgument(final String name) {
      super(name, Structure.class, ArgumentType.STRUCTURE);
      withSuggestions(NAMES);
    }

    @Override
    public StructureArgument getInstance() {
      return this;
    }

    @Override
    public Structure parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.STRUCTURE.getOrThrow(NamespacedKey.fromString(reader.readString()));
    }
  }

  public static class AttributeArgument extends Argument<Attribute, AttributeArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.ATTRIBUTE);

    public AttributeArgument(final String name) {
      super(name, Attribute.class, ArgumentType.ATTRIBUTE);
      withSuggestions(NAMES);
    }

    @Override
    public AttributeArgument getInstance() {
      return this;
    }

    @Override
    public Attribute parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.ATTRIBUTE.getOrThrow(NamespacedKey.fromString(reader.readString()));
    }
  }

  public static class StructureTypeArgument extends Argument<StructureType, StructureTypeArgument, CommandSender> {

    public static final String[] NAMES = _mapRegistry(Registry.STRUCTURE_TYPE);

    public StructureTypeArgument(final String name) {
      super(name, StructureType.class, ArgumentType.STRUCTURE_TYPE);
      withSuggestions(NAMES);
    }

    @Override
    public StructureTypeArgument getInstance() {
      return this;
    }

    @Override
    public StructureType parseArgument(final CommandSource<CommandSender> source, final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Registry.STRUCTURE_TYPE.getOrThrow(NamespacedKey.fromString(reader.readString()));
    }
  }

  public static class EntityTypeArgument extends Argument<EntityType, EntityTypeArgument, CommandSender> {
    public static final String[] NAMES = _mapRegistry(Registry.ENTITY_TYPE);

    public EntityTypeArgument(final String name) {
      super(name, EntityType.class, ArgumentType.ENTITY_TYPE);
      withSuggestions(NAMES);
    }

    @Override
    public EntityTypeArgument getInstance() {
      return this;
    }

    @Override
    public EntityType parseArgument(final CommandSource<CommandSender> source,
        final StringReader reader,
        final String key)
        throws CommandSyntaxException {
      return Objects.requireNonNull(EntityType.valueOf(reader.readString()));
    }
  }

  public final static <T extends Keyed> String[] _mapRegistry(final Registry<T> registry) {
    return registry.stream().map((final T b) -> {
      final NamespacedKey key = b.getKey();
      return key == null ? null : key.toString();
    }).filter((s) -> s != null).toArray(String[]::new);
  }
}
