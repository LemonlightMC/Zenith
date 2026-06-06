package com.lemonlightmc.zenith.sound.events;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.lemonlightmc.zenith.events.BaseEvent;
import com.lemonlightmc.zenith.sound.player.SoundPlayer;

public class PlayerPlayerRangeEvent extends BaseEvent {

  private static final HandlerList handlers = new HandlerList();
  private final SoundPlayer song;
  private final Player player;
  private final boolean state;

  public PlayerPlayerRangeEvent(SoundPlayer song, Player player, boolean state) {
    this.song = song;
    this.player = player;
    this.state = state;
  }

  public SoundPlayer getSongPlayer() {
    return song;
  }

  public Player getPlayer() {
    return player;
  }

  public boolean isInRange() {
    return state;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }

  @Override
  public HandlerList getHandlers() {
    return handlers;
  }
}