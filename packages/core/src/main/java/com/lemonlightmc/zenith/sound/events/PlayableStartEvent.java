package com.lemonlightmc.zenith.sound.events;

import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.events.BaseEventCancellable;
import com.lemonlightmc.zenith.sound.player.SoundPlayer;

public class PlayableStartEvent extends BaseEventCancellable {
  private static final HandlerList handlers = new HandlerList();
  private final SoundPlayer songPlayer;

  public PlayableStartEvent(final SoundPlayer songPlayer) {
    this.songPlayer = songPlayer;
  }

  public SoundPlayer getSongPlayer() {
    return songPlayer;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
