package com.lemonlightmc.zenith.utils;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lemonlightmc.zenith.files.FileUtils;

public class UUIDUtils {

  private static final String API_URL = "https://playerdb.co/api/player/minecraft/%s";
  private static HashMap<String, String> cache = new HashMap<>();

  public static boolean isUUID(final String uuid) {
    if (uuid == null || uuid.length() == 0) {
      return false;
    }
    try {
      UUID.fromString(uuid);
      return true;
    } catch (final IllegalArgumentException e) {
      return isUndashedUUID(uuid);
    }
  }

  public static boolean isUUID(final UUID uuid) {
    return uuid != null;
  }

  public static boolean isDashedUUID(final String uuid) {
    if (uuid == null || uuid.length() == 0) {
      return false;
    }
    try {
      UUID.fromString(uuid);
      return true;
    } catch (final IllegalArgumentException e) {
      return false;
    }
  }

  public static boolean isUndashedUUID(final String uuid) {
    if (uuid == null || uuid.length() == 0) {
      return false;
    }
    try {
      return parseUndashedUUID(uuid) != null;
    } catch (final IllegalArgumentException e) {
      return false;
    }
  }

  public static UUID parseUUID(final UUID uuid) {
    return uuid;
  }

  public static UUID parseUUID(final String uuid) {
    if (uuid == null || uuid.length() == 0) {
      return null;
    }
    try {
      return UUID.fromString(uuid);
    } catch (final IllegalArgumentException e) {
      return parseUndashedUUID(uuid);
    }
  }

  public static UUID parseDashedUUID(final String uuid) {
    if (uuid == null || uuid.length() == 0) {
      return null;
    }
    try {
      return UUID.fromString(uuid);
    } catch (final IllegalArgumentException e) {
      return null;
    }
  }

  public static UUID parseUndashedUUID(final String uuid) {
    if (uuid.length() != 32) {
      return null;
    }
    try {
      return new UUID(
          Long.parseUnsignedLong(uuid.substring(0, 16), 16),
          Long.parseUnsignedLong(uuid.substring(16), 16));
    } catch (final NumberFormatException e) {
      return null;
    }
  }

  public static UUID toUUID(final UUID uuid) {
    return uuid;
  }

  public static UUID toUUID(final String name) {
    final UUID uuid = parseUUID(name);
    if (uuid != null) {
      return uuid;
    }
    if (name == null || name.length() == 0) {
      return null;
    }
    return fetchUUID(name);
  }

  public static String toName(final String name) {
    if (name == null || name.length() == 0) {
      return null;
    }
    final UUID uuid = parseUUID(name);
    if (uuid != null) {
      return fetchName(uuid);
    }
    return name;
  }

  public static String toName(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return fetchName(uuid);
  }

  public static String toUndashedUUID(final String uuid) {
    return toUndashedUUID(toUUID(uuid));
  }

  public static String toUndashedUUID(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return (digits(uuid.getMostSignificantBits() >> 32, 8) +
        digits(uuid.getMostSignificantBits() >> 16, 4) +
        digits(uuid.getMostSignificantBits(), 4) +
        digits(uuid.getLeastSignificantBits() >> 48, 4) +
        digits(uuid.getLeastSignificantBits(), 12));
  }

  public static String fetchName(final String name) {
    if (name == null || name.length() == 0) {
      return null;
    }
    return fetch(name.toLowerCase(), "username");
  }

  public static String fetchName(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return fetchName(uuid.toString());
  }

  public static UUID fetchUUID(final String name) {
    if (name == null || name.length() == 0) {
      return null;
    }
    return toUUID(fetch(name.toLowerCase(), "id"));
  }

  public static String fetch(final String value, final String key) {
    final String cachedValue = cache.get(key + value);
    if (cachedValue != null) {
      return cachedValue;
    }
    try {
      final HttpURLConnection connection = (HttpURLConnection) (new URI(API_URL + value)).toURL().openConnection();

      connection.setUseCaches(false);
      connection.setDefaultUseCaches(false);
      connection.addRequestProperty("User-Agent", "Mozilla/5.0");
      connection.addRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
      connection.addRequestProperty("Pragma", "no-cache");
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);

      // These connection parameters need to be set or the API won't accept the
      // connection.

      try (BufferedReader bufferedReader = FileUtils.createReader(connection.getInputStream())) {
        final StringBuilder response = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null) {
          response.append(line);
        }

        final JsonElement parsed = JsonParser.parseString(response.toString());

        if (parsed == null || !parsed.isJsonObject()) {
          return null;
        }

        final JsonObject data = parsed.getAsJsonObject();

        final String result = data.get("data")
            .getAsJsonObject()
            .get("player")
            .getAsJsonObject()
            .get(key) // Grab the UUID.
            .getAsString();
        cache.put(key + value, result);
        return result;
      }
    } catch (final Exception ignored) {
      // Ignoring exception since this is usually caused by non-existent usernames.
    }

    return null;
  }

  private static String digits(final long val, final int digits) {
    final long hi = 1L << (digits * 4);
    return Long.toHexString(hi | (val & (hi - 1))).substring(1);
  }
}