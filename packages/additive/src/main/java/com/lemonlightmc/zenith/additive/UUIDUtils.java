package com.lemonlightmc.zenith.additive;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lemonlightmc.zenith.additive.files.FileUtils;

/**
 * Utility methods for parsing, formatting, and resolving player UUIDs and names.
 */
public class UUIDUtils {

  private static final String API_URL = "https://playerdb.co/api/player/minecraft/%s";
  private static HashMap<String, String> cache = new HashMap<>();

  /**
   * Checks whether the given string can be parsed as a UUID.
   *
   * @param uuid string to check
   * @return {@code true} if the string represents a valid UUID; otherwise {@code false}
   */
  public static boolean isUUID(final String uuid) {
    return parseUUID(uuid) != null;
  }

  /**
   * Checks whether the given string can be parsed as a dashed UUID.
   *
   * @param uuid string to check
   * @return {@code true} if the string represents a valid dashed UUID; otherwise {@code false}
   */
  public static boolean isDashedUUID(final String uuid) {
    return parseDashedUUID(uuid) != null;
  }

  /**
   * Checks whether the given string can be parsed as an undashed UUID.
   *
   * @param uuid string to check
   * @return {@code true} if the string represents a valid undashed UUID; otherwise {@code false}
   */
  public static boolean isUndashedUUID(final String uuid) {
    return parseUndashedUUID(uuid) != null;
  }

  /**
   * Parses a UUID string in dashed or undashed form.
   *
   * @param uuid uuid string to parse
   * @return the parsed {@link UUID}, or {@code null} if parsing fails
   */
  public static UUID parseUUID(String uuid) {
    if (uuid == null || uuid.length() == 0) {
      return null;
    }
    uuid = uuid.trim();
    try {
      return UUID.fromString(uuid);
    } catch (final IllegalArgumentException e) {
      return parseUndashedUUID(uuid);
    }
  }

  /**
   * Parses a UUID string in dashed form.
   *
   * @param uuid dashed uuid string to parse
   * @return the parsed {@link UUID}, or {@code null} if parsing fails
   */
  public static UUID parseDashedUUID(String uuid) {
    if (uuid == null || uuid.length() == 0) {
      return null;
    }
    uuid = uuid.trim();
    try {
      return UUID.fromString(uuid);
    } catch (final IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Parses a UUID string in undashed form.
   *
   * @param uuid undashed uuid string to parse
   * @return the parsed {@link UUID}, or {@code null} if parsing fails
   */
  public static UUID parseUndashedUUID(String uuid) {
    if (uuid == null) {
      return null;
    }
    uuid = uuid.trim();
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

  /**
   * Converts the given UUID to its undashed hexadecimal string representation.
   *
   * @param uuid uuid to convert
   * @return the undashed uuid string, or {@code null} if the input is {@code null}
   */
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

  /**
   * Resolves the given value to a UUID, parsing it directly or fetching it remotely when needed.
   *
   * @param name uuid string or player name to resolve
   * @return the resolved {@link UUID}, or {@code null} if resolution fails
   */
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

  /**
   * Resolves the given value to a player name, returning it directly or looking it up remotely when needed.
   *
   * @param name uuid string or player name to resolve
   * @return the resolved player name, or {@code null} if resolution fails
   */
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

  /**
   * Resolves the player name for the given UUID.
   *
   * @param uuid uuid to resolve
   * @return the resolved player name, or {@code null} if resolution fails
   */
  public static String toName(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return fetchName(uuid);
  }

  /**
   * Resolves the given value to an undashed UUID string.
   *
   * @param uuid uuid string or player name to resolve
   * @return the resolved undashed uuid string, or {@code null} if resolution fails
   */
  public static String toUndashedUUID(final String uuid) {
    return toUndashedUUID(toUUID(uuid));
  }

  /**
   * Fetches the canonical player name for the given name value from the remote lookup service.
   *
   * @param name player name or identifier to look up
   * @return the fetched player name, or {@code null} if lookup fails
   */
  public static String fetchName(final String name) {
    if (name == null || name.length() == 0) {
      return null;
    }
    return fetch(name.toLowerCase(), "username");
  }

  /**
   * Fetches the player name for the given UUID from the remote lookup service.
   *
   * @param uuid uuid to look up
   * @return the fetched player name, or {@code null} if lookup fails
   */
  public static String fetchName(final UUID uuid) {
    if (uuid == null) {
      return null;
    }
    return fetchName(uuid.toString());
  }

  /**
   * Fetches the UUID for the given player name from the remote lookup service.
   *
   * @param name player name to look up
   * @return the fetched {@link UUID}, or {@code null} if lookup fails
   */
  public static UUID fetchUUID(final String name) {
    if (name == null || name.length() == 0) {
      return null;
    }
    return parseUUID(fetch(name.toLowerCase(), "id"));
  }

  /**
   * Fetches a player data field from the remote lookup service for the given value.
   *
   * @param value lookup value
   * @param key json field key to retrieve from the player payload
   * @return the fetched value, or {@code null} if lookup fails
   */
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

  /**
   * Formats the given value as a lowercase hexadecimal string with the specified number of digits.
   *
   * @param val value to format
   * @param digits number of hexadecimal digits to produce
   * @return the formatted hexadecimal string
   */
  private static String digits(final long val, final int digits) {
    final long hi = 1L << (digits * 4);
    return Long.toHexString(hi | (val & (hi - 1))).substring(1);
  }
}
