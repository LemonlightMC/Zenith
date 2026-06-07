package com.lemonlightmc.zenith.integrations;

import java.util.UUID;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.utils.Reflect;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.ServerProtocolVersion;

import me.neznamy.tab.api.TabAPI;

public class ViaVersion {

  private static final boolean hasViaVersion = Reflect.hasClass("com.viaversion.viaversion.api.Via");

  @SuppressWarnings("unchecked")
  private static ViaAPI<Player> getViaApi() {
    return (ViaAPI<Player>) Via.getAPI();
  }

  public static TabAPI get() {
    return TabAPI.getInstance();
  }

  public static TabAPI getOrNull() {
    try {
      return TabAPI.getInstance();
    } catch (final Exception e) {
      return null;
    }
  }

  public static TabAPI getOrThrow() {
    return TabAPI.getInstance();
  }

  public static boolean isLoaded() {
    try {
      return TabAPI.getInstance() != null;
    } catch (final Exception e) {
      return false;
    }
  }

  /**
   * Retrieves the protocol version of a player identified by their unique
   * identifier (UUID).
   * <p>
   * If the ViaVersion plugin is enabled, the player's protocol version is fetched
   * using the ViaVersion API.
   * If the plugin is not enabled, this method returns -1.
   *
   * @param uuid the universally unique identifier (UUID) of the player; must not
   *             be null
   * @return the protocol version of the player as an integer if ViaVersion is
   *         enabled;
   *         otherwise, -1
   * @throws IllegalArgumentException if the provided UUID is null
   */
  public static int getVersion(final UUID uuid) {
    if (!hasViaVersion) {
      return -1;
    }
    if (uuid == null) {
      throw new IllegalArgumentException("UUID cannot be null");
    }
    return Via.getAPI().getPlayerVersion(uuid);
  }

  /**
   * Retrieves the protocol version of the specified player.
   * <br>
   * This method utilizes the ViaVersion API to determine the protocol version of
   * the player
   * if the ViaVersion plugin is enabled. If the plugin is not enabled, it
   * defaults to returning -1.
   *
   * @param player the player object for which the protocol version is to be
   *               retrieved; must not be null
   * @return the protocol version of the player as an integer if ViaVersion is
   *         enabled;
   *         -1 if ViaVersion is not enabled
   * @throws IllegalArgumentException if the player object is null
   */
  public static int getVersion(final Player player) {
    if (!hasViaVersion) {
      return -1;
    }
    if (player == null) {
      throw new IllegalArgumentException("Player object cannot be null");
    }
    return getViaApi().getPlayerVersion(player);
  }

  public static ProtocolVersion getPlayerProtocolVersion(final Player player) {
    if (!hasViaVersion) {
      return null;
    }
    if (player == null) {
      return ProtocolVersion.unknown;
    }
    return getViaApi().getPlayerProtocolVersion(player);
  }

  public static ProtocolVersion getPlayerProtocolVersion(final UUID uuid) {
    if (!hasViaVersion) {
      return null;
    }
    if (uuid == null) {
      return ProtocolVersion.unknown;
    }
    return getViaApi().getPlayerProtocolVersion(uuid);
  }

  public static ServerProtocolVersion getServerProtocolVersion() {
    if (!hasViaVersion) {
      return null;
    }
    return getViaApi().getServerVersion();
  }

  public static UserConnection sendRawPacket(final UUID uuid) {
    if (!hasViaVersion || uuid == null) {
      return null;
    }
    return getViaApi().getConnection(uuid);
  }

  public static UserConnection sendRawPacket(final Player player) {
    if (!hasViaVersion || player == null) {
      return null;
    }
    return getViaApi().getConnection(player.getUniqueId());
  }
}
