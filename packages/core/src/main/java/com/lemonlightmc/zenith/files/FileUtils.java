package com.lemonlightmc.zenith.files;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileUtils extends FileOperations {

  public static BufferedReader createReader(final Path path) throws FileNotFoundException {
    return createReader(path, StandardCharsets.UTF_8);
  }

  public static BufferedReader createReader(final Path path, final Charset charset) throws FileNotFoundException {
    if (path == null || notExists(path)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedReader(
        new InputStreamReader(new FileInputStream(path.toFile()), charset == null ? StandardCharsets.UTF_8 : null));
  }

  public static BufferedReader createReader(final File file) throws FileNotFoundException {
    return createReader(file, StandardCharsets.UTF_8);
  }

  public static BufferedReader createReader(final File file, final Charset charset) throws FileNotFoundException {
    if (file == null || notExists(file)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedReader(
        new InputStreamReader(new FileInputStream(file), charset == null ? StandardCharsets.UTF_8 : null));
  }

  public static BufferedReader createReader(final InputStream stream) throws FileNotFoundException {
    return createReader(stream, StandardCharsets.UTF_8);
  }

  public static BufferedReader createReader(final InputStream stream, final Charset charset)
      throws FileNotFoundException {
    if (stream == null) {
      throw new IllegalArgumentException("Stream must not be null");
    }
    return new BufferedReader(new InputStreamReader(stream, charset == null ? StandardCharsets.UTF_8 : null));
  }

  public static BufferedWriter createWriter(final Path path) throws FileNotFoundException {
    return createWriter(path, StandardCharsets.UTF_8);
  }

  public static BufferedWriter createWriter(final Path path, final Charset charset) throws FileNotFoundException {
    if (path == null || notExists(path)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(path.toFile()), charset == null ? StandardCharsets.UTF_8 : null));
  }

  public static BufferedWriter createWriter(final File file) throws FileNotFoundException {
    return createWriter(file, StandardCharsets.UTF_8);
  }

  public static BufferedWriter createWriter(final File file, final Charset charset) throws FileNotFoundException {
    if (file == null || notExists(file)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedWriter(
        new OutputStreamWriter(new FileOutputStream(file), charset == null ? StandardCharsets.UTF_8 : null));
  }

  public static BufferedWriter createWriter(final OutputStream stream) throws FileNotFoundException {
    return createWriter(stream, StandardCharsets.UTF_8);
  }

  public static BufferedWriter createWriter(final OutputStream stream, final Charset charset)
      throws FileNotFoundException {
    if (stream == null) {
      throw new IllegalArgumentException("Stream must not be null");
    }
    return new BufferedWriter(new OutputStreamWriter(stream, charset == null ? StandardCharsets.UTF_8 : null));
  }

  public static BufferedInputStream createInputStream(final Path path)
      throws FileNotFoundException {
    if (path == null || notExists(path)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedInputStream(new FileInputStream(path.toFile()));
  }

  public static BufferedInputStream createInputStream(final File file)
      throws FileNotFoundException {
    if (file == null || notExists(file)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedInputStream(new FileInputStream(file));
  }

  public static BufferedInputStream createInputStream(final InputStream stream)
      throws FileNotFoundException {
    if (stream == null) {
      throw new IllegalArgumentException("Path must not be null");
    }
    return new BufferedInputStream(stream);
  }

  public static BufferedOutputStream createOutputStream(final Path path)
      throws FileNotFoundException {
    if (path == null || notExists(path)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedOutputStream(new FileOutputStream(path.toFile()));
  }

  public static BufferedOutputStream createOutputStream(final File file)
      throws FileNotFoundException {
    if (file == null || notExists(file)) {
      throw new IllegalArgumentException("Path must not be null and exist");
    }
    return new BufferedOutputStream(new FileOutputStream(file));
  }

  public static BufferedOutputStream createOutputStream(final OutputStream stream)
      throws FileNotFoundException {
    if (stream == null) {
      throw new IllegalArgumentException("Path must not be null");
    }
    return new BufferedOutputStream(stream);
  }
}
