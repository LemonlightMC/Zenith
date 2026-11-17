package com.lemonlightmc.zenith.additive;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import com.lemonlightmc.zenith.additive.exceptions.HttpException;
import com.lemonlightmc.zenith.additive.files.FileUtils;

/**
 * Utility methods for building and executing HTTP requests.
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

  /**
   * Creates a configured HTTP request builder for the given URL and method.
   *
   * @param url the target URL
   * @param method the HTTP method name
   * @return a configured {@link HttpRequest.Builder}
   * @throws IllegalArgumentException if {@code url} or {@code method} is null or empty, or if the
   *     method is unsupported
   */
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

  /**
   * Creates a configured HTTP request builder with header pairs from a list.
   *
   * @param url the target URL
   * @param method the HTTP method name
   * @param headers alternating header names and values
   * @return a configured {@link HttpRequest.Builder}
   * @throws IllegalArgumentException if the headers list does not contain key-value pairs
   */
  public static HttpRequest.Builder requestBuilder(final String url, final String method, final List<String> headers) {
    final HttpRequest.Builder builder = requestBuilder(url, method);
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

  /**
   * Creates a configured HTTP request builder with headers from a map.
   *
   * @param url the target URL
   * @param method the HTTP method name
   * @param headers header names mapped to values
   * @return a configured {@link HttpRequest.Builder}
   * @throws IllegalArgumentException if the headers map size is invalid for key-value processing
   */
  public static HttpRequest.Builder requestBuilder(final String url, final String method,
      final Map<String, String> headers) {
    final HttpRequest.Builder builder = requestBuilder(url, method);
    if (headers == null || headers.isEmpty()) {
      return builder;
    }
    if (headers.size() % 2 != 0) {
      throw new IllegalArgumentException("Headers list must contain an even number of elements (key-value pairs)");
    }
    for (final Map.Entry<String, String> entry : headers.entrySet()) {
      builder.header(entry.getKey(), entry.getValue());
    }
    return builder;
  }

  /**
   * Sends an HTTP GET request to the given URL.
   *
   * @param url the target URL
   * @return the response body
   * @throws HttpException if the request fails or returns an error status
   */
  public static String get(final String url) {
    return get(requestBuilder(url, "GET").build());
  }

  /**
   * Sends an HTTP PUT request to the given URL.
   *
   * @param url the target URL
   * @return the response body
   * @throws HttpException if the request fails or returns an error status
   */
  public static String put(final String url) {
    return put(requestBuilder(url, "PUT").build());
  }

  /**
   * Sends an HTTP POST request to the given URL.
   *
   * @param url the target URL
   * @return the response body
   * @throws HttpException if the request fails or returns an error status
   */
  public static String post(final String url) {
    return post(requestBuilder(url, "POST").build());
  }

  /**
   * Sends an HTTP PATCH request to the given URL.
   *
   * @param url the target URL
   * @return the response body
   * @throws HttpException if the request fails or returns an error status
   */
  public static String patch(final String url) {
    return patch(requestBuilder(url, "PATCH").build());
  }

  /**
   * Sends an HTTP DELETE request to the given URL.
   *
   * @param url the target URL
   * @return the response body
   * @throws HttpException if the request fails or returns an error status
   */
  public static String delete(final String url) {
    return delete(requestBuilder(url, "DELETE").build());
  }

  /**
   * Sends an HTTP HEAD request to the given URL.
   *
   * @param url the target URL
   * @return the response body
   * @throws HttpException if the request fails or returns an error status
   */
  public static String head(final String url) {
    return head(requestBuilder(url, "HEAD").build());
  }

  /**
   * Downloads the content at the given URL to the specified target path.
   *
   * @param url the source URL
   * @param target the destination path
   * @throws HttpException if the download fails or returns an error status
   */
  public static void download(final String url, final Path target) {
    download(requestBuilder(url, "GET").build(), target);
  }

  /**
   * Sends the given HTTP request and returns the response body as a string.
   *
   * @param request the request to send
   * @return the response body, or {@code null} if {@code request} is {@code null}
   * @throws HttpException if the request fails or returns an error status
   */
  public static String get(final HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (final Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Sends the given HTTP request and returns the response body as a string.
   *
   * @param request the request to send
   * @return the response body, or {@code null} if {@code request} is {@code null}
   * @throws HttpException if the request fails or returns an error status
   */
  public static String put(final HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (final Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Sends the given HTTP request and returns the response body as a string.
   *
   * @param request the request to send
   * @return the response body, or {@code null} if {@code request} is {@code null}
   * @throws HttpException if the request fails or returns an error status
   */
  public static String post(final HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (final Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Sends the given HTTP request and returns the response body as a string.
   *
   * @param request the request to send
   * @return the response body, or {@code null} if {@code request} is {@code null}
   * @throws HttpException if the request fails or returns an error status
   */
  public static String patch(final HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (final Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Sends the given HTTP request and returns the response body as a string.
   *
   * @param request the request to send
   * @return the response body, or {@code null} if {@code request} is {@code null}
   * @throws HttpException if the request fails or returns an error status
   */
  public static String delete(final HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (final Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Sends the given HTTP request and returns the response body as a string.
   *
   * @param request the request to send
   * @return the response body, or {@code null} if {@code request} is {@code null}
   * @throws HttpException if the request fails or returns an error status
   */
  public static String head(final HttpRequest request) {
    if (request == null) {
      return null;
    }
    try {
      final HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.statusCode() >= 400) {
        throw new HttpException(response);
      }

      return response.body();
    } catch (final Exception e) {
      throw new HttpException("HTTP request failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Downloads the response content for the given request to the specified target path.
   *
   * @param request the request to send
   * @param target the destination path
   * @throws HttpException if the download fails or returns an error status
   */
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

    } catch (final Exception e) {
      throw new HttpException("Download failed: " + request.uri().toString(), e);
    }
  }

  /**
   * Checks whether a URL is accessible by performing a HEAD request.
   *
   * @param url the URL to check
   * @return {@code true} if the request succeeds with a 2xx status code
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