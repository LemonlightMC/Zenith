package com.lemonlightmc.zenith.sound.fade;

import com.lemonlightmc.zenith.apis.SoundAPI;
import com.lemonlightmc.zenith.messages.Logger;

public abstract class SoundFading {

  protected double duration;
  protected SoundFadingType type;

  public SoundFading(final SoundFadingType type, final double duration) {
    this.type = type;
    this.duration = duration;
  }

  public double getDurationInSeconds() {
    return duration;
  }

  public void setDuration(final double seconds) {
    this.duration = seconds;
  }

  public SoundFadingType getType() {
    return type;
  }

  public SoundFading toType(final SoundFadingType type) {
    return type.getCls().cast(type.create(duration));
  }

  public abstract double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time);

  protected double progress(final double time) {
    if (duration <= 0)
      return 1.0;
    if (time <= 0)
      return 0.0;
    if (time >= duration)
      return 1.0;
    return time / duration;
  }

  protected double clampVolume(final double value) {
    double v = Math.round(value);
    if (v < SoundAPI.MINIMUM_VOLUME)
      v = SoundAPI.MINIMUM_VOLUME;
    if (v > SoundAPI.MAXIMUM_VOLUME)
      v = SoundAPI.MAXIMUM_VOLUME;
    return v;
  }

  public static enum SoundFadingType {
    NON(NoSoundFade.class),
    LINEAR(LinearSoundFade.class),
    QUADRATIC_INOUT(QuadraticInOutSoundFade.class),
    QUADRATIC_OUT(QuadraticOutSoundFade.class),
    EXPONENTIAL_IN(ExponentialInSoundFade.class),
    EXPONENTIAL_OUT(ExponentialOutSoundFade.class),
    SINE(SineInOutSoundFade.class),
    LOGRAITHMIC(LogarithmicSoundFade.class);

    private final Class<? extends SoundFading> cls;

    private SoundFadingType(final Class<? extends SoundFading> cls) {
      this.cls = cls;
    }

    public boolean isNoFade() {
      return this.equals(NON);
    }

    public Class<? extends SoundFading> getCls() {
      return cls;
    }

    public SoundFading create(final double duration) {
      try {
        return (SoundFading) cls.getDeclaredConstructor().newInstance(duration);
      } catch (final Exception e) {
        Logger.warn("Failed to create SoundFading Instance");
        e.printStackTrace();
        return null;
      }
    }
  }

  public static class LinearSoundFade extends SoundFading {

    public LinearSoundFade(final double duration) {
      super(SoundFadingType.LINEAR, duration);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      if (time >= duration)
        return targetVolume;

      final double p = progress(time);
      final double value = initialVolume + (targetVolume - initialVolume) * p;
      return clampVolume(value);
    }
  }

  public static class QuadraticOutSoundFade extends SoundFading {
    public QuadraticOutSoundFade(final double duration) {
      super(SoundFadingType.QUADRATIC_OUT, duration);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      if (time >= duration)
        return targetVolume;

      final double p = progress(time);
      final double f = 1 - (1 - p) * (1 - p);
      final double value = initialVolume + (targetVolume - initialVolume) * f;
      return clampVolume(value);
    }
  }

  public static class QuadraticInOutSoundFade extends SoundFading {
    public QuadraticInOutSoundFade(final double duration) {
      super(SoundFadingType.QUADRATIC_INOUT, duration);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      if (time >= duration)
        return targetVolume;

      final double p = progress(time);
      final double f = (p < 0.5) ? (2 * p * p) : (1 - Math.pow(-2 * p + 2, 2) / 2.0);
      final double value = initialVolume + (targetVolume - initialVolume) * f;
      return clampVolume(value);
    }
  }

  public static class ExponentialInSoundFade extends SoundFading {
    public ExponentialInSoundFade(final double duration) {
      super(SoundFadingType.EXPONENTIAL_IN, duration);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      if (time >= duration)
        return targetVolume;
      final double p = progress(time);
      final double f = (p <= 0.0) ? 0.0 : Math.pow(2, 10 * (p - 1));
      final double value = initialVolume + (targetVolume - initialVolume) * f;
      return clampVolume(value);
    }
  }

  public static class ExponentialOutSoundFade extends SoundFading {
    public ExponentialOutSoundFade(final double duration) {
      super(SoundFadingType.EXPONENTIAL_OUT, duration);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      if (time >= duration)
        return targetVolume;
      final double p = progress(time);
      final double f = (p >= 1.0) ? 1.0 : 1 - Math.pow(2, -10 * p);
      final double value = initialVolume + (targetVolume - initialVolume) * f;
      return clampVolume(value);
    }
  }

  public static class SineInOutSoundFade extends SoundFading {
    public SineInOutSoundFade(final double duration) {
      super(SoundFadingType.SINE, duration);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      if (time >= duration)
        return targetVolume;
      final double p = progress(time);
      final double f = -(Math.cos(Math.PI * p) - 1) / 2.0;
      final double value = initialVolume + (targetVolume - initialVolume) * f;
      return clampVolume(value);
    }
  }

  public static class LogarithmicSoundFade extends SoundFading {
    public LogarithmicSoundFade(final double duration) {
      super(SoundFadingType.LOGRAITHMIC, duration);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      if (time >= duration)
        return targetVolume;
      final double p = progress(time);
      // map progress [0,1] -> [0,1] with a gentle logarithmic curve
      final double f = Math.log1p(9.0 * p) / Math.log(10.0);
      final double value = initialVolume + (targetVolume - initialVolume) * f;
      return clampVolume(value);
    }
  }

  public static class NoSoundFade extends SoundFading {

    public NoSoundFade() {
      super(SoundFadingType.NON, 0);
    }

    @Override
    public double calculateFadeVolume(final double initialVolume, final double targetVolume, final double time) {
      return clampVolume(targetVolume);
    }
  }
}
