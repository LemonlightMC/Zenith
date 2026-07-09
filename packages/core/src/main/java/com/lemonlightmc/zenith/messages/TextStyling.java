package com.lemonlightmc.zenith.messages;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lemonlightmc.zenith.additive.math.NumberConversions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TextStyling {

  private static boolean hasHexSupport = true;

  public static final Pattern STRIP_COLOR_PATTERN = Pattern.compile(
      "(?i)" + String.valueOf(ChatColor.COLOR_CHAR) + "[0-9A-FK-ORX]");
  private static final Pattern HEX_PATTERN = Pattern.compile(
      "<hex:([0-9A-Fa-f]{6})>|<#([0-9A-Fa-f]{6})>|#<([0-9A-Fa-f]{6})>|{#([0-9A-Fa-f]{6})}|{hex:([0-9A-Fa-f]{6})}||#{([0-9A-Fa-f]{6})}/gmi");
  private static final Pattern GRADIENT_PATTERN = Pattern.compile(
      "<gradient:([0-9A-Fa-f]{6})>(.*?)<\\/([0-9A-Fa-f]{6})>|{gradient:([0-9A-Fa-f]{6})>(.*?)<\\\\/([0-9A-Fa-f]{6})}/gmi");
  private static final Pattern RAINBOW_PATTERN = Pattern
      .compile("</<rainbow:([0-9]{1,3})>(.*?)<\\/rainbow>|<rainbow>(.*?)<\\/rainbow>/gmi");

  private static final List<String> SPECIAL_COLORS = List.of("&l", "&n", "&o", "&k", "&m", "&r");
  private static final String ALL_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
  private static final HashMap<String, String> styleMap = new HashMap<String, String>();
  static {
    styleMap.put("<black>", "&0");
    styleMap.put("<dark_blue>", "&1");
    styleMap.put("<dark_green>", "&2");
    styleMap.put("<dark_aqua>", "&3");
    styleMap.put("<dark_red>", "&4");
    styleMap.put("<dark_purple>", "&5");
    styleMap.put("<gold>", "&6");
    styleMap.put("<gray>", "&7");
    styleMap.put("<dark_gray>", "&8");
    styleMap.put("<blue>", "&9");
    styleMap.put("<green>", "&a");
    styleMap.put("<aqua>", "&b");
    styleMap.put("<red>", "&c");
    styleMap.put("<light_purple>", "&d");
    styleMap.put("<yellow>", "&e");
    styleMap.put("<white>", "&f");
    styleMap.put("<obfuscated>", "&k");
    styleMap.put("<bold>", "&l");
    styleMap.put("<strikethrough>", "&m");
    styleMap.put("<underline>", "&n");
    styleMap.put("<italic>", "&o");
    styleMap.put("<reset>", "&r");
    styleMap.put("<new_line>", "\n");
    styleMap.put("<br>", "\n");
    styleMap.put("<space>", " ");
    styleMap.put("<tab>", "\t");
  }

  public static void setHexSupport(final boolean support) {
    hasHexSupport = support;
  }

  public static boolean hasHexSupport() {
    return hasHexSupport;
  }

  public TextComponent string2component(final String text) {
    return new TextComponent(text);
  }

  public String component2String(final TextComponent component) {
    return component.toLegacyText();
  }

  public static TextComponent onHover(final HoverEvent.Action action, final TextComponent component,
      final String data) {
    component
        .setHoverEvent(new HoverEvent(action, new net.md_5.bungee.api.chat.hover.content.Text(data)));
    return component;
  }

  public static TextComponent onHover(final HoverEvent.Action action, final String text, final String data) {
    final TextComponent component = new TextComponent(text);
    component
        .setHoverEvent(new HoverEvent(action, new net.md_5.bungee.api.chat.hover.content.Text(data)));
    return component;
  }

  public static TextComponent onClick(final ClickEvent.Action action, final String text, final String data) {
    final TextComponent component = new TextComponent(text);
    component.setClickEvent(new ClickEvent(action, data));
    return component;
  }

  public static TextComponent onClick(final ClickEvent.Action action, final TextComponent component,
      final String data) {
    component.setClickEvent(new ClickEvent(action, data));
    return component;
  }

  public static String color(final String string, final Color color) {
    return mapColor(color) + string;
  }

  public static String color(final String string, final ChatColor color) {
    return mapColor(color) + string;
  }

  public static String color(final String string, final org.bukkit.ChatColor color) {
    return mapColor(color.asBungee()) + string;
  }

  public static String gradient(final String string, final Color start, final Color end) {
    final ChatColor[] colors = createGradient(start, end, stripSpecial(string).length());
    return apply(string, colors);
  }

  public static String rainbow(final String string, final float saturation) {
    final ChatColor[] colors = createRainbow(stripSpecial(string).length(), saturation);
    return apply(string, colors);
  }

  public static String strip(final String input) {
    if (input == null || input.length() == 0) {
      return "";
    }
    return STRIP_COLOR_PATTERN.matcher(convert(input)).replaceAll("");
  }

  public static String stripSpecial(String str) {
    for (final String color : SPECIAL_COLORS) {
      if (str.contains(color))
        str = str.replace(color, "");
    }
    return str;
  }

  public static String convert(String str) {
    for (final HashMap.Entry<String, String> entry : styleMap.entrySet()) {
      str = str.replace(entry.getKey(), entry.getValue());
    }
    if (hasHexSupport) {
      str = convertHex(str);
      str = convertGradient(str);
    }

    final char[] b = str.toCharArray();
    final int len = b.length - 1;
    for (int i = 0; i < len; i++) {
      if (b[i] == '&' && ALL_CODES.indexOf(b[i + 1]) > -1 && b[i - 1] != '&') {
        b[i] = ChatColor.COLOR_CHAR;
        b[i + 1] = Character.toLowerCase(b[i + 1]);
      }
    }
    return new String(b);
  }

  public static ChatColor mapColor(final String string) {
    return hasHexSupport ? ChatColor.of(new Color(NumberConversions.parseInt(string, 16)))
        : getClosestColor(new Color(NumberConversions.parseInt(string, 16)));
  }

  public static ChatColor mapColor(final Color color) {
    return (hasHexSupport ? ChatColor.of(color) : getClosestColor(color));
  }

  public static ChatColor mapColor(final ChatColor color) {
    return (hasHexSupport ? color : getClosestColor(color.getColor()));
  }

  public static ChatColor getClosestColor(final Color color) {
    ChatColor nearestColor = null;
    double nearestDistance = 2.147483647E9D;
    for (final char code : ChatColor.ALL_CODES.toCharArray()) {
      final ChatColor constantColor = ChatColor.getByChar(code);
      final double distance = Math.pow((color.getRed() - constantColor.getColor().getRed()), 2.0D)
          + Math.pow((color.getGreen() - constantColor.getColor().getGreen()), 2.0D)
          + Math.pow((color.getBlue() - constantColor.getColor().getBlue()), 2.0D);
      if (nearestDistance > distance) {
        nearestColor = constantColor;
        nearestDistance = distance;
      }
    }
    return nearestColor;
  }

  public static String convertHex(String string) {
    final Matcher matcher = HEX_PATTERN.matcher(string);
    while (matcher.find()) {
      String color = matcher.group(1);
      if (color == null)
        color = matcher.group(2);
      string = string.replace(matcher.group(), mapColor(color) + "");
    }
    return string;
  }

  public static String convertGradient(String string) {
    final Matcher matcher = GRADIENT_PATTERN.matcher(string);
    while (matcher.find()) {
      final String start = matcher.group(1);
      final String end = matcher.group(3);
      final String content = matcher.group(2);
      string = string.replace(matcher.group(),
          gradient(content, new Color(NumberConversions.parseInt(start, 16)),
              new Color(NumberConversions.parseInt(end, 16))));
    }
    return string;
  }

  public String convertRainbow(String string) {
    final Matcher matcher = RAINBOW_PATTERN.matcher(string);
    while (matcher.find()) {
      final String saturation = matcher.group(1);
      final String content = matcher.group(2);
      string = string.replace(matcher.group(), rainbow(content, NumberConversions.parseFloat(saturation)));
    }
    return string;
  }

  private static ChatColor[] createGradient(final Color start, final Color end, int step) {
    step = Math.max(step, 2);
    final ChatColor[] colors = new ChatColor[step];
    final int stepR = (Math.abs(start.getRed() - end.getRed()) / (step - 1))
        * (int) -Math.signum(start.getRed() - end.getRed());
    final int stepG = (Math.abs(start.getGreen() - end.getGreen()) / (step - 1))
        * (int) -Math.signum(start.getGreen() - end.getGreen());
    final int stepB = (Math.abs(start.getBlue() - end.getBlue()) / (step - 1))
        * (int) -Math.signum(start.getBlue() - end.getBlue());

    for (int i = 0; i < step; i++) {
      final Color color = new Color(start.getRed() + stepR * i, start.getGreen() + stepG * i,
          start.getBlue() + stepB * i);
      colors[i] = mapColor(color);
    }
    return colors;
  }

  private static ChatColor[] createRainbow(final int step, final float saturation) {
    final ChatColor[] colors = new ChatColor[step];
    for (int i = 0; i < step; i++) {
      final Color color = Color.getHSBColor((float) (1.0D / step * i), saturation, saturation);
      colors[i] = mapColor(color);
    }
    return colors;
  }

  private static String apply(final String source, final ChatColor[] colors) {
    final StringBuilder specialColors = new StringBuilder();
    final StringBuilder stringBuilder = new StringBuilder();
    int outIndex = 0;
    final int len = source.length();
    for (int i = 0; i < len; i++) {
      final char currentChar = source.charAt(i);
      if (currentChar != '&' && currentChar != '§' || i + 1 >= len) {
        stringBuilder.append(colors[outIndex++]).append(specialColors).append(currentChar);
      } else {
        final char nextChar = source.charAt(++i);
        if (nextChar == 'r' || nextChar == 'R') {
          specialColors.setLength(0);
        } else {
          specialColors.append(currentChar).append(nextChar);
        }
      }
    }
    return stringBuilder.toString();
  }
}
