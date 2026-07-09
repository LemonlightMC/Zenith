package com.lemonlightmc.zenith.utils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

public class WorldUtils {
  public static World[] getWorlds() {
    return Bukkit.getWorlds().toArray(World[]::new);
  }

  public static World getWorld(final UUID uid) {
    if (uid == null) {
      return null;
    }
    return Bukkit.getWorld(uid);
  }

  public static World getWorld(final String str) {
    if (str == null || str.length() == 0) {
      return null;
    }
    return Bukkit.getWorld(str);
  }

  public static String[] getWorldNames() {
    final List<World> worlds = Bukkit.getWorlds();
    final int len = worlds.size();
    final String[] names = new String[len];
    if (len == 0) {
      return names;
    }
    for (int i = 0; i < len; i++) {
      names[i] = worlds.get(i).getName();
    }
    return names;
  }

  public static World getMainWorld() {
    return Bukkit.getWorlds().get(0);
  }

  public static WorldCreator createWorldCreator(final String worldName, final World.Environment env,
      final WorldType type,
      final boolean generateStructures) {
    if (worldName == null || worldName.isEmpty()) {
      throw new IllegalArgumentException("World name cannot be null or empty");
    }
    return new WorldCreator(worldName).environment(env == null ? Environment.NORMAL : env)
        .generateStructures(generateStructures)
        .type(type == null ? WorldType.NORMAL : type);
  }

  public static WorldCreator createWorldCreator(final String worldName, final World.Environment env,
      final WorldType type) {
    return createWorldCreator(worldName, env, type, true);
  }

  public static World createWorld(final String worldName) {
    return createWorldCreator(worldName, Environment.NORMAL, WorldType.NORMAL, false).keepSpawnInMemory(false)
        .createWorld();
  }

  public static World createFlatWorld(final String worldName) {
    return createWorldCreator(worldName, Environment.NORMAL, WorldType.FLAT, false).keepSpawnInMemory(false)
        .createWorld();
  }

  public static World createVoidWorld(final String worldName) {
    final World world = createWorldCreator(worldName, Environment.NORMAL, WorldType.FLAT, false)
        .keepSpawnInMemory(false)
        .generator(new VoidGenerator())
        .createWorld();
    if (world == null) {
      throw new IllegalStateException("Failed to create world: " + worldName);
    }
    world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
    world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
    world.setGameRule(GameRule.DO_WEATHER_CYCLE, true);
    world.setTime(0L);
    world.setSpawnLocation(0, 121, 0);
    return world;
  }

  public static void deleteWorld(final JavaPlugin plugin, final World world) {
    plugin.getServer().unloadWorld(world, false);
    deleteWorldFolder(world);
  }

  private static void deleteWorldFolder(final World world) {
    if (world == null) {
      return;
    }
    try {
      final File worldFolder = new File(world.getWorldFolder().getPath());
      if (worldFolder.exists()) {
        Files.deleteIfExists(worldFolder.toPath());
      }
    } catch (final Exception ex) {
      ex.printStackTrace();
    }
  }

  static public String getStringLocation(final Location location) {
    if (location == null) {
      return "";
    }
    return location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":"
        + location.getBlockZ();
  }

  public static Location getLocation(final String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    final String[] parts = str.split(":");
    if (parts.length == 3) {
      try {
        final int x = NumberConversions.parseInt(parts[0]);
        final int y = NumberConversions.parseInt(parts[1]);
        final int z = NumberConversions.parseInt(parts[2]);
        return new Location(getMainWorld(), x, y, z);
      } catch (Exception e) {
        return null;
      }
    }
    if (parts.length < 4) {
      return null;
    }
    try {
      final World w = Bukkit.getServer().getWorld(parts[0]);
      final int x = NumberConversions.parseInt(parts[1]);
      final int y = NumberConversions.parseInt(parts[2]);
      final int z = NumberConversions.parseInt(parts[3]);
      return new Location(w, x, y, z);
    } catch (Exception e) {
      return null;
    }
  }

  public static List<Location> getSphere(final Location center, final int radius) {
    final List<Location> sphereLocations = new ArrayList<Location>();
    for (double i = 0; i <= Math.PI; i += Math.PI / 10) {
      final double circleRadius = Math.sin(i);
      final double y = center.getY() + Math.cos(i) * radius;
      for (double a = 0; a < Math.PI * 2; a += Math.PI / 10) {
        final double x = center.getX() + Math.cos(a) * circleRadius * radius;
        final double z = center.getZ() + Math.sin(a) * circleRadius * radius;
        sphereLocations.add(new Location(center.getWorld(), center.getX() + x, center.getY() + y, center.getZ() + z));
      }
    }
    return sphereLocations;
  }

  public static List<Block> getBox(final Location location, final int radius) {
    final List<Block> blocks = new ArrayList<>();
    for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
      for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
        for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
          blocks.add(location.getWorld().getBlockAt(x, y, z));
        }
      }
    }
    return blocks;
  }

  public static List<Location> getCircle(final Location center, final double radius, final int amount) {
    final double increment = (2 * Math.PI) / amount;
    final List<Location> locations = new ArrayList<Location>();
    for (int i = 0; i < amount; i++) {
      final double angle = i * increment;
      final double x = center.getX() + (radius * Math.cos(angle));
      final double z = center.getZ() + (radius * Math.sin(angle));
      locations.add(new Location(center.getWorld(), x, center.getY(), z));
    }
    return locations;
  }

  public static Block getBlockBehindPlayer(final Player player, final Number distance) {
    final Vector inverseDirectionVec = player.getLocation().getDirection().normalize()
        .multiply(distance.doubleValue() * (-1d));
    return player.getLocation().add(inverseDirectionVec).getBlock();
  }

  public static Location getLocationBehindPlayer(final Player player, final Number distance) {
    final Vector inverseDirectionVec = player.getLocation().getDirection().multiply(distance.doubleValue() * (-1d));
    return player.getLocation().add(inverseDirectionVec);
  }

  public static Block getRelativeByFace(final Block block, final BlockFace blockFace, final int offsetX,
      final int offsetY, final int offsetZ) {
    if (blockFace == BlockFace.NORTH) {
      return block.getRelative(offsetX, offsetY, -offsetZ);
    } else if (blockFace == BlockFace.SOUTH) {
      return block.getRelative(-offsetX, offsetY, offsetZ);
    } else if (blockFace == BlockFace.EAST) {
      return block.getRelative(offsetZ, offsetY, offsetX);
    } else if (blockFace == BlockFace.WEST) {
      return block.getRelative(-offsetZ, offsetY, -offsetX);
    } else if (blockFace == BlockFace.UP) {
      return block.getRelative(offsetX, offsetZ, offsetY);
    } else if (blockFace == BlockFace.DOWN) {
      return block.getRelative(offsetX, -offsetZ, -offsetY);
    }
    return block.getRelative(offsetX, offsetY, offsetZ);
  }

  public static Vector getDirection(final Location startLocation, final Location targetLocation) {
    return targetLocation.toVector().subtract(startLocation.toVector()).normalize();
  }

  public static Location centerLocation(Location location) {
    location = location.clone();
    location.setX((double) location.getBlockX() + 0.5D);
    location.setZ((double) location.getBlockZ() + 0.5D);
    return location;
  }

  public static Location decenterLocation(Location location) {
    location = location.clone();
    location.setX((double) location.getBlockX());
    location.setZ((double) location.getBlockZ());
    return location;
  }

  public static boolean isSameBlock(final Location loc1, final Location loc2) {
    return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY()
        && loc1.getBlockZ() == loc2.getBlockZ();
  }

  public static boolean isSimilarBlock(final Location loc1, final Location loc2) {
    return Math.abs(loc1.getBlockX() - loc2.getBlockX()) < 0.0001
        && Math.abs(loc1.getBlockY() - loc2.getBlockY()) < 0.0001
        && Math.abs(loc1.getBlockZ() - loc2.getBlockZ()) < 0.0001;
  }

  public static class VoidGenerator extends ChunkGenerator {

    private VoidGenerator() {
    }

    @Override
    public boolean isParallelCapable() {
      return true;
    }

    @Override
    public List<BlockPopulator> getDefaultPopulators(final World world) {
      return List.of();
    }

    @Override
    public void generateNoise(final WorldInfo worldInfo, final Random random, final int chunkX, final int chunkZ,
        final ChunkData chunkData) {
      // No need to generate noise, we want an empty world
    }

    @Override
    public void generateSurface(final WorldInfo worldInfo, final Random random, final int chunkX, final int chunkZ,
        final ChunkData chunkData) {
      // No need to generate surface, we want an empty world
    }

    @Override
    public void generateBedrock(final WorldInfo worldInfo, final Random random, final int chunkX, final int chunkZ,
        final ChunkData chunkData) {
      // No need to generate bedrock, we want an empty world
    }

    @Override
    public void generateCaves(final WorldInfo worldInfo, final Random random, final int chunkX, final int chunkZ,
        final ChunkData chunkData) {
      // No need to generate caves, we want an empty world
    }

    @Override

    public BiomeProvider getDefaultBiomeProvider(final WorldInfo worldInfo) {
      return new VoidBiomeProvider();
    }

    @Override
    public boolean canSpawn(final World world, final int x, final int z) {
      return true;
    }

    @Override
    public Location getFixedSpawnLocation(final World world, final Random random) {
      return new Location(world, 0, 0, 0);
    }
  }

  public static class VoidBiomeProvider extends BiomeProvider {

    public VoidBiomeProvider() {
    }

    @Override
    public Biome getBiome(final WorldInfo worldInfo, final int x, final int y, final int z) {
      return Biome.THE_VOID;
    }

    @Override
    public List<Biome> getBiomes(final WorldInfo worldInfo) {
      return List.of(Biome.THE_VOID);
    }
  }
}
