package com.lemonlightmc.zenith.utils;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.lemonlightmc.zenith.exceptions.HttpException;
import com.lemonlightmc.zenith.files.FileUtils;

/**
 * Minimal HTTP client using HttpClient.
 */
public final class HttpUtil {

  private static final String USER_AGENT = "Zenith/1.0 (https://github.com/Julizey/Zenith)";
  private static final long DEFAULT_TIMEOUT = 10;

  private static final HttpClient CLIENT = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT))
      .followRedirects(HttpClient.Redirect.NORMAL)
      .version(Version.HTTP_2)
      .build();

  private HttpUtil() {
  }

  public static HttpRequest.Builder requestBuilder(final String url, final String method) {
    if (url == null || url.isEmpty()) {
      throw new IllegalArgumentException("HTTP URL cannot be null or empty");
    }
    if (method == null || method.isEmpty()) {
      throw new IllegalArgumentException("HTTP method cannot be null or empty");
    }
    final HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("User-Agent", USER_AGENT)
        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT))
        .header("Accept", "application/json");

    switch (method.toUpperCase()) {
      case "POST" -> builder.POST(HttpRequest.BodyPublishers.noBody());
      case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.noBody());
      case "DELETE" -> builder.DELETE();
      case "HEAD" -> builder.method("HEAD", HttpRequest.BodyPublishers.noBody());
      case "PATCH" -> builder.method("PATCH", HttpRequest.BodyPublishers.noBody());
      case "GET" -> builder.GET();
      default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }
    return builder;
  }

  public static HttpRequest.Builder requestBuilder(final String url, final String method, final List<String> headers) {
    HttpRequest.Builder builder = requestBuilder(url, method);
    if (headers == null || headers.isEmpty()) {
      return builder;
    }
    if (headers.size() % 2 != 0) {
      throw new IllegalArgumentException("Headers list must contain an even number of elements (key-value pairs)");
    }
    for (int i = 0; i < headers.size() - 1; i += 2) {
      builder.header(headers.get(i), headers.get(i + 1));
    }
    return builder;
  }

  public static HttpRequest.Builder requestBuilder(final String url, final String method,
      final Map<String, String> headers) {
    HttpRequest.Builder builder = requestBuilder(url, method);
    if (headers == null || headers.isEmpty()) {
      return builder;
    }
    if (headers.size() % 2 != 0) {
      throw new IllegalArgumentException("Headers list must contain an even number of elements (key-value pairs)");
    }
    for (Map.Entry<String, String> entry : headers.entrySet()) {
      builder.header(entry.getKey(), entry.getValue());
    }
    return builder;
  }

  public static String get(final String url) {
    return get(requestBuilder(url, "GET").build());
  }

  public static String put(final String url) {
    return put(requestBuilder(url, "PUT").build());
  }

  public static String post(final String url) {
    return post(requestBuilder(url, "POST").build());
  }

  public static String patch(final String url) {
    return patch(requestBuilder(url, "PATCH").build());
  }

  public static String delete(final String url) {
    return delete(requestBuilder(url, "DELETE").build());
  }

  public static String head(final String url) {
    return head(requestBuilder(url, "HEAD").build());
  }

  public static String get(HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  public static String put(HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  public static String post(HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  public static String patch(HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  public static String delete(HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  public static String head(HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  public static void download(final HttpRequest request, final Path target) {
    if (request == null) {
      return;
    }
    try {
      final HttpResponse<InputStream> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      // Ensure parent directories exist
      FileUtils.mkdirs(target.getParent());

      // Download to temp file first, then move atomically
      final Path tempFile = target.resolveSibling(target.getFileName() + ".tmp");
      try (InputStream in = response.body()) {
        FileUtils.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
      }

      FileUtils.moveFile(tempFile, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

    } catch (Exception e) {
      throw new HttpException("Download failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Check if a URL is accessible (returns 2xx status).
   *
   * @param url the URL to check
   * @return true if accessible
   */
  public static boolean isAccessible(final String url) {
    try {
      final HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("User-Agent", USER_AGENT)
          .method("HEAD", HttpRequest.BodyPublishers.noBody())
          .timeout(Duration.ofSeconds(5))
          .build();

      final HttpResponse<Void> response = CLIENT.send(request, HttpResponse.BodyHandlers.discarding());
      return response.statusCode() >= 200 && response.statusCode() < 300;
    } catch (final Exception e) {
      return false;
    }
  }
}