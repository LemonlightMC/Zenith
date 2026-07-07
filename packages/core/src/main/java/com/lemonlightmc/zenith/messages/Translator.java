package com.lemonlightmc.zenith.messages;

import java.util.Locale;

import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.version.Version;

public interface Translator {

  public Locale locale();

  public Version version();

  public String author();

  public String translate(final String key);

  public boolean canTranslate(final String key);

  default public boolean canTranslate(final String key, final Player player) {
    return canTranslate(key);
  }

  default public String translate(final String key, final Player player) {
    return translate(key);
  }

}
