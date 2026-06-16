package com.lemonlightmc.zenith.sound;

import org.bukkit.SoundCategory;

import com.lemonlightmc.zenith.apis.SoundAPI;
import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.utils.MathUtils;;

public abstract class Playable implements Cloneable {

  protected SoundCategory source = SoundAPI.DEFAULT_SOURCE;
  protected double volume = SoundAPI.DEFAULT_VOLUME;
  protected double pitch = SoundAPI.DEFAULT_PITCH;
  protected long seed = SoundAPI.DEFAULT_SEED;
  protected int panning = SoundAPI.DEFAULT_PANNING;
  protected boolean isStereo = false;

  public Playable() {
  }

  public Playable(final SoundCategory source) {
    setSource(source);
  }

  public Playable(final SoundCategory source, final double volume) {
    setSource(source);
    setVolume(volume);

  }

  public Playable(final SoundCategory source, final double volume, final double pitch) {
    setSource(source);
    setVolume(volume);
    setPitch(pitch);
  }

  public Playable(final SoundCategory source, final double volume, final double pitch, final int panning) {
    setSource(source);
    setVolume(volume);
    setPitch(pitch);
    setPanning(panning);
  }

  public Playable(final Playable playable) {
    this(playable.source, playable.volume, playable.pitch, playable.panning, playable.seed);
  }

  public Playable(final SoundCategory source, final double volume, final double pitch, final int panning,
      final long seed) {
    setSource(source);
    setVolume(volume);
    setPitch(pitch);
    setPanning(panning);
    setSeed(seed);
  }

  public SoundCategory getSource() {
    return source;
  }

  public void setSource(final SoundCategory source) {
    if (source == null) {
      throw new IllegalArgumentException("Sound Source cant be empty");
    }
    this.source = source;
  }

  public double getVolume() {
    return volume;
  }

  public void setVolume(final double volume) {
    this.volume = MathUtils.normalizeRangeOrThrow(volume, SoundAPI.MINIMUM_VOLUME, SoundAPI.MAXIMUM_VOLUME,
        "Volume");
  }

  public double getPitch() {
    return pitch;
  }

  public void setPitch(final double pitch) {
    this.pitch = MathUtils.normalizeRangeOrThrow(pitch, SoundAPI.MINIMUM_PITCH, SoundAPI.MAXIMUM_PITCH,
        "Pitch");
  }

  public int getPanning() {
    return panning;
  }

  public void setPanning(final int panning) {
    this.panning = MathUtils.normalizeRangeOrThrow(panning, SoundAPI.MINIMUM_PANNING, SoundAPI.MAXIMUM_PANNING,
        "Panning");
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(final long seed) {
    this.seed = MathUtils.normalizeRangeOrThrow(seed, SoundAPI.MINIMUM_SEED, SoundAPI.MAXIMUM_SEED, "Seed");
  }

  public void setStereo(final boolean isStereo) {
    this.isStereo = isStereo;
  }

  public boolean isStereo() {
    return isStereo;
  }

  @Override
  public int hashCode() {
    int result = 31 + ((source == null) ? 0 : source.hashCode());
    result = 31 * result + Double.hashCode(volume);
    result = 31 * result + Double.hashCode(pitch);
    result = 31 * result + panning;
    result = 31 * result + Long.hashCode(seed);
    return 31 * result + (isStereo ? 1231 : 1237);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    final Playable other = (Playable) obj;
    return source == other.source && panning == other.panning && seed == other.seed && isStereo == other.isStereo
        && Double.doubleToLongBits(volume) == Double.doubleToLongBits(other.volume)
        && Double.doubleToLongBits(pitch) == Double.doubleToLongBits(other.pitch);
  }

  @Override
  public String toString() {
    return "Playable [source=" + source + ", volume=" + volume + ", pitch=" + pitch + ", seed=" + seed + ", panning="
        + panning + ", isStereo=" + isStereo + "]";
  }

  public static abstract class PlayableBuilder implements Builder<Playable> {
    protected SoundCategory source = SoundAPI.DEFAULT_SOURCE;
    protected double volume = SoundAPI.DEFAULT_VOLUME;
    protected double pitch = SoundAPI.DEFAULT_PITCH;
    protected int panning = SoundAPI.DEFAULT_PANNING;
    protected long seed = SoundAPI.DEFAULT_SEED;
    protected boolean isStereo = false;

    PlayableBuilder() {
    }

    PlayableBuilder(final Playable existing) {
      this.volume = existing.volume;
      this.pitch = existing.pitch;
      this.panning = existing.panning;
      this.source = existing.source;
      this.seed = existing.seed;
    }

    public PlayableBuilder source(final SoundCategory source) {
      this.source = source;
      return this;
    }

    public SoundCategory source() {
      return source;
    }

    public PlayableBuilder volume(final double volume) {
      this.volume = volume;
      return this;
    }

    public double volume() {
      return volume;
    }

    public PlayableBuilder pitch(final double pitch) {
      this.pitch = pitch;
      return this;
    }

    public double pitch() {
      return pitch;
    }

    public PlayableBuilder panning(final int panning) {
      this.panning = panning;
      return this;
    }

    public int panning() {
      return panning;
    }

    public PlayableBuilder seed(final long seed) {
      this.seed = seed;
      return this;
    }

    public long seed() {
      return seed;
    }

    public PlayableBuilder stereo(final boolean isStereo) {
      this.isStereo = isStereo;
      return this;
    }

    public boolean stereo() {
      return isStereo;
    }

    @Override
    public int hashCode() {
      int result = 31 + ((source == null) ? 0 : source.hashCode());
      result = 31 * result + Double.hashCode(volume);
      result = 31 * result + Double.hashCode(pitch);
      result = 31 * result + panning;
      result = 31 * result + Long.hashCode(seed);
      return 31 * result + (isStereo ? 1231 : 1237);
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      final PlayableBuilder other = (PlayableBuilder) obj;
      return source == other.source && panning == other.panning && seed == other.seed && isStereo == other.isStereo
          && Double.doubleToLongBits(volume) != Double.doubleToLongBits(other.volume)
          && Double.doubleToLongBits(pitch) != Double.doubleToLongBits(other.pitch);
    }

    @Override
    public String toString() {
      return "PlayableBuilder [source=" + source + ", volume=" + volume + ", pitch=" + pitch + ", panning=" + panning
          + ", seed=" + seed + ", isStereo=" + isStereo + "]";
    }

    @Override
    public abstract Playable build();
  }
}
