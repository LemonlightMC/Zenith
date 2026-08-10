package com.lemonlightmc.zenith.utils;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.additive.Reflect;
import com.lemonlightmc.zenith.version.MCVersion;

public class BukkitReflect {

  public static Class<?> getMinecraftClass(final String className) {
    return Reflect.getClass("net.minecraft." + className);
  }

  public static Class<?> getNMSClass(final String clazz) {
    return Reflect.getClass("net.minecraft.server." + MCVersion.current() + clazz);
  }

  public static Class<?> getBukkitClass(final String className) {
    return Reflect.getClass("org.bukkit.craftbukkit." + MCVersion.current() + className);
  }

  public static Object getConnection(final Player player) {
    if (player == null) {
      return null;
    }
    final Object entityPlayer = Reflect.invokeMethod(player.getClass(), player, "getHandle", Player.class, player);
    return Reflect.getObject(entityPlayer.getClass(), entityPlayer, "playerConnection");
  }

  public static void sendPacket(final Object packet, final Player player) {
    if (packet == null || player == null) {
      return;
    }

    final Object conn = getConnection(player);
    Reflect.invokeMethod(conn.getClass(), conn, "sendPacket", getMinecraftClass("Packet"), packet);
  }
}
