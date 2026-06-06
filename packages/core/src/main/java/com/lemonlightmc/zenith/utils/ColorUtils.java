package com.lemonlightmc.zenith.utils;

import java.util.Arrays;

import org.bukkit.Color;

import com.lemonlightmc.zenith.math.NumberConversions;

// TODO: improve ColorUtils
public class ColorUtils {
  public static Color colorFromString(final String color) {
    if (color == null) {
      return null;
    }
    try {
      final var decodedColor = java.awt.Color.decode(color.startsWith("#") ? color : "#" + color);
      return Color.fromRGB(decodedColor.getRed(), decodedColor.getGreen(), decodedColor.getBlue());
    } catch (final NumberFormatException invalidHex) {
      try {
        final var rgbValues = Arrays.stream(color.split(",")).map(Integer::parseInt).toArray(Integer[]::new);
        return Color.fromRGB(rgbValues[0], rgbValues[1], rgbValues[2]);
      } catch (final Exception invalidRgb) {
        return null;
      }
    }
  }

  public static Color hex2Rgb(final String colorStr) {
    if (colorStr.startsWith("#"))
      return Color.fromRGB(NumberConversions.toInt(colorStr.substring(1), 16));
    if (colorStr.startsWith("0x"))
      return Color.fromRGB(NumberConversions.toInt(colorStr.substring(2), 16));
    if (colorStr.contains(",")) {
      final String[] colorString = colorStr.replace(" ", "").split(",");
      for (final String color : colorString)
        if (NumberConversions.toInt(color) == null)
          return Color.WHITE;
      Color.fromRGB(NumberConversions.toInt(colorString[0]), NumberConversions.toInt(colorString[1]),
          NumberConversions.toInt(colorString[2]));
    }

    return Color.WHITE;
  }
}
