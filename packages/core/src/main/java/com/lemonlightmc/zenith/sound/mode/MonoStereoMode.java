package com.lemonlightmc.zenith.sound.mode;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.sound.Instrument;
import com.lemonlightmc.zenith.sound.Note;
import com.lemonlightmc.zenith.sound.Sound;

public class MonoStereoMode extends ChannelMode {
  private double distance = 2;

  @Override
  public void play(final Player player, final Location location,
      final Note note, final double volume, final double pitch) {
    final String instrumentFileName = Instrument.getCustomInstrumentFileName(note);

    if (instrumentFileName != null) {
      ChannelMode.playSound(player, location, instrumentFileName, note.getSource(), volume, pitch,
          distance);
      ChannelMode.playSound(player, location, instrumentFileName, note.getSource(), volume, pitch,
          -distance);
    } else {
      final org.bukkit.Sound bukkitSound = Instrument.getInstrument(note);
      ChannelMode.playSound(player, location, bukkitSound, note.getSource(), volume, pitch, distance);
      ChannelMode.playSound(player, location, bukkitSound, note.getSource(), volume, pitch, -distance);
    }
  }

  @Override
  public void play(final Player player, final Location location, final Sound sound, final double volume,
      final double pitch) {
    ChannelMode.playSound(player, location, sound.getBukkitSound(), sound.getSource(), volume, pitch, distance);
    ChannelMode.playSound(player, location, sound.getBukkitSound(), sound.getSource(), volume, pitch, -distance);
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(final double distance) {
    this.distance = distance;
  }
}
