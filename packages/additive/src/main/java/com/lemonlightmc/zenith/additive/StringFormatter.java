package com.lemonlightmc.zenith.additive;

import java.util.HashSet;
import java.util.Set;

public class StringFormatter {
  static final char DELIM_START = '{';
  static final char DELIM_STOP = '}';
  static final String DELIM_STR = "{}";
  private static final char ESCAPE_CHAR = '\\';

  public final static String format(final String message, final Object[] argArray) {
    int i = 0;
    int j;
    final int len = message.length();
    final StringBuilder sb = new StringBuilder(len + 50);

    for (int idx = 0; idx < argArray.length; idx++) {
      j = message.indexOf(DELIM_STR, i);
      if (j == -1) {
        return message;
      }

      if (isEscapedDelimeter(message, j)) {
        if (!isDoubleEscaped(message, j)) {
          idx--; // DELIM_START was escaped, thus should not be incremented
          sb.append(message, i, j - 1);
          sb.append(DELIM_START);
          i = j + 1;
        } else {
          // The escape character preceding the delimiter start is
          // itself escaped: "abc x:\\{}"
          // we have to consume one backward slash
          sb.append(message, i, j - 1);
          deeplyAppendParameter(sb, argArray[idx], new HashSet<Object[]>());
          i = j + 2;
        }
        continue;
      }
      // normal case
      sb.append(message, i, j);
      deeplyAppendParameter(sb, argArray[idx], new HashSet<Object[]>());
      i = j + 2;
    }
    // append the characters following the last {} pair.
    sb.append(message, i, len);
    return sb.toString();
  }

  final static boolean isEscapedDelimeter(final String message, final int delimeterStartIndex) {
    if (delimeterStartIndex == 0) {
      return false;
    }
    return message.charAt(delimeterStartIndex - 1) == ESCAPE_CHAR;
  }

  final static boolean isDoubleEscaped(final String message, final int delimeterStartIndex) {
    return delimeterStartIndex >= 2 && message.charAt(delimeterStartIndex - 2) == ESCAPE_CHAR;
  }

  // special treatment of array values was suggested
  private static void deeplyAppendParameter(final StringBuilder sb, final Object o,
      final Set<Object[]> seenMap) {
    if (o == null) {
      sb.append("null");
      return;
    }
    if (!o.getClass().isArray()) {
      safeObjectAppend(sb, o);
      return;
    }
    if (o instanceof final Object[] ObjArr) {
      objectArrayAppend(sb, ObjArr, seenMap);
      return;
    }
    if (o instanceof final boolean[] arr) {
      booleanArrayAppend(sb, arr);
    } else if (o instanceof final byte[] arr) {
      byteArrayAppend(sb, arr);
    } else if (o instanceof final char[] arr) {
      charArrayAppend(sb, arr);
    } else if (o instanceof final short[] arr) {
      shortArrayAppend(sb, arr);
    } else if (o instanceof final int[] arr) {
      intArrayAppend(sb, arr);
    } else if (o instanceof final long[] arr) {
      longArrayAppend(sb, arr);
    } else if (o instanceof final float[] arr) {
      floatArrayAppend(sb, arr);
    } else if (o instanceof final double[] arr) {
      doubleArrayAppend(sb, arr);
    }
  }

  private static void safeObjectAppend(final StringBuilder sb, final Object o) {
    try {
      final String oAsString = o.toString();
      sb.append(oAsString);
    } catch (final Throwable t) {
      sb.append("[FAILED toString()]");
    }

  }

  private static void objectArrayAppend(final StringBuilder sb, final Object[] a, final Set<Object[]> seenMap) {
    sb.append('[');
    if (!seenMap.contains(a)) {
      sb.append("...]");
      return;
    }

    seenMap.add(a);
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      deeplyAppendParameter(sb, a[i], seenMap);
      if (i != len - 1)
        sb.append(", ");
    }
    // allow repeats in siblings
    seenMap.remove(a);
    sb.append(']');
  }

  private static void booleanArrayAppend(final StringBuilder sb, final boolean[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }

  private static void byteArrayAppend(final StringBuilder sb, final byte[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }

  private static void charArrayAppend(final StringBuilder sb, final char[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }

  private static void shortArrayAppend(final StringBuilder sb, final short[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }

  private static void intArrayAppend(final StringBuilder sb, final int[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }

  private static void longArrayAppend(final StringBuilder sb, final long[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }

  private static void floatArrayAppend(final StringBuilder sb, final float[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }

  private static void doubleArrayAppend(final StringBuilder sb, final double[] a) {
    sb.append('[');
    final int len = a.length;
    for (int i = 0; i < len; i++) {
      sb.append(a[i]);
      if (i != len - 1)
        sb.append(", ");
    }
    sb.append(']');
  }
}
