package com.lemonlightmc.zenith.additive;

import java.io.BufferedReader;
import java.io.Reader;
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
import com.google.gson.reflect.TypeToken;
import com.lemonlightmc.zenith.additive.files.FileUtils;
import com.lemonlightmc.zenith.additive.logger.GlobalLogger;

/**
 * Utility methods for JSON serialization, parsing, and safe value extraction using Gson.
 */
public class JsonUtil {
  private static volatile Gson gson;
  private static final ReentrantLock LOCK = new ReentrantLock();
  private static GsonBuilder GSON_BUILDER = new GsonBuilder();

  /**
   * Applies custom configuration to the shared {@link GsonBuilder} and rebuilds the cached {@link Gson} instance.
   *
   * @param consumer configuration action applied to the shared builder
   */
  public static void customizeGsonBuilder(final Consumer<GsonBuilder> consumer) {
    LOCK.lock();
    try {
      consumer.accept(GSON_BUILDER);
      gson = GSON_BUILDER.create();
    } finally {
      LOCK.unlock();
    }
  }

  /**
   * Builds and caches a {@link Gson} instance from the current shared builder configuration.
   *
   * @return the finalized {@link Gson} instance
   */
  public static Gson finalizeGson() {
    LOCK.lock();
    try {
      gson = GSON_BUILDER.create();
      return gson;
    } finally {
      LOCK.unlock();
    }
  }

  /**
   * Returns the shared {@link Gson} instance, creating it if necessary.
   *
   * @return the shared {@link Gson} instance
   */
  public static Gson gson() {
    if (gson == null) {
      gson = finalizeGson();
    }
    return gson;
  }

  /**
   * Serializes the given object to a JSON string.
   *
   * @param obj object to serialize
   * @return the JSON representation, or {@code null} if serialization fails or the input is {@code null}
   */
  public static String toJson(final Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof final JsonElement element) {
      return toJson(element);
    }
    try {
      return gson().toJson(obj);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to serialize object to JSON: " + obj);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Serializes the given JSON element to a string.
   *
   * @param obj JSON element to serialize
   * @return the JSON representation, or {@code null} if serialization fails or the input is {@code null}
   */
  public static String toJson(final JsonElement obj) {
    if (obj == null) {
      return null;
    }
    try {
      return gson().toJson(obj);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to serialize object to JSON: " + obj);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Parses the given JSON string into a {@link JsonElement}.
   *
   * @param str JSON string to parse
   * @return the parsed element, or {@code null} if parsing fails or the input is {@code null} or empty
   */
  public static JsonElement fromJson(final String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    try {
      return JsonParser.parseString(str);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to parse JSON: " + str);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Parses the given JSON string into an instance of the specified class.
   *
   * @param str JSON string to parse
   * @param elementCls target class for deserialization
   * @param <T> target type
   * @return the deserialized value, or {@code null} if parsing fails or the input is {@code null} or empty
   */
  public static <T> T fromJson(final String str, final Class<T> elementCls) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    try {
      return gson().fromJson(str, elementCls);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to parse JSON: " + str);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Parses JSON from the given reader into an instance of the specified class.
   *
   * @param reader reader providing JSON content
   * @param elementCls target class for deserialization
   * @param <T> target type
   * @return the deserialized value, or {@code null} if parsing fails or the reader is {@code null}
   */
  public static <T> T fromJson(final Reader reader, final Class<T> elementCls) {
    if (reader == null) {
      return null;
    }
    try {
      return gson().fromJson(reader, elementCls);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to parse JSON from Reader");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Parses the given JSON string into a value described by the specified type token.
   *
   * @param str JSON string to parse
   * @param typeToken target type token for deserialization
   * @param <T> target type
   * @return the deserialized value, or {@code null} if parsing fails or the type token is {@code null}
   */
  public static <T> T fromJson(final String str, final TypeToken<T> typeToken) {
    // implicit null check for str
    if (typeToken == null) {
      return null;
    }
    try {
      return gson().fromJson(str, typeToken);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to parse JSON: " + str);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Parses JSON from the given reader into a value described by the specified type token.
   *
   * @param reader reader providing JSON content
   * @param typeToken target type token for deserialization
   * @param <T> target type
   * @return the deserialized value, or {@code null} if parsing fails or an input is {@code null}
   */
  public static <T> T fromJson(final Reader reader, final TypeToken<T> typeToken) {
    if (reader == null || typeToken == null) {
      return null;
    }
    try {
      return gson().fromJson(reader, typeToken);
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to parse JSON from reader");
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Returns the JSON object member with the given name.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the member as a {@link JsonObject}, or {@code null} if it is missing or not an object
   */
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

  /**
   * Returns the JSON element member with the given name.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the member element, or {@code null} if it is missing or an input is {@code null}
   */
  public static JsonElement get(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return json.get(memberName);
  }

  /**
   * Returns the JSON array member with the given name.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the member as a {@link JsonArray}, or {@code null} if it is missing or not an array
   */
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

  /**
   * Returns the string value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the string value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static String getString(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonString(json.get(memberName));
  }

  /**
   * Returns the boolean value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the boolean value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static Boolean getBool(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonBool(json.get(memberName));
  }

  /**
   * Returns the short value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the short value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static Short getShort(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonShort(json.get(memberName));
  }

  /**
   * Returns the byte value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the byte value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static Byte getByte(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonByte(json.get(memberName));
  }

  /**
   * Returns the integer value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the integer value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static Integer getInt(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonInt(json.get(memberName));
  }

  /**
   * Returns the long value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the long value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static Long getLong(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonLong(json.get(memberName));
  }

  /**
   * Returns the float value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the float value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static Float getFloat(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonFloat(json.get(memberName));
  }

  /**
   * Returns the double value of the specified member.
   *
   * @param json source JSON object
   * @param memberName name of the member to retrieve
   * @return the double value, or {@code null} if the member is missing, not a primitive, or an input is {@code null}
   */
  public static Double getDouble(final JsonObject json, final String memberName) {
    if (json == null || memberName == null) {
      return null;
    }
    return toJsonDouble(json.get(memberName));
  }

  /**
   * Converts the given element to a {@link JsonObject} when possible.
   *
   * @param element element to convert
   * @return the element as a {@link JsonObject}, or {@code null} if it is not an object
   */
  public static JsonObject toJsonObject(final JsonElement element) {
    return element != null && element.isJsonObject() ? element.getAsJsonObject() : null;
  }

  /**
   * Converts the given element to a {@link JsonArray} when possible.
   *
   * @param element element to convert
   * @return the element as a {@link JsonArray}, or {@code null} if it is not an array
   */
  public static JsonArray toJsonArray(final JsonElement element) {
    return element != null && element.isJsonObject() ? element.getAsJsonArray() : null;
  }

  /**
   * Converts the given primitive element to a string.
   *
   * @param element element to convert
   * @return the string value, or {@code null} if the element is not a primitive
   */
  public static String toJsonString(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsString() : null;
  }

  /**
   * Converts the given primitive element to a boolean.
   *
   * @param element element to convert
   * @return the boolean value, or {@code null} if the element is not a primitive
   */
  public static Boolean toJsonBool(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsBoolean() : null;
  }

  /**
   * Converts the given primitive element to a short.
   *
   * @param element element to convert
   * @return the short value, or {@code null} if the element is not a primitive
   */
  public static Short toJsonShort(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsShort() : null;
  }

  /**
   * Converts the given primitive element to a byte.
   *
   * @param element element to convert
   * @return the byte value, or {@code null} if the element is not a primitive
   */
  public static Byte toJsonByte(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsByte() : null;
  }

  /**
   * Converts the given primitive element to an integer.
   *
   * @param element element to convert
   * @return the integer value, or {@code null} if the element is not a primitive
   */
  public static Integer toJsonInt(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsInt() : null;
  }

  /**
   * Converts the given primitive element to a long.
   *
   * @param element element to convert
   * @return the long value, or {@code null} if the element is not a primitive
   */
  public static Long toJsonLong(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsLong() : null;
  }

  /**
   * Converts the given primitive element to a float.
   *
   * @param element element to convert
   * @return the float value, or {@code null} if the element is not a primitive
   */
  public static Float toJsonFloat(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsFloat() : null;
  }

  /**
   * Converts the given primitive element to a double.
   *
   * @param element element to convert
   * @return the double value, or {@code null} if the element is not a primitive
   */
  public static Double toJsonDouble(final JsonElement element) {
    return element != null && element.isJsonPrimitive() ? element.getAsDouble() : null;
  }

  /**
   * Deserializes the given element into an instance of the specified class.
   *
   * @param element element to deserialize
   * @param elementClass target class for deserialization
   * @param <T> target type
   * @return the deserialized value, or {@code null} if deserialization fails or the target class is {@code null}
   */
  public static <T> T toClass(final JsonElement element, final Class<T> elementClass) {
    // implicit null check for element
    if (elementClass == null) {
      return null;
    }
    try {
      return gson().fromJson(element, elementClass);
    } catch (final Exception e) {
      GlobalLogger.warn("Invalid Json: " + element);
      return null;
    }
  }

  /**
   * Deserializes the given element into a value described by the specified type token.
   *
   * @param element element to deserialize
   * @param typeToken target type token for deserialization
   * @param <T> target type
   * @return the deserialized value, or {@code null} if deserialization fails or the type token is {@code null}
   */
  public static <T> T toClass(final JsonElement element, final TypeToken<T> typeToken) {
    // implicit null check for element
    if (typeToken == null) {
      return null;
    }
    try {
      return gson().fromJson(element, typeToken);
    } catch (final Exception e) {
      GlobalLogger.warn("Invalid Json: " + element);
      return null;
    }
  }

  /**
   * Performs an HTTP GET request and parses the response body as a JSON object.
   *
   * @param url URL to request
   * @return the parsed JSON object, or {@code null} if the request or parsing fails
   */
  public static JsonObject requestJson(final String url) {
    return requestJson(url, 4000);
  }

  /**
   * Performs an HTTP GET request and parses the response body as a JSON object.
   *
   * @param urlStr URL to request
   * @param timeout connection and read timeout in milliseconds
   * @return the parsed JSON object, or {@code null} if the request or parsing fails
   */
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
      return JsonUtil.toJsonObject(JsonUtil.fromJson(response.toString()));
    } catch (final Exception e) {
      GlobalLogger.warn("Failed to request JSON from URL: " + urlStr);
      e.printStackTrace();
      return null;
    }
  }

}
