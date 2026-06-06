package com.lemonlightmc.zenith.sound.mode;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.sound.Instrument;
import com.lemonlightmc.zenith.sound.Note;
import com.lemonlightmc.zenith.sound.Sound;

public class MonoMode extends ChannelMode {

  @Override
  public void play(final Player player, final Location location, final Note note, final double volume,
      final double pitch) {

    final String instrumentFileName = Instrument.getCustomInstrumentFileName(note);
    if (instrumentFileName != null) {
      player.playSound(location, instrumentFileName, note.getSource(), (float) volume, (float) pitch);
    } else {
      player.playSound(location,
          Instrument.getInstrument(note), note.getSource(), (float) volume, (float) pitch);
    }
  }

  @Override
  public void play(final Player player, final Location location, final Sound sound, final double volume,
      final double pitch) {
    player.playSound(location, sound.getBukkitSound(), sound.getSource(), (float) volume, (float) pitch);
  }
}
