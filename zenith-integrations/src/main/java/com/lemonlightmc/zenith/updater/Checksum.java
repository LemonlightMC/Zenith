package com.lemonlightmc.zenith.updater;

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
    SHA256("SHA-256"),
    SHA512("SHA-512"),
    SHA1("SHA-1"),
    MD5("MD5");

    private final String algorithm;

    ChecksumType(String algorithm) {
      this.algorithm = algorithm;
    }

    /**
     * @return the Java Security algorithm name
     */
    public String algorithm() {
      return algorithm;
    }
  }

  /**
   * Calculate checksum of a file using the specified algorithm.
   *
   * @param path the file path
   * @param type the checksum algorithm
   * @return the checksum as a lowercase hex string
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
   * Calculate checksum of bytes using the specified algorithm.
   *
   * @param data the data
   * @param type the checksum algorithm
   * @return the checksum as a lowercase hex string
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
   * Verify that a file matches an expected checksum.
   *
   * @param path     the file path
   * @param expected the expected checksum (hex string)
   * @param type     the checksum algorithm (null to skip verification)
   * @return true if checksums match, or if no checksum/type provided
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
   * Calculate SHA-256 checksum of a file.
   *
   * @param path the file path
   * @return the checksum as a lowercase hex string
   */

  public static String sha256(Path path) {
    return hash(path, ChecksumType.SHA256);
  }

  /**
   * Calculate SHA-256 checksum of bytes.
   *
   * @param data the data
   * @return the checksum as a lowercase hex string
   */

  public static String sha256(byte[] data) {
    return hash(data, ChecksumType.SHA256);
  }

  /**
   * Verify that a file matches an expected SHA-256 checksum.
   *
   * @param path     the file path
   * @param expected the expected checksum (hex string)
   * @return true if checksums match
   */
  public static boolean verify(Path path, String expected) {
    return verify(path, expected, ChecksumType.SHA256);
  }

  /**
   * Convert bytes to lowercase hex string.
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
   * Convert hex string to bytes.
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
