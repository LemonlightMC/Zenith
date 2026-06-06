package com.lemonlightmc.zenith.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import com.lemonlightmc.zenith.exceptions.DependencyException;
import com.lemonlightmc.zenith.exceptions.HttpException;
import com.lemonlightmc.zenith.files.FileUtils;

/**
 * Minimal HTTP client using HttpClient.
 */
public final class HttpUtil {

  private static final String USER_AGENT = "Zenith/1.0 (https://github.com/Julizey/Zenith)";
  private static final long DEFAULT_TIMEOUT = 10;

  private static final java.net.http.HttpClient CLIENT = java.net.http.HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(DEFAULT_TIMEOUT))
      .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
      .build();

  private HttpUtil() {
  }

  /**
   * Perform a GET request and return the response body as a string.
   *
   * @param url the URL to request
   * @return the response body
   * @throws DependencyException if the request fails
   */

  public static String get(final String url) {
    return get(url, DEFAULT_TIMEOUT, "Accept", "application/json");
  }

  public static String get(final String url, final String... headers) {
    return get(url, DEFAULT_TIMEOUT, headers);
  }

  /**
   * Perform a GET request with custom headers.
   *
   * @param url     the URL to request
   * @param headers headers in pairs (key, value, key, value, ...)
   * @return the response body
   */

  public static String get(final String url, long timeout, final String... headers) {
    if (url == null || url.isBlank()) {
      return null;
    }
    final long finalTimeout = Math.max(Math.min(timeout, 120), 1);
    try {
      final HttpRequest.Builder builder = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("User-Agent", USER_AGENT)
          .timeout(Duration.ofSeconds(finalTimeout))
          .GET();

      for (int i = 0; i < headers.length - 1; i += 2) {
        builder.header(headers[i], headers[i + 1]);
      }

      final HttpRequest request = builder.build();
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() >= 400) {
        throw new IOException("HTTP " + response.statusCode() + ": " + url);
      }

      return response.body();
    } catch (IOException | InterruptedException e) {
      throw new HttpException("HTTP request failed: " + url, e);
    }
  }

  public static void download(final String url, final Path target) {
    download(url, target, 300);
  }

  /**
   * Download a file from a URL.
   *
   * @param url    the URL to download
   * @param target the target path
   * @throws DependencyException if the download fails
   */
  public static void download(final String url, final Path target, long timeout) {
    try {
      final HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url))
          .header("User-Agent", USER_AGENT)
          .timeout(Duration.ofSeconds(timeout))
          .GET()
          .build();

      final HttpResponse<InputStream> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

      if (response.statusCode() >= 400) {
        throw new IOException("HTTP " + response.statusCode() + ": " + url);
      }

      // Ensure parent directories exist
      FileUtils.mkdirs(target.getParent());

      // Download to temp file first, then move atomically
      final Path tempFile = target.resolveSibling(target.getFileName() + ".tmp");
      try (InputStream in = response.body()) {
        FileUtils.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
      }

      FileUtils.moveFile(tempFile, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

    } catch (IOException | InterruptedException e) {
      throw new HttpException("Download failed: " + url, e);
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