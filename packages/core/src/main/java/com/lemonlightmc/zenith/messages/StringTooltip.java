package com.lemonlightmc.zenith.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public record StringTooltip(String message, String tooltip) {

  public TextComponent resolve() {
    TextComponent component = new TextComponent(message);
    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(tooltip)));
    return component;
  }

  public static StringTooltip none(final String suggestion) {
    return new StringTooltip(suggestion, null);
  }

  public static Collection<StringTooltip> none(final String... suggestions) {
    return of(suggestions);
  }

  public static Collection<StringTooltip> none(final Collection<String> suggestions) {
    return of(suggestions);
  }

  public static StringTooltip of(final String suggestion) {
    return new StringTooltip(suggestion, null);
  }

  public static Collection<StringTooltip> of(final String... suggestions) {
    final Collection<StringTooltip> list = new ArrayList<>();
    for (final String str : suggestions) {
      list.add(StringTooltip.none(str));
    }
    return list;
  }

  public static Collection<StringTooltip> of(final Collection<String> suggestions) {
    final Collection<StringTooltip> list = new ArrayList<>();
    for (final String str : suggestions) {
      list.add(StringTooltip.none(str));
    }
    return list;
  }

  public static StringTooltip of(final String suggestion, final String tooltip) {
    return tooltip == null ? new StringTooltip(suggestion, null) : new StringTooltip(suggestion, tooltip);
  }

  public static StringTooltip of(final String suggestion, final BaseComponent tooltip) {
    return tooltip == null ? new StringTooltip(suggestion, null) : new StringTooltip(suggestion, tooltip.toString());
  }

  public static StringTooltip of(final String suggestion, final BaseComponent... components) {
    return of(suggestion, new TextComponent(components));
  }

  public static Collection<StringTooltip> of(final Function<String, String> tooltipGenerator,
      final String... suggestions) {
    final Collection<StringTooltip> list = new ArrayList<>();
    for (final String str : suggestions) {
      list.add(StringTooltip.of(str, tooltipGenerator.apply(str)));
    }
    return list;
  }

  public static Collection<StringTooltip> of(final Function<String, String> tooltipGenerator,
      final Collection<String> suggestions) {
    final Collection<StringTooltip> list = new ArrayList<>();
    for (final String str : suggestions) {
      list.add(StringTooltip.of(str, tooltipGenerator.apply(str)));
    }
    return list;
  }

}
