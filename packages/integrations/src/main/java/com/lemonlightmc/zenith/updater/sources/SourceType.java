package com.lemonlightmc.zenith.updater.sources;

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

  private Class<?> cls;

  private SourceType(Class<?> cls) {
    this.cls = cls;
  }

  public Object create() {
    try {
      return cls.getDeclaredConstructor().newInstance();
    } catch (Exception e) {
      return null;
    }
  }
}
