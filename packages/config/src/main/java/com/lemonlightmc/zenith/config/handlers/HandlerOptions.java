package com.lemonlightmc.zenith.config.handlers;

import java.util.List;
import java.util.Objects;

import com.lemonlightmc.zenith.additive.interfaces.Cloneable;
import com.lemonlightmc.zenith.config.Configurate;

/**
 * Configures formatting and parsing behavior for file handlers.
 */
public class HandlerOptions implements Cloneable<HandlerOptions> {
  /** Header text written to or expected from handled files. */
  protected String header = null;
  /** Footer text written to or expected from handled files. */
  protected String footer = null;

  /** Whether comments should be parsed and preserved when supported. */
  protected boolean parseComments = true;

  /** Whether tab characters should be converted to spaces. */
  protected boolean convertTabs = true;
  /** The number of spaces to use when converting tabs. */
  protected int tabSpaces = 2;
  /** Whether quotes should be normalized when supported. */
  protected boolean replaceQuotes = true;
  /** Whether double quotes should be preferred when replacing quotes. */
  protected boolean preferDoubleQuotes = true;

  /**
   * Creates handler options initialized from the global {@link Configurate} defaults.
   */
  public HandlerOptions() {
    this.header = Configurate.options().header;
    this.footer = Configurate.options().footer;
    this.parseComments = Configurate.options().parseComments;
    this.convertTabs = Configurate.options().convertTabs;
    this.tabSpaces = Configurate.options().tabSpaces;
    this.replaceQuotes = Configurate.options().replaceQuotes;
    this.preferDoubleQuotes = Configurate.options().preferDoubleQuotes;
  }

  /**
   * Creates handler options copied from another instance.
   *
   * @param options the options to copy
   */
  public HandlerOptions(final HandlerOptions options) {
    this.header = options.header;
    this.footer = options.footer;

    this.parseComments = options.parseComments;

    this.convertTabs = options.convertTabs;
    this.tabSpaces = options.tabSpaces;
    this.replaceQuotes = options.replaceQuotes;
    this.preferDoubleQuotes = options.preferDoubleQuotes;
  }

  /**
   * Creates a new handler options instance with default values.
   *
   * @return a new {@link HandlerOptions} instance
   */
  public static HandlerOptions create() {
    return new HandlerOptions();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HandlerOptions clone() {
    return new HandlerOptions(this);
  }

  /**
   * Returns the configured header text.
   *
   * @return the header text, or {@code null} if none is configured
   */
  public String header() {
    return header;
  }

  /**
   * Sets the configured header text from multiple lines.
   *
   * @param header the header lines to join with newlines
   * @return this options instance for chaining
   */
  public HandlerOptions header(final List<String> header) {
    this.header = header == null ? null : String.join("\n", header);
    return this;
  }

  /**
   * Sets the configured header text.
   *
   * @param header the header text
   * @return this options instance for chaining
   */
  public HandlerOptions header(final String header) {
    this.header = header;
    return this;
  }

  /**
   * Sets the configured footer text from multiple lines.
   *
   * @param footer the footer lines to join with newlines
   * @return this options instance for chaining
   */
  public HandlerOptions footer(final List<String> footer) {
    this.footer = footer == null ? null : String.join("\n", footer);
    return this;
  }

  /**
   * Sets the configured footer text.
   *
   * @param footer the footer text
   * @return this options instance for chaining
   */
  public HandlerOptions footer(final String footer) {
    this.footer = footer;
    return this;
  }

  /**
   * Returns the configured footer text.
   *
   * @return the footer text, or {@code null} if none is configured
   */
  public String footer() {
    return footer;
  }

  /**
   * Returns whether comments should be parsed.
   *
   * @return {@code true} if comments should be parsed; {@code false} otherwise
   */
  public boolean parseComments() {
    return parseComments;
  }

  /**
   * Sets whether comments should be parsed.
   *
   * @param parseComments {@code true} to parse comments; {@code false} otherwise
   * @return this options instance for chaining
   */
  public HandlerOptions parseComments(final boolean parseComments) {
    this.parseComments = parseComments;
    return this;
  }

  /**
   * Returns whether tabs should be converted to spaces.
   *
   * @return {@code true} if tabs should be converted; {@code false} otherwise
   */
  public boolean convertTabs() {
    return convertTabs;
  }

  /**
   * Sets whether tabs should be converted to spaces.
   *
   * @param value {@code true} to convert tabs; {@code false} otherwise
   * @return this options instance for chaining
   */
  public HandlerOptions convertTabs(final boolean value) {
    this.convertTabs = value;
    return this;
  }

  /**
   * Returns the number of spaces used when converting tabs.
   *
   * @return the configured tab width in spaces
   */
  public int tabSpaces() {
    return tabSpaces;
  }

  /**
   * Sets the number of spaces used when converting tabs.
   *
   * @param value the requested tab width in spaces
   * @return this options instance for chaining
   */
  public HandlerOptions tabSpaces(final int value) {
    this.tabSpaces = Math.min(Math.max(value, 1), 8);
    return this;
  }

  /**
   * Returns whether quotes should be normalized.
   *
   * @return {@code true} if quotes should be replaced; {@code false} otherwise
   */
  public boolean replaceQuotes() {
    return replaceQuotes;
  }

  /**
   * Sets whether quotes should be normalized.
   *
   * @param value {@code true} to replace quotes; {@code false} otherwise
   * @return this options instance for chaining
   */
  public HandlerOptions replaceQuotes(final boolean value) {
    this.replaceQuotes = value;
    return this;
  }

  /**
   * Returns whether double quotes are preferred when replacing quotes.
   *
   * @return {@code true} if double quotes are preferred; {@code false} otherwise
   */
  public boolean preferDoubleQuotes() {
    return preferDoubleQuotes;
  }

  /**
   * Sets whether double quotes are preferred when replacing quotes.
   *
   * @param value {@code true} to prefer double quotes; {@code false} otherwise
   * @return this options instance for chaining
   */
  public HandlerOptions preferDoubleQuotes(final boolean value) {
    this.preferDoubleQuotes = value;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 31 + ((header == null) ? 0 : header.hashCode());
    result = 31 * result + ((footer == null) ? 0 : footer.hashCode());
    result = 31 * result + (parseComments ? 1231 : 1237);
    result = 31 * result + (convertTabs ? 1231 : 1237);
    result = 31 * result + tabSpaces;
    result = 31 * result + (replaceQuotes ? 1231 : 1237);
    return 31 * result + (preferDoubleQuotes ? 1231 : 1237);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final HandlerOptions other = (HandlerOptions) obj;
    return parseComments == other.parseComments
        && replaceQuotes == other.replaceQuotes
        && convertTabs == other.convertTabs
        && tabSpaces == other.tabSpaces
        && preferDoubleQuotes == other.preferDoubleQuotes
        && Objects.equals(footer, other.footer)
        && Objects.equals(header, other.header);
  }
}
