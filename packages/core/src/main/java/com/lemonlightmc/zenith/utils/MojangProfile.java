package com.lemonlightmc.zenith.utils;

import java.util.UUID;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lemonlightmc.zenith.interfaces.Cloneable;

public class MojangProfile implements Cloneable<MojangProfile> {
  private final UUID uuid;
  private final String username;
  private final String value;
  private final String signature;
  private final long timestamp;

  public MojangProfile(final UUID uuid, final String username, final String value, final String signature) {
    if (uuid == null || username == null || username.isBlank()) {
      throw new IllegalArgumentException("UUID and username cannot be empty");
    }
    this.uuid = uuid;
    this.username = username;
    this.value = value;
    this.signature = signature;
    this.timestamp = System.currentTimeMillis();
  }

  public MojangProfile(final String uuidStr, final String username, final String value, final String signature) {
    this.uuid = MojangProfile.formatUUID(uuidStr);
    if (uuid == null || username == null || username.isBlank()) {
      throw new IllegalArgumentException("UUID and username cannot be empty");
    }
    this.username = username;
    this.value = value;
    this.signature = signature;
    this.timestamp = System.currentTimeMillis();
  }

  public MojangProfile(final String uuid, final String username) {
    this(uuid, username, null, null);
  }

  public UUID getUniqueId() {
    return this.uuid;
  }

  public String getName() {
    return this.username;
  }

  public String getValue() {
    return this.value;
  }

  public String getSignature() {
    return this.signature;
  }

  public long getTimestamp() {
    return timestamp;
  }

  @Override
  public String toString() {
    return "MojangProfile [uuid=" + uuid + ", username=" + username + ", value=" + value + ", signature=" + signature
        + ", timestamp=" + timestamp + "]";
  }

  @Override
  public int hashCode() {
    int result = 31 + uuid.hashCode();
    result = 31 * result + username.hashCode();
    result = 31 * result + ((value == null) ? 0 : value.hashCode());
    result = 31 * result + ((signature == null) ? 0 : signature.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final MojangProfile other = (MojangProfile) obj;
    if (value == null && other.value != null || signature == null && other.signature != null) {
      return false;
    }
    return uuid.equals(other.uuid) && username.equals(other.username) && signature.equals(other.signature)
        && value.equals(other.value);
  }

  @Override
  public MojangProfile clone() {
    return new MojangProfile(uuid.toString(), username, value, signature);
  }

  private static UUID formatUUID(final String rawUUID) {
    if (rawUUID == null || rawUUID.isEmpty()) {
      return null;
    }
    if (rawUUID.contains("-")) {
      return UUID.fromString(rawUUID);
    }
    final StringBuilder formattedUUID = new StringBuilder(rawUUID);
    formattedUUID.insert(8, '-');
    formattedUUID.insert(13, '-');
    formattedUUID.insert(18, '-');
    formattedUUID.insert(23, '-');
    return UUID.fromString(formattedUUID.toString());
  }

  private static MojangProfile resolveJson(final JsonObject object) {
    if (object == null) {
      return null;
    }
    final String id = JsonUtil.getString(object, "uuid");
    final String name = JsonUtil.getString(object, "name");
    String value = null;
    String signature = null;

    final JsonArray properties = JsonUtil.getJsonArray(object, "properties");
    if (properties == null || properties.isEmpty()) {
      return new MojangProfile(id, name, value, signature);
    }

    for (final JsonElement property : properties) {
      final JsonObject propertyObj = JsonUtil.toJsonObject(property);
      final String propertyName = JsonUtil.getString(propertyObj, "name");
      if (!"textures".equals(propertyName)) {
        continue;
      }
      value = JsonUtil.getString(propertyObj, "value");
      signature = JsonUtil.getString(propertyObj, "signature");

      if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
        value = value.substring(1, value.length() - 1);
      }
      if (signature != null && signature.startsWith("\"") && signature.endsWith("\"")) {
        signature = signature.substring(1, signature.length() - 1);
      }
    }
    return new MojangProfile(id, name, value, signature);
  }

  public static MojangProfile getMojangProfile(final String username) {
    final String apiEndpoint = "https://api.mojang.com/users/profiles/minecraft/" + username + "?unsigned=false";
    return resolveJson(JsonUtil.requestJson(apiEndpoint));
  }

  public static MojangProfile getMojangProfileFromUUID(final String uuid) {
    final String apiEndpoint = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.replace("-", "")
        + "?unsigned=false";
    return resolveJson(JsonUtil.requestJson(apiEndpoint));
  }
}
