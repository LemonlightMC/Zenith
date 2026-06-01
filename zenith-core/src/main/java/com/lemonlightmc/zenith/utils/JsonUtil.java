package com.lemonlightmc.zenith.utils;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lemonlightmc.zenith.files.FileUtils;
import com.lemonlightmc.zenith.messages.Logger;

public class JsonUtil {
  private static volatile Gson gson;
  private static final ReentrantLock LOCK = new ReentrantLock();
  private static GsonBuilder GSON_BUILDER = new GsonBuilder();

  public static void customizeGsonBuilder(Consumer<GsonBuilder> consumer) {
    LOCK.lock();
    try {
      consumer.accept(GSON_BUILDER);
      gson = GSON_BUILDER.create();
    } finally {
      LOCK.unlock();
    }
  }

  public static void finalizeGson() {
    LOCK.lock();
    try {
      gson = GSON_BUILDER.create();
    } finally {
      LOCK.unlock();
    }
  }

  public static Gson gson() {
    if (gson == null) {
      gson = new Gson();
    }
    return gson;
  }

  public static JsonElement parse(final String jsonString) {
    if (jsonString == null || jsonString.isEmpty()) {
      return null;
    }
    try {
      return JsonParser.parseString(jsonString);
    } catch (final Exception e) {
      Logger.warn("Invalid Json: " + jsonString);
      return null;
    }
  }

  public static <T> T parse(final String jsonString, Class<T> elementClass) {
    if (jsonString == null || jsonString.isEmpty() || elementClass == null) {
      return null;
    }
    try {
      return gson().fromJson(jsonString, elementClass);
    } catch (final Exception e) {
      Logger.warn("Invalid Json: " + jsonString);
      return null;
    }
  }

  public static JsonObject parseObject(final String jsonString) {
    final JsonElement jsonElement = parse(jsonString);
    return jsonElement != null && jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null;
  }

  public static JsonArray parseArray(final String jsonString) {
    final JsonElement jsonElement = parse(jsonString);
    return jsonElement != null && jsonElement.isJsonArray() ? jsonElement.getAsJsonArray() : null;
  }

  public static JsonObject getJsonObject(final JsonObject json, final String memberName) {
    if (json == null) {
      return null;
    }
    if (memberName == null) {
      return json;
    }
    final JsonElement element = json.get(memberName);
    if (element != null && element.isJsonObject()) {
      return element.getAsJsonObject();
    }
    return null;
  }

  public static JsonElement get(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return json.get(memberName);
  }

  public static JsonArray getJsonArray(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    final JsonElement element = json.get(memberName);
    if (element != null && element.isJsonArray()) {
      return element.getAsJsonArray();
    }
    return null;
  }

  public static String getString(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonString(json.get(memberName));
  }

  public static Boolean getBool(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonBool(json.get(memberName));
  }

  public static Short getShort(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonShort(json.get(memberName));
  }

  public static Byte getByte(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonByte(json.get(memberName));
  }

  public static Integer getInt(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonInt(json.get(memberName));
  }

  public static Long getLong(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonLong(json.get(memberName));
  }

  public static Float getFloat(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonFloat(json.get(memberName));
  }

  public static Double getDouble(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonDouble(json.get(memberName));
  }

  public static JsonObject toJsonObject(final JsonElement element) {
    return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
  }

  public static JsonObject toJsonArray(final JsonElement element) {
    return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
  }

  public static String toJsonString(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
  }

  public static Boolean toJsonBool(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : null;
  }

  public static Short toJsonShort(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsShort() : null;
  }

  public static Byte toJsonByte(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsByte() : null;
  }

  public static Integer toJsonInt(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsInt() : null;
  }

  public static Long toJsonLong(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsLong() : null;
  }

  public static Float toJsonFloat(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsFloat() : null;
  }

  public static Double toJsonDouble(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsDouble() : null;
  }

  public static <T> T toClass(final JsonElement element, Class<T> elementClass) {
    if (element == null || elementClass == null) {
      return null;
    }
    try {
      return gson().fromJson(element, elementClass);
    } catch (final Exception e) {
      Logger.warn("Invalid Json: " + element);
      return null;
    }
  }

  public static JsonObject requestJson(final String url) {
    return requestJson(url, 4000);
  }

  public static JsonObject requestJson(final String urlStr, final int timeout) {
    if (urlStr == null || urlStr.isBlank()) {
      return null;
    }
    final int finalTimeout = Math.max(Math.min(timeout, 60000), 100);
    try {
      final URL url = URI.create(urlStr).toURL();
      final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");
      connection.setConnectTimeout(finalTimeout);
      connection.setReadTimeout(finalTimeout);
      if (connection.getResponseCode() != 200) {
        return null;
      }
      final BufferedReader reader = FileUtils.createReader((connection.getInputStream()));
      final StringBuilder response = new StringBuilder();

      String line;
      while ((line = reader.readLine()) != null) {
        response.append(line);
      }

      reader.close();
      return JsonUtil.parseObject(response.toString());
    } catch (final Exception e) {
      Logger.warn("Failed to request JSON from URL: " + urlStr);
      e.printStackTrace();
      return null;
    }
  }
}
