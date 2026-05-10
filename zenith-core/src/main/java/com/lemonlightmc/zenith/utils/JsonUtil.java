package com.lemonlightmc.zenith.utils;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.lemonlightmc.zenith.files.FileUtils;
import com.lemonlightmc.zenith.messages.Logger;

public class JsonUtil {

  public static JsonObject parse(final String jsonString) {
    if (jsonString == null || jsonString.isEmpty()) {
      return null;
    }
    try {
      final JsonElement jsonElement = JsonParser.parseString(jsonString);
      return jsonElement != null && jsonElement.isJsonObject() ? jsonElement.getAsJsonObject() : null;
    } catch (final Exception e) {
      Logger.warn("Invalid Json: " + jsonString);
      return null;
    }
  }

  public static String getString(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    final JsonElement element = json.get(memberName);
    if (element != null && element instanceof final JsonPrimitive primitive) {
      return primitive.getAsString();
    }
    return null;
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

  public static JsonObject getJsonObject(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    final JsonElement element = json.get(memberName);
    if (element != null && element.isJsonObject()) {
      return element.getAsJsonObject();
    }
    return null;
  }

  public static JsonObject toJsonObject(final JsonElement element) {
    return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
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
      return JsonUtil.parse(response.toString());
    } catch (final Exception e) {
      Logger.warn("Failed to request JSON from URL: " + urlStr);
      e.printStackTrace();
      return null;
    }
  }
}
