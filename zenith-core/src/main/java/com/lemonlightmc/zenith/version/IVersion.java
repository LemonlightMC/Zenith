package com.lemonlightmc.zenith.version;

import com.lemonlightmc.zenith.interfaces.Cloneable;
import com.lemonlightmc.zenith.version.Version.VersionModifier;

public interface IVersion extends Comparable<IVersion>, Cloneable<IVersion> {
  public int getMajor();

  public int getMinor();

  public int getPatch();

  public int getBuild();

  public VersionModifier getModifier();

  public boolean isMajor(IVersion version);

  public boolean isMinor(IVersion version);

  public boolean isPatch(IVersion version);

  public boolean isBuild(IVersion version);

  public boolean isSame(IVersion version);

  public boolean isDifferent(IVersion version);

  public boolean isNewerThan(IVersion version);

  public boolean isOlderThan(IVersion version);

  public boolean isAtLeast(IVersion version);

  public boolean isBetween(IVersion minVersion, IVersion maxVersion);

  public boolean isOutside(IVersion minVersion, IVersion maxVersion);

  public String formatted();

  public String formatted(boolean includeEmpty);

  @Override
  public String toString();

  @Override
  public boolean equals(Object o);

  @Override
  public int hashCode();

}
