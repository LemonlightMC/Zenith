package com.lemonlightmc.zenith.additive.math;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility methods for numeral conversions.
 */
public class Numerals {
  private static final LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();

  static {
    roman_numerals.put("M", 1000);
    roman_numerals.put("CM", 900);
    roman_numerals.put("D", 500);
    roman_numerals.put("CD", 400);
    roman_numerals.put("C", 100);
    roman_numerals.put("XC", 90);
    roman_numerals.put("L", 50);
    roman_numerals.put("XL", 40);
    roman_numerals.put("X", 10);
    roman_numerals.put("IX", 9);
    roman_numerals.put("V", 5);
    roman_numerals.put("IV", 4);
    roman_numerals.put("I", 1);
  }

  /**
   * Converts an integer value to its Roman numeral representation.
   *
   * @param arabNum the integer value to convert
   * @return the Roman numeral representation of the given value
   */
  public static String toRoman(int arabNum) {
    String res = arabNum < 0 ? "-" : "";
    arabNum = Math.abs(arabNum);
    for (final Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
      final int matches = arabNum / entry.getValue();
      if (matches > 0) {
        res += entry.getKey().repeat(matches);
        arabNum = arabNum % entry.getValue();
      }
    }
    return res;
  }
}
