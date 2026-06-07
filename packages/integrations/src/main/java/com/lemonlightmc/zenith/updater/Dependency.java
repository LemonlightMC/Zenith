package com.lemonlightmc.zenith.updater;

import java.util.Objects;

import com.lemonlightmc.zenith.updater.sources.SourceType;
import com.lemonlightmc.zenith.utils.ServerPlatform;
import com.lemonlightmc.zenith.version.MCVersion;
import com.lemonlightmc.zenith.version.Version;
import com.lemonlightmc.zenith.version.VersionConstraint;
import com.lemonlightmc.zenith.version.VersionRange;

/**
 * Represents a plugin dependency from a specific source.
 * <p>
 * Use the static factory methods to create dependencies:
 * <ul>
 * <li>{@link #hangar(String)} - Hangar (PaperMC repository)</li>
 * <li>{@link #modrinth(String)} - Modrinth</li>
 * <li>{@link #spiget(int)} - SpigotMC (via Spiget API)</li>
 * <li>{@link #github(String)} - GitHub Releases</li>
 * <li>{@link #url(String)} - Direct URL</li>
 * </ul>
 */
public final class Dependency {

  private final SourceType sourceType;
  private final String identifier;
  private final String name;
  private final String fileName;
  private final String sha256;
  private final VersionConstraint constraint;
  private final UpdatePolicy updatePolicy;
  private final FailurePolicy failurePolicy;
  private final MCVersion minecraftVersion;
  private final String assetPattern;
  private final String repository;
  private final ServerPlatform platform;

  private Dependency(final Builder<?> builder) {
    this.sourceType = builder.sourceType;
    this.identifier = builder.identifier;
    this.name = builder.name;
    this.fileName = builder.fileName;
    this.sha256 = builder.sha256;
    this.constraint = builder.constraint == null ? VersionConstraint.latest() : builder.constraint;
    this.updatePolicy = builder.updatePolicy;
    this.failurePolicy = builder.failurePolicy;
    this.minecraftVersion = builder.minecraftVersion == null ? MCVersion.current() : builder.minecraftVersion;
    this.assetPattern = builder.assetPattern;
    this.repository = builder.repository;
    this.platform = builder.platform == null ? ServerPlatform.detect() : builder.platform;
  }

  public String repository() {
    return repository;
  }

  // ========== Factory Methods ==========

  /**
   * Create a dependency from Hangar (PaperMC's plugin repository).
   *
   * @param slug the project slug (e.g., "ProtocolLib")
   */
  public static HangarBuilder hangar(final String slug) {
    return new HangarBuilder(slug);
  }

  /**
   * Create a dependency from Modrinth.
   *
   * @param slugOrId the project slug or ID (e.g., "packetevents")
   */
  public static ModrinthBuilder modrinth(final String slugOrId) {
    return new ModrinthBuilder(slugOrId);
  }

  /**
   * Create a dependency from SpigotMC (via Spiget API).
   *
   * @param resourceId the SpigotMC resource ID
   */
  public static SpigetBuilder spiget(final int resourceId) {
    return new SpigetBuilder(resourceId);
  }

  /**
   * Create a dependency from GitHub Releases.
   *
   * @param repo the repository in "owner/repo" format
   */
  public static GitHubBuilder github(final String repo) {
    return new GitHubBuilder(repo);
  }

  /**
   * Create a dependency from a direct URL.
   *
   * @param downloadUrl the direct download URL
   */
  public static UrlBuilder url(final String downloadUrl) {
    return new UrlBuilder(downloadUrl);
  }

  /**
   * Create a dependency from Maven Central.
   *
   * @param groupAndArtifact the group:artifact coordinates (e.g.,
   *                         "org.example:my-lib")
   */
  public static Builder<?> maven(final String groupAndArtifact) {
    return new MavenBuilder(groupAndArtifact);
  }

  /**
   * Create a dependency resolved via JitPack (GitHub owner/repo).
   *
   * @param repo the GitHub repo in "owner/repo" format
   */
  public static Builder<?> jitpack(final String repo) {
    return new JitpackBuilder(repo);
  }

  /**
   * Create a dependency from CodeMC (placeholder).
   *
   * @param identifier project identifier on CodeMC
   */
  public static Builder<?> codemc(final String identifier) {
    return new CodemcBuilder(identifier);
  }

  /**
   * Create a dependency from CurseForge by addon id.
   *
   * @param addonId numeric CurseForge addon id
   */
  public static Builder<?> curseforge(final int addonId) {
    return new CurseForgeBuilder(addonId);
  }

  // ========== Getters ==========

  public SourceType sourceType() {
    return sourceType;
  }

  public String identifier() {
    return identifier;
  }

  /**
   * @return display name, or identifier if not set
   */
  public String name() {
    return name != null ? name : identifier;
  }

  public String fileName() {
    return fileName;
  }

  public String sha256() {
    return sha256;
  }

  public VersionConstraint constraint() {
    return constraint;
  }

  public UpdatePolicy updatePolicy() {
    return updatePolicy;
  }

  public FailurePolicy failurePolicy() {
    return failurePolicy;
  }

  public MCVersion minecraftVersion() {
    return minecraftVersion;
  }

  public Version effectiveMinecraftVersion() {
    if (minecraftVersion() != null) {
      return minecraftVersion();
    }
    return MCVersion.current();
  }

  public String assetPattern() {
    return assetPattern;
  }

  /**
   * @return the platform preference (uses current platform if not set explicitly)
   */
  public ServerPlatform platform() {
    return platform;
  }

  /**
   * @return true if this is a latest version request (no constraint or constraint
   *         is latest)
   */
  public boolean isLatest() {
    return constraint == null || constraint.isLatest();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof Dependency))
      return false;
    final Dependency other = (Dependency) obj;
    return sourceType == other.sourceType && Objects.equals(identifier, other.identifier);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sourceType, identifier);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(sourceType.name().toLowerCase()).append(":").append(identifier);
    if (constraint != null) {
      sb.append("@").append(constraint);
    }
    return sb.toString();
  }

  // ========== Source Types ==========

  // ========== Builder Base ==========

  public static abstract class Builder<T extends Builder<T>> {
    final SourceType sourceType;
    final String identifier;

    String name;

    String fileName;

    String sha256;

    VersionConstraint constraint;
    UpdatePolicy updatePolicy = UpdatePolicy.MINOR;
    FailurePolicy failurePolicy = FailurePolicy.FAIL;

    MCVersion minecraftVersion;

    String assetPattern;
    String repository;
    ServerPlatform platform = null;

    Builder(final SourceType sourceType, final String identifier) {
      if (identifier == null || identifier.isBlank()) {
        throw new IllegalArgumentException("identifier cannot be null or blank");
      }
      if (sourceType == null) {
        throw new IllegalArgumentException("sourceType cannot be null");
      }
      this.sourceType = sourceType;
      this.identifier = identifier;
    }

    @SuppressWarnings("unchecked")
    protected T self() {
      return (T) this;
    }

    /**
     * Set a display name for this dependency.
     */
    public T name(final String name) {
      this.name = name;
      return self();
    }

    /**
     * Set a custom repository URL for sources that support it (e.g., Maven).
     */
    public T repository(final String repository) {
      this.repository = repository;
      return self();
    }

    /**
     * Set a custom filename for the downloaded file.
     */
    public T fileName(final String fileName) {
      this.fileName = fileName;
      return self();
    }

    /**
     * Set an expected SHA-256 checksum for verification.
     */
    public T sha256(final String sha256) {
      this.sha256 = sha256;
      return self();
    }

    /**
     * Request the latest version.
     */
    public T latest() {
      this.constraint = VersionConstraint.latest();
      return self();
    }

    /**
     * Request an exact version.
     */
    public T versionExact(final Version version) {
      this.constraint = VersionConstraint.exact(version);
      return self();
    }

    /**
     * Request at least this version (inclusive).
     */
    public T versionAtLeast(final Version minVersion) {
      this.constraint = VersionConstraint.atleast(minVersion);
      return self();
    }

    /**
     * Request a constrained version (mostly by a range)
     * <p>
     * Examples: {@code ">=5.0.0 <6.0.0"}, {@code ">1.0.0"}
     */
    public T versionRange(final VersionRange range) {
      this.constraint = VersionConstraint.range(range);
      return self();
    }

    public T version(final String range) {
      this.constraint = VersionConstraint.parse(range);
      return self();
    }

    /**
     * Set the update policy for this dependency.
     */
    public T updatePolicy(final UpdatePolicy policy) {
      this.updatePolicy = Objects.requireNonNull(policy);
      return self();
    }

    /**
     * Set what happens if this dependency fails to resolve/download.
     */
    public T onFailure(final FailurePolicy policy) {
      this.failurePolicy = Objects.requireNonNull(policy);
      return self();
    }

    /**
     * Filter versions by Minecraft version compatibility.
     */
    public T minecraftVersion(final MCVersion mcVersion) {
      this.minecraftVersion = mcVersion;
      return self();
    }

    /**
     * Set the target platform for this dependency.
     * <p>
     * Will use the current platform by default, but you can override this
     * if you need a specific platform variant
     *
     * @param platform the target platform
     */
    public T platform(final ServerPlatform platform) {
      if (platform == null) {
        throw new IllegalArgumentException("Dependency Platform cannot be null");
      }
      this.platform = platform;
      return self();
    }

    /**
     * Build the dependency.
     */
    public Dependency build() {
      return new Dependency(this);
    }
  }

  // ========== Source-Specific Builders ==========

  public static final class HangarBuilder extends Builder<HangarBuilder> {
    HangarBuilder(final String slug) {
      super(SourceType.HANGAR, slug);
    }
  }

  public static final class ModrinthBuilder extends Builder<ModrinthBuilder> {
    ModrinthBuilder(final String slugOrId) {
      super(SourceType.MODRINTH, slugOrId);
    }
  }

  public static final class SpigetBuilder extends Builder<SpigetBuilder> {
    SpigetBuilder(final int resourceId) {
      super(SourceType.SPIGET, String.valueOf(resourceId));
    }
  }

  public static final class GitHubBuilder extends Builder<GitHubBuilder> {
    GitHubBuilder(final String repo) {
      super(SourceType.GITHUB, repo);
      if (!repo.contains("/")) {
        throw new IllegalArgumentException("GitHub repo must be in 'owner/repo' format");
      }
    }

    /**
     * Set a pattern to match release assets.
     * <p>
     * Examples: {@code "*.jar"}, {@code "*-spigot-*.jar"},
     * {@code "ProtocolLib.jar"}
     */
    public GitHubBuilder assetPattern(final String pattern) {
      this.assetPattern = pattern;
      return self();
    }
  }

  public static final class UrlBuilder extends Builder<UrlBuilder> {
    UrlBuilder(final String url) {
      super(SourceType.URL, url);
    }
  }

  public static final class MavenBuilder extends Builder<MavenBuilder> {
    MavenBuilder(final String coords) {
      super(SourceType.MAVEN, coords);
    }
  }

  public static final class JitpackBuilder extends Builder<JitpackBuilder> {
    JitpackBuilder(final String repo) {
      super(SourceType.JITPACK, repo);
    }
  }

  public static final class CodemcBuilder extends Builder<CodemcBuilder> {
    CodemcBuilder(final String id) {
      super(SourceType.CODEMC, id);
    }
  }

  public static final class CurseForgeBuilder extends Builder<CurseForgeBuilder> {
    CurseForgeBuilder(final int id) {
      super(SourceType.CURSEFORGE, String.valueOf(id));
    }
  }
}