package com.lemonlightmc.zenith.math;

public class Rotation {

  private double yaw;
  private double pitch;

  public Rotation(final double yaw, final double pitch) {
    this.yaw = yaw;
    this.pitch = pitch;
  }

  public Rotation(final double yaw) {
    this.yaw = yaw;
    this.pitch = 0;
  }

  public Rotation() {
    this.yaw = 0;
    this.pitch = 0;
  }

  public double getYaw() {
    return this.yaw;
  }

  public void setYaw(final double yaw) {
    this.yaw = yaw;
  }

  public double getPitch() {
    return this.pitch;
  }

  public void setPitch(final double pitch) {
    this.pitch = pitch;
  }

  // between +/-180
  public double getNormalizedYaw() {
    return normalizeYaw(yaw);
  }

  // between +/-90
  public double getNormalizedPitch() {
    return normalizePitch(pitch);
  }

  @Override
  public int hashCode() {
    return 31 * (31 + Double.hashCode(yaw)) + Double.hashCode(pitch);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final Rotation other = (Rotation) obj;
    return Double.doubleToLongBits(yaw) == Double.doubleToLongBits(other.yaw)
        && Double.doubleToLongBits(pitch) == Double.doubleToLongBits(other.pitch);
  }

  @Override
  public String toString() {
    return "Rotation [yaw=" + yaw + ", pitch=" + pitch + "]";
  }

  public static double normalizeYaw(final double yaw) {
    double normalizedYaw = yaw % 360.0F;
    if (normalizedYaw >= 180.0F) {
      normalizedYaw -= 360.0F;
    } else if (normalizedYaw < -180.0F) {
      normalizedYaw += 360.0F;
    }
    return normalizedYaw;
  }

  public static double normalizePitch(final double pitch) {
    return pitch > 90.0F ? 90.0F : Math.max(pitch, -90.0F);
  }
}