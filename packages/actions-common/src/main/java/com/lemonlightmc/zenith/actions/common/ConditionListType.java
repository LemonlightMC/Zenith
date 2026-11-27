package com.lemonlightmc.zenith.actions.common;

/**
 * Defines whether a condition list acts as a whitelist or blacklist.
 */
public enum ConditionListType {
  WHITELIST(false),
  BLACKLIST(true);

  private final boolean negate;

  private ConditionListType(final boolean negate) {
    this.negate = negate;
  }

  public boolean apply(final boolean value) {
    return negate ? !value : value;
  }
}
