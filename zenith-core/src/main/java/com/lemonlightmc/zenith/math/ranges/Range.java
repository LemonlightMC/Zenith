package com.lemonlightmc.zenith.math.ranges;

import java.util.Comparator;

import com.lemonlightmc.zenith.interfaces.Cloneable;

// TODO: Rework endsWith/startsWith
public interface Range<T extends Range<T, V>, V extends Number> extends Cloneable<T>, Comparable<T> {

  public static String[] parse(String str) {
    if (str == null || str.length() == 0) {
      return new String[] { "", "" };
    }
    if (str.charAt(0) == '[') {
      str = str.substring(1);
    }
    if (str.charAt(str.length() - 1) == ']') {
      str = str.substring(0, str.length() - 1);
    }
    final String[] parts = str.split("[,\\.\\s]+");
    if (parts == null || parts.length != 2) {
      return new String[] { "", "" };
    }

    return new String[] { parts[0].strip(), parts[1].strip() };
  }

  V getMin();

  V getMiddle();

  V getMax();

  boolean isLower(V i);

  boolean isHigher(V i);

  boolean isInRange(V i);

  boolean isOutsideRange(V i);

  boolean isAfter(V num);

  boolean isAfter(T range);

  boolean isBefore(V num);

  boolean isBefore(T range);

  boolean startsWith(V num);

  boolean startsWith(T range);

  boolean endsWith(V num);

  boolean endsWith(T range);

  boolean isEmpty();

  boolean contains(V num);

  boolean contains(T range);

  boolean overlaps(T range);

  T intersection(T range);

  V clamp(V num);

  T clamp(T range);

  V getLength();

  boolean isMaxValue();

  boolean isMinValue();

  @Override
  int compareTo(T o);

  Comparator<T> getComparator();

  @Override
  T clone();

  @Override
  String toString();

  @Override
  int hashCode();

  @Override
  boolean equals(Object obj);

}