package com.lemonlightmc.zenith.sound;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.SoundCategory;
import org.bukkit.plugin.Plugin;

import com.lemonlightmc.zenith.apis.SoundAPI;
import com.lemonlightmc.zenith.base.ZenithPlugin;

public class Sound extends Playable {
  protected NamespacedKey key;
  protected org.bukkit.Sound bukkitSound;

  public Sound(final NamespacedKey key) {
    this(key, SoundAPI.DEFAULT_SOURCE, SoundAPI.DEFAULT_VOLUME, SoundAPI.DEFAULT_PITCH);
  }

  public Sound(final NamespacedKey key, final SoundCategory source) {
    this(key, source, SoundAPI.DEFAULT_VOLUME, SoundAPI.DEFAULT_PITCH);
  }

  public Sound(final NamespacedKey key, final SoundCategory source, final float volume) {
    this(key, source, volume, SoundAPI.DEFAULT_PITCH, SoundAPI.DEFAULT_PANNING, SoundAPI.DEFAULT_SEED);
  }

  public Sound(final NamespacedKey key, final SoundCategory source, final float volume, final float pitch) {
    this(key, source, volume, pitch, SoundAPI.DEFAULT_PANNING, SoundAPI.DEFAULT_SEED);
  }

  public Sound(final NamespacedKey key, final SoundCategory source, final float volume, final float pitch,
      final int panning) {
    this(key, source, volume, pitch, panning, SoundAPI.DEFAULT_SEED);
  }

  public Sound(final NamespacedKey key, final SoundCategory source, final float volume, final float pitch,
      final int panning,
      final long seed) {
    super(source, volume, pitch, panning, seed);
    this.key = key;
    if (key != null) {
      this.bukkitSound = Registry.SOUNDS.get(key);
    }
  }

  public static Sound minecraft(final String name) {
    return new Sound(NamespacedKey.minecraft(name));
  }

  public static Sound from(final NamespacedKey key) {
    return new Sound(key);
  }

  public static Sound from(final NamespacedKey key, final SoundCategory source) {
    return new Sound(key, source);
  }

  public static Sound from(final NamespacedKey key, final SoundCategory source, final float volume) {
    return new Sound(key, source, volume);
  }

  public static Sound from(final NamespacedKey key, final SoundCategory source, final float volume,
      final float pitch) {
    return new Sound(key, source, volume, pitch);
  }

  public static Sound from(final NamespacedKey key, final SoundCategory source, final float volume, final float pitch,
      final int panning) {
    return new Sound(key, source, volume, pitch, panning);
  }

  public static Sound from(final NamespacedKey key, final SoundCategory source, final float volume, final float pitch,
      final int panning, final long seed) {
    return new Sound(key, source, volume, pitch, panning, seed);
  }

  public static Sound from(final String name) {
    return new Sound(NamespacedKey.fromString(name, ZenithPlugin.getInstance()));
  }

  public static Sound from(final String name, final Plugin plugin) {
    return new Sound(NamespacedKey.fromString(name, plugin));
  }

  public static Sound from(final String name, final SoundCategory source) {
    return new Sound(NamespacedKey.minecraft(name), source);
  }

  public static Sound from(final String name, final SoundCategory source, final float volume) {
    return new Sound(NamespacedKey.minecraft(name), source, volume);
  }

  public static Sound from(final String name, final SoundCategory source, final float volume, final float pitch) {
    return new Sound(NamespacedKey.fromString(name), source, volume, pitch);
  }

  public static Sound from(final String name, final SoundCategory source, final float volume, final float pitch,
      final int panning) {
    return new Sound(NamespacedKey.minecraft(name), source, volume, pitch, panning);
  }

  public static Sound from(final String name, final SoundCategory source, final float volume, final float pitch,
      final int panning, final long seed) {
    return new Sound(NamespacedKey.minecraft(name), source, volume, pitch, panning, seed);
  }

  public void setKey(final NamespacedKey key) {
    if (key == null) {
      throw new IllegalArgumentException("Key for Sound cant be null");
    }
    this.key = key;
    this.bukkitSound = Registry.SOUNDS.get(key);
  }

  public NamespacedKey getKey() {
    return key;
  }

  public void setBukkitSound(final org.bukkit.Sound sound) {
    if (sound == null) {
      throw new IllegalArgumentException("Bukkit Sound cant be null");
    }
    this.bukkitSound = sound;
  }

  public org.bukkit.Sound getBukkitSound() {
    return bukkitSound;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + ((key == null) ? 0 : key.hashCode());
    result = 31 * result + ((bukkitSound == null) ? 0 : bukkitSound.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj) || getClass() != obj.getClass())
      return false;
    final Sound other = (Sound) obj;
    if (key == null) {
      if (other.key != null)
        return false;
    } else if (!key.equals(other.key))
      return false;
    if (bukkitSound == null) {
      if (other.bukkitSound != null)
        return false;
    } else if (!bukkitSound.equals(other.bukkitSound))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Sound [key=" + key + ", bukkitSound=" + bukkitSound + "]";
  }

}
