package com.lemonlightmc.zenith.sound.player;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import com.lemonlightmc.zenith.scheduler.GlobalScheduler;
import com.lemonlightmc.zenith.scheduler.ScheduledTask;
import com.lemonlightmc.zenith.sound.Playlist;
import com.lemonlightmc.zenith.sound.RepeatMode;
import com.lemonlightmc.zenith.sound.Song;
import com.lemonlightmc.zenith.sound.events.PlayableEndEvent;
import com.lemonlightmc.zenith.sound.events.PlayableLoopEvent;
import com.lemonlightmc.zenith.sound.events.PlayableNextEvent;
import com.lemonlightmc.zenith.sound.events.PlayableStoppedEvent;
import com.lemonlightmc.zenith.sound.fade.SoundFader;
import com.lemonlightmc.zenith.sound.fade.SoundFading;
import com.lemonlightmc.zenith.sound.fade.SoundFading.SoundFadingType;

public abstract class SoundPlayer {
  protected Song song;

  protected Song playingSong;
  protected Playlist playlist;
  protected int actualSong = 0;

  protected short tick = 0;
  private double elapsedTimeInSeconds = 0;

  protected boolean enabled = true;
  protected boolean playing = false;
  protected boolean fading = false;

  protected Map<UUID, Boolean> playerList = new ConcurrentHashMap<>();

  protected double volume = 1d;
  protected SoundFader fadeIn;
  protected SoundFader fadeOut;
  protected SoundFader fadeTemp = null;

  protected SoundCategory soundCategory = SoundCategory.MASTER;
  protected RepeatMode repeat = RepeatMode.NO;

  private final Lock lock = new ReentrantLock();
  private ScheduledTask backgroundTask = null;

  public SoundPlayer(final Song song) {
    this(new Playlist(song));
  }

  public SoundPlayer(final Song song, final SoundCategory soundCategory) {
    this(new Playlist(song), soundCategory);
  }

  public SoundPlayer(final Playlist playlist, final SoundCategory soundCategory) {
    this(playlist, soundCategory, false);
  }

  public SoundPlayer(final Playlist playlist) {
    this.playlist = playlist;

    this.playingSong = playlist.getPlayable(actualSong);
    this.song = playingSong;

    fadeIn = new SoundFader(SoundFadingType.NON);
    fadeOut = new SoundFader(SoundFadingType.NON);
  }

  public SoundPlayer(final Playlist playlist, final SoundCategory soundCategory, final boolean random) {
    this(playlist);
    setCategory(soundCategory);
  }

  public SoundFading getFadeInEffect() {
    return fadeIn.getFade();
  }

  public SoundFading getFadeOutEffect() {
    return fadeOut.getFade();
  }

  public void setFadeInEffect(final SoundFading fade) {
    this.fadeIn = new SoundFader(fadeIn, fade);
  }

  public void setFadeOutEffect(final SoundFading fade) {
    this.fadeOut = new SoundFader(fadeOut, fade);
  }

  public Set<UUID> getPlayerUUIDs() {
    final Set<UUID> uuids = new HashSet<>(playerList.keySet());
    return Collections.unmodifiableSet(uuids);
  }

  public void addPlayer(final Player player) {
    addPlayer(player.getUniqueId());
  }

  public void addPlayer(final UUID player) {
    lock.lock();
    try {
      if (!playerList.containsKey(player)) {
        playerList.put(player, false);
      }
    } finally {
      lock.unlock();
    }
  }

  public abstract void playTick(Player player, int tick);

  public void destroy() {
    final PlayableStoppedEvent event = new PlayableStoppedEvent(this);
    GlobalScheduler.run(() -> Bukkit.getPluginManager().callEvent(event));
    if (event.isCancelled()) {
      return;
    }
    playing = false;
    setTick((short) 0);
  }

  public boolean isPlaying() {
    return playing;
  }

  public void setPlaying(final boolean playing) {
    if (this.playing == playing)
      return;

    lock.lock();
    try {
      this.playing = playing;

      fadeTemp.setInitialVolume(playing ? 0 : volume);
      fadeTemp.setTargetVolume(playing ? volume : 0);
      if (playing) {
        run();
      } else {
        final PlayableStoppedEvent event = new PlayableStoppedEvent(this);
        GlobalScheduler.run(() -> Bukkit.getPluginManager().callEvent(event));
      }
    } finally {
      lock.unlock();
    }
  }

  public void setFader(final SoundFadingType type) {
    setFader(new SoundFader(type, song.getTempo()));
  }

  public void setFader(final SoundFader fade) {
    if (!fade.getType().isNoFade()) {
      fadeTemp = fade;
      fadeTemp.setInitialVolume(playing ? 0 : volume);
      fadeTemp.setTargetVolume(playing ? volume : 0);
      fading = true;
    } else {
      fading = false;
      fadeTemp = null;
      volume = fadeIn.getTargetVolume();
    }
  }

  public void setFading(final boolean isFading) {
    this.fading = isFading;
  }

  public boolean isFading() {
    return this.fading;
  }

  public short getTick() {
    return this.tick;
  }

  public void setTick(short tick) {
    if (tick < 0)
      tick = 0;
    this.tick = tick;
    elapsedTimeInSeconds = playingSong.getTimeInSecondsAtTick(tick);
  }

  public void removePlayer(final Player player) {
    removePlayer(player.getUniqueId());
  }

  public void removePlayer(final UUID uuid) {
    lock.lock();
    try {
      playerList.remove(uuid);
      if (playerList.isEmpty()) {
        final PlayableEndEvent event = new PlayableEndEvent(this);
        GlobalScheduler.run(() -> Bukkit.getPluginManager().callEvent(event));
        setPlaying(false);
      }
    } finally {
      lock.unlock();
    }
  }

  public double getVolume() {
    return volume;
  }

  public void setVolume(double volume) {
    if (volume > 1d) {
      volume = 1d;
    } else if (volume < 0d) {
      volume = 0d;
    }
    this.volume = volume;

    fadeIn.setTargetVolume(volume);
    fadeOut.setInitialVolume(volume);
    if (fadeTemp != null) {
      if (playing)
        fadeTemp.setTargetVolume(volume);
      else
        fadeTemp.setInitialVolume(volume);
    }
  }

  public Song getPlayingSong() {
    return playingSong;
  }

  public Playlist getPlaylist() {
    return playlist;
  }

  public void setPlaylist(final Playlist playlist) {
    this.playlist = playlist;
  }

  public int getPlayedSongIndex() {
    return actualSong;
  }

  public void playSong(final int index) {
    lock.lock();
    try {
      if (playlist.exist(index)) {
        playingSong = playlist.getPlayable(index);
        song = playingSong.clone();
        actualSong = index;
        resetState();
      }
    } finally {
      lock.unlock();
    }
  }

  public void playNextSong() {
    lock.lock();
    try {
      tick = (short) playingSong.getLength();
      elapsedTimeInSeconds = playingSong.getLengthInSeconds();
    } finally {
      lock.unlock();
    }
  }

  public SoundCategory getCategory() {
    return soundCategory;
  }

  public void setCategory(final SoundCategory soundCategory) {
    this.soundCategory = soundCategory;
  }

  public void setRepeatMode(final RepeatMode repeatMode) {
    this.repeat = repeatMode;
  }

  public RepeatMode getRepeatMode() {
    return repeat;
  }

  private void run() {
    if (backgroundTask != null && Bukkit.getScheduler().isCurrentlyRunning(backgroundTask.getTaskId())
        && Bukkit.getScheduler().isQueued(backgroundTask.getTaskId()))
      return;

    backgroundTask = GlobalScheduler.runAsync(() -> {
      while (playing || fading) {
        final long startTime = System.currentTimeMillis();

        if (!enabled) {
          break;
        }

        lock.lock();
        try {
          if (tick < 0)
            tick = 0;

          if (tick >= playingSong.getLength()) {
            onSongEnded();
            continue;
          }

          final double timeDelta = 1f / playingSong.getTempo(tick);
          if (!processFade(tick != 0 ? timeDelta : 0))
            continue;

          playActualTick();

          tick++;
          elapsedTimeInSeconds += timeDelta;
        } catch (final Exception e) {
          printExceptionDuringPlayback(e);
        } finally {
          lock.unlock();
        }

        final long duration = System.currentTimeMillis() - startTime;
        waitForNextTick(duration);
      }
    });
  }

  private boolean processFade(final double timeDelta) {
    if (fadeTemp != null) {
      if (fadeTemp.isDone()) {
        fadeTemp = null;
        fading = false;
        if (!playing) {
          callEvent(new PlayableStoppedEvent(this));
          volume = fadeIn.getTargetVolume();
          return false;
        }

        if (elapsedTimeInSeconds < fadeIn.getFade().getDurationInSeconds()) {
          fadeIn.setElapsedTime(fadeIn.getFade().getDurationInSeconds());
        }
      } else {
        volume = fadeTemp.calculateFade(timeDelta);
      }
    } else if (elapsedTimeInSeconds < fadeIn.getFade().getDurationInSeconds()) {
      volume = fadeIn.calculateFade(timeDelta);
    } else if (elapsedTimeInSeconds >= playingSong.getLengthInSeconds()
        - fadeOut.getFade().getDurationInSeconds()) {
      volume = fadeOut.calculateFade(timeDelta);
    }
    return true;
  }

  private void onSongEnded() {
    resetState();
    if (repeat == RepeatMode.ONE && callCancellableEvent(new PlayableLoopEvent(this))) {
      return;
    } else {
      if (playlist.hasNext(actualSong)) {
        playSong(actualSong + 1);
        callEvent(new PlayableNextEvent(this));
        return;
      } else {
        playSong(0);
        if (repeat == RepeatMode.ALL && callCancellableEvent(new PlayableLoopEvent(this))) {
          return;
        }
      }
    }
    playing = false;
    callEvent(new PlayableEndEvent(this));
  }

  private void resetState() {
    tick = 0;
    elapsedTimeInSeconds = 0;
    fadeIn.setElapsedTime(0);
    fadeIn.setTempo(song.getTempo());
    fadeOut.setElapsedTime(0);
    fadeOut.setTempo(song.getTempo());
    volume = fadeIn.getTargetVolume();
  }

  private void playActualTick() {
    try {
      for (final UUID uuid : playerList.keySet()) {
        final Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
          // offline...
          continue;
        }
        playTick(player, tick);
      }
    } catch (final Exception e) {
      printExceptionDuringPlayback(e);
    }
  }

  private void waitForNextTick(final long durationOfExecution) {
    final double delayMillis = 1000f / playingSong.getTempo(tick);
    if (durationOfExecution < delayMillis) {
      try {
        Thread.sleep((long) (delayMillis - durationOfExecution));
      } catch (final InterruptedException e) {
        // do nothing
      }
    }
  }

  private <T extends Event & Cancellable> boolean callCancellableEvent(final T event) {
    callEvent(event);
    return !event.isCancelled();
  }

  private void callEvent(final Event event) {
    lock.lock();
    final Condition condition = lock.newCondition();
    try {
      GlobalScheduler.run(() -> {
        lock.lock();
        try {
          Bukkit.getPluginManager().callEvent(event);
          condition.signal();
        } finally {
          lock.unlock();
        }
      });
      condition.await();
    } catch (final InterruptedException ignored) {
    } finally {
      lock.unlock();
    }
  }

  private void printExceptionDuringPlayback(final Exception e) {
    Bukkit.getLogger().severe("An error occurred during the playback of song "
        + (playingSong != null
            ? "(author: '" + playingSong.getMetadata().getAuthor() + "', title: '"
                + playingSong.getMetadata().getTitle() + "')"
            : "'null'"));
    e.printStackTrace();
  }

}