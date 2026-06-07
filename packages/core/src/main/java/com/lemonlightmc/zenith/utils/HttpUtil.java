package com.lemonlightmc.zenith.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

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
      .build();

  private HttpUtil() {
  }

  public static HttpRequest.Builder requestBuilder(final String url) {
    final HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("User-Agent", USER_AGENT)
        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT));
    builder.header("Accept", "application/json");
    return builder;
  }

  public static HttpRequest.Builder requestBuilder(final String url, final String method, final String... headers) {
    final HttpRequest.Builder builder = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .header("User-Agent", USER_AGENT)
        .timeout(Duration.ofSeconds(DEFAULT_TIMEOUT));

    switch (method.toUpperCase()) {
      case "POST" -> builder.POST(HttpRequest.BodyPublishers.noBody());
      case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.noBody());
      case "DELETE" -> builder.DELETE();
      case "HEAD" -> builder.method("HEAD", HttpRequest.BodyPublishers.noBody());
      case "PATCH" -> builder.method("PATCH", HttpRequest.BodyPublishers.noBody());
      case "GET" -> builder.GET();
      default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
    }
    for (int i = 0; i < headers.length - 1; i += 2) {
      builder.header(headers[i], headers[i + 1]);
    }
    return builder;
  }

  public static String get(HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new IOException("HTTP " + response.statusCode() + ": " + request.uri().toString());
      }

      return response.body();
    } catch (IOException | InterruptedException e) {
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
        throw new IOException("HTTP " + response.statusCode() + ": " + request.uri().toString());
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