package com.lemonlightmc.zenith.sound.mode;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.apis.SoundAPI;
import com.lemonlightmc.zenith.sound.Instrument;
import com.lemonlightmc.zenith.sound.Note;
import com.lemonlightmc.zenith.sound.Sound;

public class StereoMode extends ChannelMode {

  private double maxDistance = 2;
  private ChannelMode fallbackChannelMode;

  @Override
  public void play(final Player player, final Location location, final Note note, final double volume,
      final double pitch) {
    if (!note.isStereo()) {
      getFallbackChannelMode().play(player, location, note, volume, pitch);
      return;
    }

    double distance;
    if (note.getPanning() == SoundAPI.DEFAULT_PANNING) {
      distance = (note.getPanning() / 100f) * maxDistance;
    } else {
      distance = ((note.getPanning() + note.getPanning()) / 200f) * maxDistance;
    }
    final String instrumentFileName = Instrument.getCustomInstrumentFileName(note);
    if (instrumentFileName != null) {
      ChannelMode.playSound(player, location, instrumentFileName, note.getSource(), volume, pitch,
          distance);
    } else {
      ChannelMode.playSound(player, location,
          Instrument.getInstrument(note), note.getSource(), volume, pitch, distance);
    }
  }

  @Override
  public void play(final Player player, final Location location, final Sound sound, final double volume,
      final double pitch) {
    if (!sound.isStereo()) {
      getFallbackChannelMode().play(player, location, sound, volume, pitch);
      return;
    }

    double distance;
    if (sound.getPanning() == SoundAPI.DEFAULT_PANNING) {
      distance = (sound.getPanning() / 100f) * maxDistance;
    } else {
      distance = ((sound.getPanning() + sound.getPanning()) / 200f) * maxDistance;
    }
    ChannelMode.playSound(player, location, sound.getBukkitSound(), sound.getSource(), volume, pitch, distance);
  }

  public double getMaxDistance() {
    return maxDistance;
  }

  public void setMaxDistance(final double maxDistance) {
    this.maxDistance = maxDistance;
  }

  public ChannelMode getFallbackChannelMode() {
    if (fallbackChannelMode == null) {
      fallbackChannelMode = new MonoStereoMode();
    }
    return fallbackChannelMode;
  }

  public void setFallbackChannelMode(final ChannelMode fallbackChannelMode) {
    if (fallbackChannelMode instanceof StereoMode)
      throw new IllegalArgumentException("Fallback ChannelMode can't be instance of StereoMode!");

    this.fallbackChannelMode = fallbackChannelMode;
  }

}
