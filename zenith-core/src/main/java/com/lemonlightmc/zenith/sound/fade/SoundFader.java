package com.lemonlightmc.zenith.sound.fade;

import com.lemonlightmc.zenith.apis.SoundAPI;
import com.lemonlightmc.zenith.sound.fade.SoundFading.SoundFadingType;

public class SoundFader {

  private SoundFading fade;
  private double elapsedTime = 0;
  private double initialVolume = SoundAPI.MINIMUM_VOLUME;
  private double targetVolume = SoundAPI.MAXIMUM_VOLUME;
  private double tempo;

  public SoundFader(final SoundFadingType type, final int fadeDuration, final double songTempo) {
    this.fade = type.create(fadeDuration / songTempo);
    this.tempo = songTempo;
  }

  public SoundFader(final SoundFadingType type, final int fadeDuration) {
    this.fade = type.create(fadeDuration / SoundAPI.COMMON_TEMPO);
    this.tempo = SoundAPI.COMMON_TEMPO;
  }

  public SoundFader(final SoundFadingType type, final double songTempo) {
    this.fade = type.create(0);
    this.tempo = songTempo;
  }

  public SoundFader(final SoundFadingType type) {
    this.fade = type.create(0);
    this.tempo = SoundAPI.COMMON_TEMPO;
  }

  public SoundFader(final SoundFading fade, final double songTempo) {
    this.fade = fade;
    this.tempo = songTempo;
  }

  public SoundFader(final SoundFading fade) {
    this.fade = fade;
    this.tempo = SoundAPI.COMMON_TEMPO;
  }

  public SoundFader(final SoundFader fader, final SoundFading fade) {
    this.fade = fade;
    this.elapsedTime = fader.elapsedTime;
    this.initialVolume = fader.initialVolume;
    this.targetVolume = fader.targetVolume;
    this.tempo = fader.tempo;
  }

  public SoundFader(final SoundFader fader) {
    this.fade = fader.fade;
    this.elapsedTime = fader.elapsedTime;
    this.initialVolume = fader.initialVolume;
    this.targetVolume = fader.targetVolume;
    this.tempo = fader.tempo;
  }

  public SoundFading getFade() {
    return fade;
  }

  public boolean isDone() {
    return elapsedTime >= fade.getDurationInSeconds();
  }

  public double calculateFade() {
    return calculateFade(0);
  }

  public double calculateFade(double deltaTime) {
    if (isDone())
      return -1;
    if (deltaTime < 0)
      throw new IllegalArgumentException("Delta time can not be negative");

    elapsedTime += deltaTime;
    return fade.calculateFadeVolume(initialVolume, targetVolume, elapsedTime);
  }

  public int getProgress() {
    return (int) Math.round(elapsedTime / tempo);
  }

  public SoundFadingType getType() {
    return fade.getType();
  }

  public void setType(final SoundFadingType type) {
    fade = type.create(fade.getDurationInSeconds());
  }

  public double getDuration() {
    return Math.round(fade.getDurationInSeconds() / tempo);
  }

  public void setDuration(final int fadeDuration) {
    fade.setDuration(fadeDuration);
  }

  public double getElapsedTime() {
    return elapsedTime;
  }

  public void setElapsedTime(final int fadeDone) {
    this.elapsedTime = fadeDone * tempo;
  }

  public void setElapsedTime(final double elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public double getInitialVolume() {
    return initialVolume;
  }

  public void setInitialVolume(final double initialVolume) {
    this.initialVolume = initialVolume;
  }

  public double getTargetVolume() {
    return targetVolume;
  }

  public void setTargetVolume(final double targetVolume) {
    this.targetVolume = targetVolume;
  }

  public void setTempo(final double tempo) {
    this.tempo = tempo;
  }

  public double getTempo() {
    return this.tempo;
  }

}