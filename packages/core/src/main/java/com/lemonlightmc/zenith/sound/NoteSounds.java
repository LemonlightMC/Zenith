package com.lemonlightmc.zenith.sound;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;

import com.lemonlightmc.zenith.apis.SoundAPI;

public class NoteSounds extends Sound {

  public static NoteSounds NOTE_PIANO = new NoteSounds(0, "NOTE_PIANO", "minecraft:block.note_block.harp", "NOTE_PIANO",
      "BLOCK_NOTE_HARP", "BLOCK_NOTE_BLOCK_HARP");
  public static NoteSounds NOTE_BASS = new NoteSounds(1, "NOTE_BASS", "minecraft:block.note_block.bass", "NOTE_BASS",
      "BLOCK_NOTE_BASS", "BLOCK_NOTE_BLOCK_BASS");

  public static NoteSounds NOTE_BASS_DRUM = new NoteSounds(2, "NOTE_BASS_DRUM", "minecraft:block.note_block.basedrum",
      "NOTE_BASS_DRUM",
      "BLOCK_NOTE_BASEDRUM",
      "BLOCK_NOTE_BLOCK_BASEDRUM");
  public static NoteSounds NOTE_SNARE_DRUM = new NoteSounds(3, "NOTE_SNARE_DRUM", "minecraft:block.note_block.snare",
      "NOTE_SNARE_DRUM",
      "BLOCK_NOTE_SNARE",
      "BLOCK_NOTE_BLOCK_SNARE");
  public static NoteSounds NOTE_STICKS = new NoteSounds(4, "NOTE_STICKS", "minecraft:block.note_block.hat",
      "NOTE_STICKS",
      "BLOCK_NOTE_HAT", "BLOCK_NOTE_BLOCK_HAT");
  public static NoteSounds NOTE_BASS_GUITAR = new NoteSounds(5, "NOTE_BASS_GUITAR", "minecraft:block.note_block.guitar",
      "NOTE_BASS_GUITAR",
      "BLOCK_NOTE_GUITAR",
      "BLOCK_NOTE_BLOCK_GUITAR");
  public static NoteSounds NOTE_FLUTE = new NoteSounds(6, "NOTE_FLUTE", "minecraft:block.note_block.flute",
      "NOTE_FLUTE",
      "BLOCK_NOTE_FLUTE", "BLOCK_NOTE_BLOCK_FLUTE");
  public static NoteSounds NOTE_BELL = new NoteSounds(7, "NOTE_BELL", "minecraft:block.note_block.bell", "NOTE_BELL",
      "BLOCK_NOTE_BELL", "BLOCK_NOTE_BLOCK_BELL");
  public static NoteSounds NOTE_CHIME = new NoteSounds(8, "NOTE_CHIME", "minecraft:block.note_block.chime",
      "NOTE_CHIME",
      "BLOCK_NOTE_CHIME", "BLOCK_NOTE_BLOCK_CHIME");
  public static NoteSounds NOTE_XYLOPHONE = new NoteSounds(9, "NOTE_XYLOPHONE", "minecraft:block.note_block.xylophone",
      "NOTE_XYLOPHONE",
      "BLOCK_NOTE_XYLOPHONE",
      "BLOCK_NOTE_BLOCK_XYLOPHONE");
  public static NoteSounds NOTE_PLING = new NoteSounds(15, "NOTE_PLING", "minecraft:block.note_block.pling",
      "NOTE_PLING",
      "BLOCK_NOTE_PLING", "BLOCK_NOTE_BLOCK_PLING");
  public static NoteSounds NOTE_IRON_XYLOPHONE = new NoteSounds(10, "NOTE_IRON_XYLOPHONE",
      "minecraft:block.note_block.iron_xylophone",
      "BLOCK_NOTE_BLOCK_IRON_XYLOPHONE");
  public static NoteSounds NOTE_COW_BELL = new NoteSounds(11, "NOTE_COW_BELL", "minecraft:block.note_block.cow_bell",
      "BLOCK_NOTE_BLOCK_COW_BELL");
  public static NoteSounds NOTE_DIDGERIDOO = new NoteSounds(12, "NOTE_DIDGERIDOO",
      "minecraft:block.note_block.didgeridoo",
      "BLOCK_NOTE_BLOCK_DIDGERIDOO");
  public static NoteSounds NOTE_BIT = new NoteSounds(13, "NOTE_BIT", "minecraft:block.note_block.bit",
      "BLOCK_NOTE_BLOCK_BIT");
  public static NoteSounds NOTE_BANJO = new NoteSounds(14, "NOTE_BANJO", "minecraft:block.note_block.banjo",
      "BLOCK_NOTE_BLOCK_BANJO");

  private static final Map<String, NoteSounds> soundsByName = new HashMap<>();
  private static final NoteSounds[] soundsByIndex = new NoteSounds[16];

  private final int instrumentIndex;
  private final String resourcePackName;
  private final String[] versionDependentNames;
  private final String name;

  NoteSounds(final int instrumentIndex, final String name, final String resourcePackName,
      final String... versionDependentNames) {
    super(null);
    this.name = name.toUpperCase();
    this.instrumentIndex = instrumentIndex;
    this.resourcePackName = resourcePackName;
    this.versionDependentNames = versionDependentNames;
    cacheSound();
  }

  private void cacheSound() {
    NoteSounds.soundsByIndex[instrumentIndex] = this;
    for (final String vName : versionDependentNames) {
      NoteSounds.soundsByName.put(vName, this);
      if (bukkitSound == null) {
        // also gets the bukkit Sound
        setKey(NamespacedKey.minecraft(vName));
      }
    }
  }

  public static NoteSounds getByName(final String name) {
    return soundsByName.get(name.toUpperCase());
  }

  public static NoteSounds getByName(final String name, final NoteSounds def) {
    final NoteSounds sound = soundsByName.get(name.toUpperCase());
    return sound == null ? def : sound;
  }

  public static org.bukkit.Sound getSoundbyName(final String name) {
    final NoteSounds sound = soundsByName.get(name.toUpperCase());
    if (sound != null)
      return sound.getBukkitSound();

    return SoundAPI.getFromRegistry(name);
  }

  public static org.bukkit.Sound getSoundbyName(final String name, final NoteSounds def) {
    final NoteSounds sound = soundsByName.get(name.toUpperCase());
    if (sound != null)
      return sound.getBukkitSound();

    return SoundAPI.getFromRegistry(name, def);
  }

  public static NoteSounds getByIndex(final int index) {
    if (index < 0 || index >= soundsByIndex.length)
      return null;
    return soundsByIndex[index];
  }

  public static NoteSounds getByIndex(final int index, final NoteSounds def) {
    if (index < 0 || index >= soundsByIndex.length)
      return def;
    return soundsByIndex[index] == null ? def : soundsByIndex[index];
  }

  public static org.bukkit.Sound getSoundByIndex(final int index) {
    if (index < 0 || index >= soundsByIndex.length)
      return null;
    final NoteSounds sound = soundsByIndex[index];
    return sound == null ? null : sound.getBukkitSound();
  }

  public static org.bukkit.Sound getSoundByIndex(final int index, final NoteSounds def) {
    if (index < 0 || index >= soundsByIndex.length)
      return def.getBukkitSound();
    final NoteSounds sound = soundsByIndex[index];
    return sound == null ? def.getBukkitSound() : sound.getBukkitSound();
  }

  @Override
  public NamespacedKey getKey() {
    return key;
  }

  public int getInstrumentIndex() {
    return instrumentIndex;
  }

  public String getName() {
    return name;
  }

  public String getResourcePackName() {
    return resourcePackName;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + instrumentIndex;
    result = 31 * result + ((resourcePackName == null) ? 0 : resourcePackName.hashCode());
    result = 31 * result + Arrays.hashCode(versionDependentNames);
    result = 31 * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj) || getClass() != obj.getClass())
      return false;
    final NoteSounds other = (NoteSounds) obj;
    if (resourcePackName == null) {
      if (other.resourcePackName != null)
        return false;
    } else if (!resourcePackName.equals(other.resourcePackName))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return instrumentIndex == other.instrumentIndex
        && Arrays.equals(versionDependentNames, other.versionDependentNames);
  }

  @Override
  public String toString() {
    return "NoteSounds [key=" + key + ", bukkitSound=" + bukkitSound + ", source=" + source + ", volume=" + volume
        + ", pitch=" + pitch + ", seed=" + seed + ", panning=" + panning + ", instrumentIndex=" + instrumentIndex
        + ", resourcePackName=" + resourcePackName + ", versionDependentNames=" + Arrays.toString(versionDependentNames)
        + ", name=" + name + "]";
  }

}