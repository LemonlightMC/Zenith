package com.lemonlightmc.zenith.config.handlers;

import java.util.List;

import com.lemonlightmc.zenith.config.Configurate;
import com.lemonlightmc.zenith.interfaces.Cloneable;

public class HandlerOptions implements Cloneable<HandlerOptions> {
  protected String header = null;
  protected String footer = null;

  protected boolean parseComments = true;

  protected boolean convertTabs = true;
  protected int tabSpaces = 2;
  protected boolean replaceQuotes = true;
  protected boolean preferDoubleQuotes = true;

  public HandlerOptions() {
    this.header = Configurate.options().header;
    this.footer = Configurate.options().footer;
    this.parseComments = Configurate.options().parseComments;
    this.convertTabs = Configurate.options().convertTabs;
    this.tabSpaces = Configurate.options().tabSpaces;
    this.replaceQuotes = Configurate.options().replaceQuotes;
    this.preferDoubleQuotes = Configurate.options().preferDoubleQuotes;
  }

  public HandlerOptions(final HandlerOptions options) {
    this.header = options.header;
    this.footer = options.footer;

    this.parseComments = options.parseComments;

    this.convertTabs = options.convertTabs;
    this.tabSpaces = options.tabSpaces;
    this.replaceQuotes = options.replaceQuotes;
    this.preferDoubleQuotes = options.preferDoubleQuotes;
  }

  public static HandlerOptions create() {
    return new HandlerOptions();
  }

  @Override
  public HandlerOptions clone() {
    return new HandlerOptions(this);
  }

  public String header() {
    return header;
  }

  public HandlerOptions header(final List<String> header) {
    this.header = header == null ? null : String.join("\n", header);
    return this;
  }

  public HandlerOptions header(final String header) {
    this.header = header;
    return this;
  }

  public HandlerOptions footer(final List<String> footer) {
    this.footer = footer == null ? null : String.join("\n", footer);
    return this;
  }

  public HandlerOptions footer(final String footer) {
    this.footer = footer;
    return this;
  }

  public String footer() {
    return footer;
  }

  public boolean parseComments() {
    return parseComments;
  }

  public HandlerOptions parseComments(final boolean parseComments) {
    this.parseComments = parseComments;
    return this;
  }

  public boolean convertTabs() {
    return convertTabs;
  }

  public HandlerOptions convertTabs(final boolean value) {
    this.convertTabs = value;
    return this;
  }

  public int tabSpaces() {
    return tabSpaces;
  }

  public HandlerOptions tabSpaces(final int value) {
    this.tabSpaces = Math.min(Math.max(value, 1), 8);
    return this;
  }

  public boolean replaceQuotes() {
    return replaceQuotes;
  }

  public HandlerOptions replaceQuotes(final boolean value) {
    this.replaceQuotes = value;
    return this;
  }

  public boolean preferDoubleQuotes() {
    return preferDoubleQuotes;
  }

  public HandlerOptions preferDoubleQuotes(final boolean value) {
    this.preferDoubleQuotes = value;
    return this;
  }

  @Override
  public int hashCode() {
    int result = 31 + ((header == null) ? 0 : header.hashCode());
    result = 31 * result + ((footer == null) ? 0 : footer.hashCode());
    result = 31 * result + (parseComments ? 1231 : 1237);
    result = 31 * result + (convertTabs ? 1231 : 1237);
    result = 31 * result + tabSpaces;
    result = 31 * result + (replaceQuotes ? 1231 : 1237);
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final HandlerOptions other = (HandlerOptions) obj;
    if (header == null && other.header != null || footer == null && other.footer != null) {
      return false;
    }
    return parseComments != other.parseComments
        && replaceQuotes != other.replaceQuotes
        && convertTabs != other.convertTabs
        && tabSpaces != other.tabSpaces
        && footer.equals(other.footer) && header.equals(other.header);
  }
}
