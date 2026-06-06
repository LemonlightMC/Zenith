package com.lemonlightmc.zenith.messages;

import me.clip.placeholderapi.PlaceholderAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.base.ZenithPlugin;
import com.lemonlightmc.zenith.utils.StringUtils.Replaceable;

public class MessageFormatter {

  private static String prefix;
  private static String suffix;
  private static boolean hasPlaceholderAPI = false;
  private static List<Replaceable> globalPlaceholders = new ArrayList<>();

  public static enum MessageTypes {
    CHAT,
    ACTIONBAR,
    TITLE,
    SUBTITLE,
  }

  public static String format(String msg) {
    msg = getMessage(msg, null);
    if (msg == null) {
      return null;
    }
    if (!globalPlaceholders.isEmpty()) {
      for (final Replaceable replaceable : globalPlaceholders) {
        msg = replaceable.apply(msg);
      }
    }
    return TextStyling.convert(msg);
  }

  public static String format(
      final String msg,
      final Replaceable... replaceables) {
    return format(null, msg, false, true, replaceables);
  }

  public static String format(
      final String msg,
      final boolean withPrefix,
      final Replaceable... replaceables) {
    return format(null, msg, withPrefix, true, replaceables);
  }

  public static String format(
      final String msg,
      final boolean withPrefix,
      final boolean withColor,
      final Replaceable... replaceables) {
    return format(null, msg, withPrefix, withColor, replaceables);
  }

  public static String format(final Player p, String msg, final boolean withPrefix,
      final boolean withColor,
      final Replaceable... replaceables) {
    msg = getMessage(msg, null);
    if (msg == null) {
      return null;
    }

    if (p != null) {
      msg = parsePlaceholder(p, msg);
    }
    msg = applyReplacements(msg, replaceables);
    if (withPrefix) {
      msg = (prefix + " " + msg).replace("\n", "\n" + prefix + " ");
    }
    return withColor ? TextStyling.convert(msg) : TextStyling.strip(msg);
  }

  public static String getMessage(String msg, final Locale locale) {
    if (msg == null || msg.length() == 0) {
      return null;
    }
    if (msg.startsWith("messages.")) {
      msg = ZenithPlugin.getInstance().getMessageStore().getMessage(msg.substring(9), locale);
    }
    return msg == null || msg.length() == 0 ? null : msg;
  }

  public static void setPrefix(String prefix) {
    if (!prefix.endsWith("&r")) {
      prefix += "&r";
    }
    MessageFormatter.prefix = prefix;
  }

  public static String getPrefix() {
    return prefix;
  }

  public static void setSuffix(String suffix) {
    if (!prefix.endsWith("&r")) {
      suffix += "&r";
    }
    MessageFormatter.suffix = suffix;
  }

  public static String getSuffix() {
    return suffix;
  }

  public static String applyReplacements(String message, final Replaceable... replacements) {
    if (message == null || message.length() == 0) {
      return message;
    }
    if (replacements != null && replacements.length > 0) {
      for (final Replaceable replaceable : replacements) {
        message = replaceable.apply(message);
      }
    }
    if (!globalPlaceholders.isEmpty()) {
      for (final Replaceable replaceable : globalPlaceholders) {
        message = replaceable.apply(message);
      }
    }
    return message;
  }

  public static String parsePlaceholder(final CommandSender sender, final String str) {
    if (sender instanceof Player && hasPlaceholderAPI) {
      return PlaceholderAPI.setPlaceholders((Player) sender, str);
    }
    return str;
  }

  public static String parsePlaceholder(final Player p, final String str) {
    if (hasPlaceholderAPI) {
      return PlaceholderAPI.setPlaceholders(p, str);
    }
    return str;
  }

  public static void setPlaceholdersSupport(final boolean value) {
    hasPlaceholderAPI = value;
  }
}
