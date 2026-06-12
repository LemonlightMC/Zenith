package com.lemonlightmc.zenith.dependency.sources;

import com.lemonlightmc.zenith.dependency.DependencySource;

public enum SourceType {
  HANGAR(HangarSource.class),
  MODRINTH(ModrinthSource.class),
  SPIGET(SpigetSource.class),
  GITHUB(GitHubSource.class),
  MAVEN(MavenSource.class),
  JITPACK(JitpackSource.class),
  CODEMC(CodeMCSource.class),
  CURSEFORGE(CurseForgeSource.class),
  URL(UrlSource.class);

  private final Class<?> cls;

  private SourceType(final Class<?> cls) {
    this.cls = cls;
  }

  @SuppressWarnings("unchecked")
  public <T extends DependencySource> T create() {
    try {
      return (T) cls.getDeclaredConstructor().newInstance();
    } catch (final Exception e) {
      return null;
    }
  }
}
