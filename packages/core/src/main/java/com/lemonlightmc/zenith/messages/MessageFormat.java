package com.lemonlightmc.zenith.messages;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.interfaces.Cloneable;
import com.lemonlightmc.zenith.utils.StringUtils;
import com.lemonlightmc.zenith.utils.StringUtils.Replaceable;

import me.clip.placeholderapi.PlaceholderAPI;

public record MessageFormat(String prefix, boolean prefixEachLine,
    String suffix, boolean suffixEachLine, boolean withColors, boolean withPlaceholders, int maxWidth,
    boolean wrapLines,
    String nullReplacement, Map<String, String> globalPlaceholders,
    Map<String, Supplier<String>> globalPlaceholderSuppliers, DateTimeFormatter datetimeFormat,
    DateTimeFormatter timeFormat, DateTimeFormatter dateFormat)
    implements Cloneable<MessageFormat> {

  public MessageFormat(final String prefix, final boolean prefixEachLine,
      final String suffix, final boolean suffixEachLine, final boolean withColors, final boolean withPlaceholders,
      final int maxWidth,
      final boolean wrapLines,
      final String nullReplacement, final Map<String, String> globalPlaceholders,
      final Map<String, Supplier<String>> globalPlaceholderSuppliers, final DateTimeFormatter datetimeFormat,
      final DateTimeFormatter timeFormat, final DateTimeFormatter dateFormat) {
    this.prefix = prefix != null && prefix.isEmpty() ? null : prefix;
    this.suffix = suffix != null && suffix.isEmpty() ? null : suffix;
    this.withColors = withColors;
    this.withPlaceholders = withPlaceholders;
    this.prefixEachLine = prefixEachLine;
    this.suffixEachLine = suffixEachLine;
    this.maxWidth = maxWidth == -1 ? Integer.MAX_VALUE : Math.max(0, maxWidth);
    this.wrapLines = wrapLines;
    this.nullReplacement = nullReplacement == null ? "null" : nullReplacement;
    this.datetimeFormat = datetimeFormat == null ? DateTimeFormatter.ISO_DATE_TIME : datetimeFormat;
    this.timeFormat = timeFormat == null ? DateTimeFormatter.ISO_DATE : timeFormat;
    this.dateFormat = dateFormat == null ? DateTimeFormatter.ISO_TIME : dateFormat;

    this.globalPlaceholders = globalPlaceholders != null && globalPlaceholders.isEmpty() ? null : globalPlaceholders;
    this.globalPlaceholderSuppliers = globalPlaceholderSuppliers != null && globalPlaceholderSuppliers.isEmpty() ? null
        : globalPlaceholderSuppliers;
  }

  public MessageFormat(final MessageFormatBuilder builder) {
    this(builder.prefix, builder.prefixEachLine, builder.suffix, builder.suffixEachLine, builder.allowColors,
        builder.allowPlaceholders, builder.maxWidth, builder.wrapLines,
        builder.nullReplacement, builder.globalPlaceholders, builder.globalPlaceholders2, builder.datetimeFormat,
        builder.dateFormat, builder.timeFormat);
  }

  public MessageFormat(final MessageFormat other) {
    this(other.prefix, other.prefixEachLine, other.suffix, other.suffixEachLine, other.withColors,
        other.withPlaceholders, other.maxWidth, other.wrapLines,
        other.nullReplacement, other.globalPlaceholders, other.globalPlaceholderSuppliers, other.datetimeFormat,
        other.dateFormat, other.timeFormat);
  }

  public static MessageFormatBuilder builder() {
    return new MessageFormatBuilder();
  }

  private static final MessageFormat standartFormat = new MessageFormat(null, false, null, false, true, true, -1, false,
      "null",
      null, null, null, null, null);

  public static MessageFormat standard() {
    return standartFormat;
  }

  @Override
  public MessageFormat clone() {
    return new MessageFormat(this);
  }

  public String format(final String msg) {
    return format(null, msg);
  }

  public String format(final Player player, String msg, final Replaceable... replaceables) {
    if (msg == null || msg.isEmpty()) {
      return nullReplacement;
    }

    if (msg.startsWith("messages.")) {
      msg = ZenithProvider.getInstance().getMessageStore().getMessage(msg.substring(9), parsePlayerLocale(player));
    }
    if (msg == null || msg.isEmpty()) {
      return nullReplacement;
    }
    if (replaceables != null && replaceables.length != 0) {
      for (final Replaceable replaceable : replaceables) {
        if (replaceable != null) {
          msg = replaceable.apply(msg);
        }
      }
    }
    if (globalPlaceholders != null) {
      for (final Map.Entry<String, String> entry : globalPlaceholders.entrySet()) {
        msg.replaceAll(entry.getKey(), entry.getValue());
      }
    }
    if (globalPlaceholderSuppliers != null) {
      for (final Map.Entry<String, Supplier<String>> entry : globalPlaceholderSuppliers.entrySet()) {
        msg.replaceAll(entry.getKey(), entry.getValue().get());
      }
    }
    if (withPlaceholders && player != null) {
      msg = PlaceholderAPI.setPlaceholders(player, msg);
    }
    msg = applyDateTime(msg);
    if (prefix != null) {
      if (prefixEachLine && msg.contains("\n")) {
        msg = msg.replace("\n", "\n" + prefix);
      }
      msg = prefix + msg;
    }
    if (suffix != null) {
      if (suffixEachLine && msg.contains("\n")) {
        msg = msg.replace("\n", "\n" + suffix);
      }
      msg = msg + suffix;
    }
    msg = applyLayout(msg);
    return withColors ? TextStyling.convert(msg) : TextStyling.strip(msg);
  }

  private String applyDateTime(String message) {
    final LocalDateTime now = getTime();
    if (message.contains("{datetime}")) {
      message = message.replace("{datetime}", datetimeFormat.format(now));
    }
    if (message.contains("{date}")) {
      message = message.replace("{date}", dateFormat.format(now));
    }
    if (message.contains("{time}")) {
      message = message.replace("{time}", timeFormat.format(now));
    }
    return message;
  }

  private LocalDateTime getTime() {
    final Instant now = Clock.systemDefaultZone().instant();
    return LocalDateTime.ofEpochSecond(now.getEpochSecond(), now.getNano(),
        ZoneId.systemDefault().getRules().getOffset(now));
  }

  private static Locale parsePlayerLocale(final Player player) {
    if (player == null) {
      return Locale.getDefault();
    }
    try {
      final Locale locale = StringUtils.parseLocale(player.getLocale());
      return locale != null ? locale : Locale.getDefault();
    } catch (final Exception e) {
      return Locale.getDefault();
    }
  }

  private String applyLayout(final String message) {
    final String[] lines = message.split("\n", -1);
    for (int i = 0; i < lines.length; i++) {
      final String line = lines[i];
      if (line.length() <= maxWidth) {
        continue;
      }
      if (wrapLines) {
        lines[i] = wrapLine(line, maxWidth);
      }
    }
    return String.join("\n", lines);
  }

  private String wrapLine(final String line, final int width) {
    final StringBuilder out = new StringBuilder();
    int pos = 0;
    while (pos < line.length()) {
      final int end = Math.min(pos + width, line.length());
      // try to break at last space within window
      if (end < line.length()) {
        int space = line.lastIndexOf(' ', end);
        if (space <= pos) {
          space = end; // no space found, hard break
        }
        out.append(line, pos, space).append('\n');
        pos = (space == end) ? end : space + 1;
      } else {
        out.append(line.substring(pos));
        break;
      }
    }
    return out.toString();
  }

  public Map<String, Object> serialize() {
    final Map<String, Object> cfg = new HashMap<>();
    cfg.put("prefix", prefix);
    cfg.put("suffix", suffix);
    cfg.put("withColors", withColors);
    cfg.put("withPlaceholders", withPlaceholders);
    cfg.put("prefixEachLine", prefixEachLine);
    cfg.put("suffixEachLine", suffixEachLine);
    cfg.put("maxWidth", maxWidth);
    cfg.put("wrapLines", wrapLines);
    cfg.put("dateTimeFormatter", datetimeFormat == null ? null : datetimeFormat.toString());
    cfg.put("nullReplacement", nullReplacement);
    cfg.put("globalPlaceholders", globalPlaceholders);
    return cfg;
  }

  @SuppressWarnings("unchecked")
  public static MessageFormat deserialize(final Map<String, Object> cfg) {
    if (cfg == null)
      return MessageFormat.builder().build();
    final MessageFormatBuilder b = MessageFormat.builder();
    if (cfg.containsKey("prefix"))
      b.prefix(Objects.toString(cfg.get("prefix"), ""));
    if (cfg.containsKey("suffix"))
      b.suffix(Objects.toString(cfg.get("suffix"), ""));
    if (cfg.containsKey("withColors"))
      b.allowColors(Boolean.parseBoolean(Objects.toString(cfg.get("withColors"), "true")));
    if (cfg.containsKey("withPlaceholders"))
      b.allowPlaceholders(Boolean.parseBoolean(Objects.toString(cfg.get("withPlaceholders"), "true")));
    if (cfg.containsKey("prefixEachLine"))
      b.prefixEachLine(Boolean.parseBoolean(Objects.toString(cfg.get("prefixEachLine"), "true")));
    if (cfg.containsKey("suffixEachLine"))
      b.suffixEachLine(Boolean.parseBoolean(Objects.toString(cfg.get("suffixEachLine"), "false")));
    if (cfg.containsKey("maxWidth"))
      b.maxWidth(Integer.parseInt(Objects.toString(cfg.get("maxWidth"), "0")));
    if (cfg.containsKey("wrapLines"))
      b.wrapLines(Boolean.parseBoolean(Objects.toString(cfg.get("wrapLines"), "false")));
    if (cfg.containsKey("dateTimeFormat")) {
      b.dateTimeFormat(Objects.toString(cfg.get("dateTimeFormat"), ""));
    }
    if (cfg.containsKey("nullReplacement"))
      b.nullReplacement(Objects.toString(cfg.get("nullReplacement"), null));
    if (cfg.containsKey("globalPlaceholders")) {
      final Object o = cfg.get("globalPlaceholders");
      if (o instanceof Map) {
        final Map<String, String> map = ((Map<String, String>) o);
        final Map<String, String> result = new HashMap<>();
        for (final Map.Entry<String, String> e : map.entrySet()) {
          result.put(e.getKey(), e.getValue());
        }
        b.globalPlaceholders(result);
      }
    }
    return b.build();
  }

  public static class MessageFormatBuilder {

    private String prefix = "";
    private String suffix = "";
    private boolean allowColors = true;
    private boolean allowPlaceholders = true;
    private boolean prefixEachLine = true;
    private boolean suffixEachLine = false;
    private Map<String, String> globalPlaceholders;
    private Map<String, Supplier<String>> globalPlaceholders2;
    private DateTimeFormatter datetimeFormat = DateTimeFormatter.ISO_DATE_TIME;
    private DateTimeFormatter timeFormat = DateTimeFormatter.ISO_TIME;
    private DateTimeFormatter dateFormat = DateTimeFormatter.ISO_DATE;
    private int maxWidth = 0;
    private boolean wrapLines = false;
    private String nullReplacement;

    public MessageFormatBuilder prefix(final String prefix) {
      this.prefix = prefix == null ? "" : prefix;
      return this;
    }

    public MessageFormatBuilder suffix(final String suffix) {
      this.suffix = suffix == null ? "" : suffix;
      return this;
    }

    public MessageFormatBuilder allowColors(final boolean allowColors) {
      this.allowColors = allowColors;
      return this;
    }

    public MessageFormatBuilder allowPlaceholders(final boolean allowPlaceholders) {
      this.allowPlaceholders = allowPlaceholders;
      return this;
    }

    public MessageFormatBuilder prefixEachLine(final boolean prefixEachLine) {
      this.prefixEachLine = prefixEachLine;
      return this;
    }

    public MessageFormatBuilder suffixEachLine(final boolean suffixEachLine) {
      this.suffixEachLine = suffixEachLine;
      return this;
    }

    public MessageFormatBuilder globalPlaceholders(final Map<String, String> globalPlaceholders) {
      this.globalPlaceholders = globalPlaceholders;
      return this;
    }

    public MessageFormatBuilder globalPlaceholder(final String token, final Supplier<String> supplier) {
      if (token == null || token.isEmpty() || supplier == null)
        return this;
      if (globalPlaceholders2 == null) {
        globalPlaceholders2 = new HashMap<>();
      }
      this.globalPlaceholders2.put(token, supplier);
      return this;
    }

    public MessageFormatBuilder globalPlaceholder(final String token, final String supplier) {
      if (token == null || token.isEmpty() || supplier == null)
        return this;
      if (globalPlaceholders == null) {
        globalPlaceholders = new HashMap<>();
      }
      this.globalPlaceholders.put(token, supplier);
      return this;
    }

    public MessageFormatBuilder dateTimeFormat(final String pattern) {
      this.datetimeFormat = DateTimeFormatter.ofPattern(pattern);
      return this;
    }

    public MessageFormatBuilder dateTimeFormat(final FormatStyle style) {
      this.datetimeFormat = DateTimeFormatter.ofLocalizedDate(style).withZone(ZoneId.systemDefault());
      return this;
    }

    public MessageFormatBuilder dateTimeFormat(final DateTimeFormatter dtF) {
      this.datetimeFormat = dtF == null ? DateTimeFormatter.ISO_DATE_TIME : dtF;
      return this;
    }

    public MessageFormatBuilder dateFormat(final String pattern) {
      this.dateFormat = DateTimeFormatter.ofPattern(pattern);
      return this;
    }

    public MessageFormatBuilder dateFormat(final FormatStyle style) {
      this.dateFormat = DateTimeFormatter.ofLocalizedDate(style).withZone(ZoneId.systemDefault());
      return this;
    }

    public MessageFormatBuilder dateFormat(final DateTimeFormatter dateFormat) {
      this.dateFormat = dateFormat == null ? DateTimeFormatter.ISO_DATE : dateFormat;
      return this;
    }

    public MessageFormatBuilder timeFormat(final String pattern) {
      this.timeFormat = DateTimeFormatter.ofPattern(pattern);
      return this;
    }

    public MessageFormatBuilder timeFormat(final DateTimeFormatter timeFormat) {
      this.timeFormat = timeFormat == null ? DateTimeFormatter.ISO_TIME : timeFormat;
      return this;
    }

    public MessageFormatBuilder maxWidth(final int maxWidth) {
      this.maxWidth = Math.max(0, maxWidth);
      return this;
    }

    public MessageFormatBuilder wrapLines(final boolean wrapLines) {
      this.wrapLines = wrapLines;
      return this;
    }

    public MessageFormatBuilder nullReplacement(final String nullReplacement) {
      this.nullReplacement = nullReplacement;
      return this;
    }

    public MessageFormat build() {
      return new MessageFormat(this);
    }
  }
}