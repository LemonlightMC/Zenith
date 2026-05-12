package com.lemonlightmc.zenith.apis;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.time.Ticks;
import com.lemonlightmc.zenith.utils.MathUtils;
import com.lemonlightmc.zenith.utils.StringUtils.Replaceable;

// TODO: Rework Title API for V1.1
public class TitleAPI {

  public static final int DEFAULT_FADEIN = 10;
  public static final int DEFAULT_STAY = 70;
  public static final int DEFAULT_FADEOUT = 20;

  public static TitleBuilder builder() {
    return new TitleBuilder();
  }

  public static TitleBuilder builder(final String title) {
    return new TitleBuilder(title);
  }

  public static TitleBuilder builder(final String title, final String subtitle) {
    return new TitleBuilder(title, subtitle);
  }

  public static void broadcast(
      final TitleInfo info) {
    if (info == null) {
      return;
    }
    info.broadcast();
  }

  public static void broadcast(
      final String msg,
      final Replaceable... replaceables) {
    TitleInfo.title(msg, replaceables).broadcast();
  }

  public static void broadcast(
      final String msg,
      final int fadeIn,
      final int stay,
      final int fadeOut,
      final Replaceable... replaceables) {
    TitleInfo.title(msg, fadeIn, stay, fadeOut, replaceables).broadcast();
  }

  public static void broadcast(
      final String msg, final String subMsg,
      final Replaceable... replaceables) {
    TitleInfo.from(msg, subMsg, replaceables).broadcast();
  }

  public static void broadcast(
      final String msg,
      final String subMsg,
      final int fadeIn,
      final int stay,
      final int fadeOut,
      final Replaceable... replaceables) {
    TitleInfo.from(msg, subMsg, fadeIn, stay, fadeOut, replaceables).broadcast();
  }

  public static void send(
      final Player players,
      final TitleInfo info) {
    if (info == null) {
      return;
    }
    info.send(players);
  }

  public static void send(
      final Player[] players,
      final TitleInfo info) {
    if (info == null) {
      return;
    }
    info.send(players);
  }

  public static void send(
      final List<Player> players,
      final TitleInfo info) {
    if (info == null) {
      return;
    }
    info.send(players);
  }

  public static void sendTitle(
      final Player p,
      final String msg,
      final Replaceable... replaceables) {
    TitleInfo.title(msg, replaceables).send(p);
  }

  public static void sendTitle(
      final Player p,
      final String msg,
      final int fadeIn,
      final int stay,
      final int fadeOut,
      final Replaceable... replaceables) {
    TitleInfo.title(msg, fadeIn, stay, fadeOut, replaceables).send(p);
  }

  public static void sendSubtitle(
      final Player p,
      final String msg,
      final Replaceable... replaceables) {
    TitleInfo.subtitle(msg, replaceables).send(p);
  }

  public static void sendSubtitle(
      final Player p,
      final String msg,
      final int fadeIn,
      final int stay,
      final int fadeOut,
      final Replaceable... replaceables) {
    TitleInfo.subtitle(msg, fadeIn, stay, fadeOut, replaceables).send(p);
  }

  public static class TitleInfo {
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private String title;
    private String subtitle;

    public TitleInfo(final TitleBuilder builder) {
      this(builder.build());
    }

    public TitleInfo(final TitleInfo info) {
      this(info.getTitle(), info.getSubtitle(), null, info.getFadeIn(),
          info.getStay(), info.getFadeOut());
    }

    public TitleInfo(final String title, final Replaceable[] replaceables) {
      this(title, null, replaceables, DEFAULT_FADEIN, DEFAULT_STAY, DEFAULT_FADEOUT);
    }

    public TitleInfo(final String title, final String subtitle,
        final Replaceable[] replaceables) {
      this(title, subtitle, replaceables, DEFAULT_FADEIN, DEFAULT_STAY, DEFAULT_FADEOUT);
    }

    public TitleInfo(final String title, final String subtitle,
        final Replaceable[] replaceables, final int fadeIn,
        final int stay, final int fadeOut) {
      this.fadeIn = MathUtils.normalizeRangeOrThrow(fadeIn, -1, Integer.MAX_VALUE, "Title FadeIn");
      this.stay = MathUtils.normalizeRangeOrThrow(stay, -1, Integer.MAX_VALUE, "Title Stay In");
      this.fadeOut = MathUtils.normalizeRangeOrThrow(fadeOut, -1, Integer.MAX_VALUE, "Title FadeOut");
      this.title = MessageFormatter.format(title, true, false, replaceables);
      this.subtitle = MessageFormatter.format(subtitle, true, false, replaceables);
    }

    public static TitleInfo subtitle(final String subtitle, final Replaceable... replaceables) {
      return new TitleInfo(null, subtitle, replaceables);
    }

    public static TitleInfo subtitle(final String subtitle, final int fadeIn,
        final int stay, final int fadeOut, final Replaceable... replaceables) {
      return new TitleInfo(null, subtitle, replaceables, fadeIn, stay, fadeOut);
    }

    public static TitleInfo title(final String title, final Replaceable... replaceables) {
      return new TitleInfo(title, null, replaceables);
    }

    public static TitleInfo title(final String title, final int fadeIn,
        final int stay, final int fadeOut, final Replaceable... replaceables) {
      return new TitleInfo(title, null, replaceables, fadeIn, stay, fadeOut);
    }

    public static TitleInfo from(final String title, final String subtitle,
        final Replaceable[] replaceables) {
      return new TitleInfo(title, subtitle, replaceables);
    }

    public static TitleInfo from(final String title, final String subtitle, final int fadeIn,
        final int stay, final int fadeOut, final Replaceable[] replaceables) {
      return new TitleInfo(title, subtitle, replaceables, fadeIn, stay, fadeOut);
    }

    public static TitleInfo from(final TitleBuilder builder) {
      return new TitleInfo(builder);
    }

    public int getFadeIn() {
      return fadeIn;
    }

    public void setFadeIn(final int fadeIn) {
      this.fadeIn = MathUtils.normalizeRangeOrThrow(fadeIn, -1, Integer.MAX_VALUE, "Title FadeIn");
    }

    public int getStay() {
      return stay;
    }

    public void setStay(final int stay) {
      this.stay = MathUtils.normalizeRangeOrThrow(stay, -1, Integer.MAX_VALUE, "Title Stay In");
    }

    public int getFadeOut() {
      return fadeOut;
    }

    public void setFadeOut(final int fadeOut) {
      this.fadeOut = MathUtils.normalizeRangeOrThrow(fadeOut, -1, Integer.MAX_VALUE, "Title FadeOut");
    }

    public String getTitle() {
      return title;
    }

    public void setTitle(final String title) {
      this.title = title;
    }

    public String getSubtitle() {
      return subtitle;
    }

    public void setSubtitle(final String subtitle) {
      this.subtitle = subtitle;
    }

    public void send(final Player player) {
      final String tempTitle = MessageFormatter.parsePlaceholder(player, title);
      final String tempSubtitle = MessageFormatter.parsePlaceholder(player, subtitle);
      player.sendTitle(tempTitle, tempSubtitle, fadeIn, stay, fadeOut);
    }

    public void send(final List<Player> players) {
      for (final Player player : players) {
        send(player);
      }
    }

    public void send(final Player[] players) {
      for (final Player player : players) {
        send(player);
      }
    }

    public void broadcast() {
      for (final Player p : Bukkit.getOnlinePlayers()) {
        send(p);
      }
    }

    public int hashCode() {
      int result = 31 + fadeIn;
      result = 31 * result + stay;
      result = 31 * result + fadeOut;
      result = 31 * result + ((title == null) ? 0 : title.hashCode());
      result = 31 * result + ((subtitle == null) ? 0 : subtitle.hashCode());
      return result;
    }

    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      final TitleInfo other = (TitleInfo) obj;
      if (title == null) {
        if (other.title != null)
          return false;
      } else if (!title.equals(other.title))
        return false;
      if (subtitle == null) {
        if (other.subtitle != null)
          return false;
      } else if (!subtitle.equals(other.subtitle))
        return false;
      return fadeIn == other.fadeIn && stay == other.stay && fadeOut == other.fadeOut;
    }

    public String toString() {
      return "TitleInfo [fadeIn=" + fadeIn + ", stay=" + stay + ", fadeOut=" + fadeOut + ", title=" + title
          + ", subtitle=" + subtitle + "]";
    }
  }

  public static class TitleBuilder {
    private int fadeIn;
    private int stay;
    private int fadeOut;
    private String title;
    private String subtitle;
    private Replaceable[] replaceables;

    public TitleBuilder() {
      this.title = "";
      this.subtitle = "";
      this.fadeIn = DEFAULT_FADEIN;
      this.stay = DEFAULT_STAY;
      this.fadeOut = DEFAULT_FADEOUT;
    }

    public TitleBuilder(final String title) {
      this(title, "", DEFAULT_FADEIN, DEFAULT_STAY, DEFAULT_FADEOUT);
    }

    public TitleBuilder(final String title, final String subtitle) {
      this(title, subtitle, DEFAULT_FADEIN, DEFAULT_STAY, DEFAULT_FADEOUT);
    }

    public TitleBuilder(final String title, final String subtitle, final int fadeIn, final int stay,
        final int fadeOut) {
      this.title = title;
      this.subtitle = subtitle;
      this.fadeIn = fadeIn;
      this.stay = stay;
      this.fadeOut = fadeOut;
    }

    public int fadeIn() {
      return fadeIn;
    }

    public int stay() {
      return stay;
    }

    public int fadeOut() {
      return fadeOut;
    }

    public String title() {
      return title;
    }

    public String subtitle() {
      return subtitle;
    }

    public TitleBuilder title(final String title) {
      this.title = title;
      return this;
    }

    public TitleBuilder subtitle(final String subtitle) {
      this.subtitle = subtitle;
      return this;
    }

    public TitleBuilder title(final String title, final Replaceable[] replaceables) {
      this.title = title;
      this.replaceables = replaceables;
      return this;
    }

    public TitleBuilder subtitle(final String subtitle, final Replaceable[] replaceables) {
      this.subtitle = subtitle;
      this.replaceables = replaceables;
      return this;
    }

    public TitleBuilder fadeIn(final int duration) {
      this.fadeIn = duration;
      return this;
    }

    public TitleBuilder stay(final int duration) {
      this.stay = duration;
      return this;
    }

    public TitleBuilder fadeOut(final int duration) {
      this.fadeOut = duration;
      return this;
    }

    public TitleBuilder fadeIn(final Duration duration) {
      this.fadeIn = Ticks.fromDuration(duration);
      return this;
    }

    public TitleBuilder stay(final Duration duration) {
      this.stay = Ticks.fromDuration(duration);
      return this;
    }

    public TitleBuilder fadeOut(final Duration duration) {
      this.fadeOut = Ticks.fromDuration(duration);
      return this;
    }

    public TitleInfo build() {
      return new TitleInfo(title, subtitle, replaceables, fadeIn, stay, fadeOut);
    }

    public int hashCode() {
      int result = 31 + fadeIn;
      result = 31 * result + stay;
      result = 31 * result + fadeOut;
      result = 31 * result + ((title == null) ? 0 : title.hashCode());
      result = 31 * result + ((subtitle == null) ? 0 : subtitle.hashCode());
      result = 31 * result + Arrays.hashCode(replaceables);
      return result;
    }

    public boolean equals(final Object obj) {
      if (this == obj)
        return true;
      if (obj == null || getClass() != obj.getClass())
        return false;
      final TitleBuilder other = (TitleBuilder) obj;
      if (fadeIn != other.fadeIn)
        return false;
      if (stay != other.stay)
        return false;
      if (fadeOut != other.fadeOut)
        return false;
      if (title == null) {
        if (other.title != null)
          return false;
      } else if (!title.equals(other.title))
        return false;
      if (subtitle == null) {
        if (other.subtitle != null)
          return false;
      } else if (!subtitle.equals(other.subtitle))
        return false;
      if (!Arrays.equals(replaceables, other.replaceables))
        return false;
      return true;
    }

    public String toString() {
      return "TitleBuilder [fadeIn=" + fadeIn + ", stay=" + stay + ", fadeOut=" + fadeOut + ", title=" + title
          + ", subtitle=" + subtitle
          + ", replaceables=" + Arrays.toString(replaceables) + "]";
    }

  }
}
