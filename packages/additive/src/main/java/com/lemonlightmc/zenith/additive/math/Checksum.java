package com.lemonlightmc.zenith.additive.math;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Checksum utilities for verifying file integrity.
 */
public final class Checksum {

  private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

  private Checksum() {
  }

  /**
   * Supported checksum algorithms.
   */
  public static enum ChecksumType {
    /** SHA-256 digest algorithm. */
    SHA256("SHA-256"),
    /** SHA-512 digest algorithm. */
    SHA512("SHA-512"),
    /** SHA-1 digest algorithm. */
    SHA1("SHA-1"),
    /** MD5 digest algorithm. */
    MD5("MD5");

    private final String algorithm;

    ChecksumType(String algorithm) {
      this.algorithm = algorithm;
    }

    /**
     * Returns the Java Security algorithm name.
     *
     * @return the Java Security algorithm name
     */
    public String algorithm() {
      return algorithm;
    }
  }

  /**
   * Calculates the checksum of a file using the specified algorithm.
   *
   * @param path the file path
   * @param type the checksum algorithm
   * @return the checksum as a lowercase hexadecimal string
   * @throws RuntimeException if the checksum cannot be calculated
   */
  public static String hash(Path path, ChecksumType type) {
    try {
      MessageDigest digest = MessageDigest.getInstance(type.algorithm());

      try (InputStream in = Files.newInputStream(path)) {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = in.read(buffer)) != -1) {
          digest.update(buffer, 0, read);
        }
      }

      return bytesToHex(digest.digest());
    } catch (NoSuchAlgorithmException | IOException e) {
      throw new RuntimeException("Failed to calculate checksum: " + path, e);
    }
  }

  /**
   * Calculates the checksum of a byte array using the specified algorithm.
   *
   * @param data the data to hash
   * @param type the checksum algorithm
   * @return the checksum as a lowercase hexadecimal string
   * @throws RuntimeException if the algorithm is not available
   */
  public static String hash(byte[] data, ChecksumType type) {
    try {
      MessageDigest digest = MessageDigest.getInstance(type.algorithm());
      return bytesToHex(digest.digest(data));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(type.algorithm() + " not available", e);
    }
  }

  /**
   * Verifies that a file matches an expected checksum.
   *
   * @param path     the file path
   * @param expected the expected checksum as a hexadecimal string
   * @param type     the checksum algorithm, or {@code null} to skip verification
   * @return {@code true} if the checksums match or verification is skipped; otherwise {@code false}
   */
  public static boolean verify(Path path, String expected, ChecksumType type) {
    if (expected == null || expected.isEmpty() || type == null) {
      return true; // No checksum to verify
    }

    if (!Files.exists(path)) {
      return false;
    }

    String actual = hash(path, type);
    return actual.equalsIgnoreCase(expected);
  }

  /**
   * Calculates the SHA-256 checksum of a file.
   *
   * @param path the file path
   * @return the checksum as a lowercase hexadecimal string
   */
  public static String sha256(Path path) {
    return hash(path, ChecksumType.SHA256);
  }

  /**
   * Calculates the SHA-256 checksum of a byte array.
   *
   * @param data the data to hash
   * @return the checksum as a lowercase hexadecimal string
   */
  public static String sha256(byte[] data) {
    return hash(data, ChecksumType.SHA256);
  }

  /**
   * Verifies that a file matches an expected SHA-256 checksum.
   *
   * @param path     the file path
   * @param expected the expected checksum as a hexadecimal string
   * @return {@code true} if the checksums match; otherwise {@code false}
   */
  public static boolean verify(Path path, String expected) {
    return verify(path, expected, ChecksumType.SHA256);
  }

  /**
   * Converts a byte array to a lowercase hexadecimal string.
   *
   * @param bytes the bytes to convert
   * @return the hexadecimal representation of the given bytes
   */
  private static String bytesToHex(byte[] bytes) {
    char[] hex = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      int v = bytes[i] & 0xFF;
      hex[i * 2] = HEX_CHARS[v >>> 4];
      hex[i * 2 + 1] = HEX_CHARS[v & 0x0F];
    }
    return new String(hex);
  }

  /**
   * Converts a hexadecimal string to a byte array.
   *
   * @param hex the hexadecimal string to convert
   * @return the decoded byte array
   */
  public static byte[] hexToBytes(String hex) {
    int len = hex.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
          + Character.digit(hex.charAt(i + 1), 16));
    }
    return data;
  }
}
