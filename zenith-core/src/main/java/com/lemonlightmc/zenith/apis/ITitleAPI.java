package com.lemonlightmc.zenith.apis;

import java.time.Duration;
import java.util.List;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.interfaces.Builder;
import com.lemonlightmc.zenith.utils.StringUtils.Replaceable;

public interface ITitleAPI {

  public static interface ITitleInfo {
    public int getFadeIn();

    public void setFadeIn(final int fadeIn);

    public int getStay();

    public void setStay(final int stay);

    public int getFadeOut();

    public void setFadeOut(final int fadeOut);

    public String getTitle();

    public void setTitle(final String title);

    public String getSubtitle();

    public void setSubtitle(final String subtitle);

    public void send(final Player player);

    public void send(final List<Player> players);

    public void send(final Player[] players);

    public void broadcast();

    @Override
    public int hashCode();

    @Override
    public boolean equals(final Object obj);

    @Override
    public String toString();
  }

  public static interface ITitleBuilder extends Builder<ITitleInfo> {

    public String title();

    public ITitleBuilder title(String title);

    public ITitleBuilder title(String title, final Replaceable[] replaceable);

    public String subtitle();

    public ITitleBuilder subtitle(String subtitle);

    public ITitleBuilder subtitle(String subtitle, final Replaceable[] replaceable);

    public int fadeIn();

    public ITitleBuilder fadeIn(int duration);

    public ITitleBuilder fadeIn(Duration duration);

    public int stay();

    public ITitleBuilder stay(int duration);

    public ITitleBuilder stay(Duration duration);

    public int fadeOut();

    public ITitleBuilder fadeOut(int duration);

    public ITitleBuilder fadeOut(Duration duration);

    public int hashCode();

    public boolean equals(Object obj);

    public String toString();
  }
}