package com.lemonlightmc.zenith.v2.schema;

import java.util.StringJoiner;

public interface Table {
  String getName();

  Attribute[] getAttributes();

  default String getTableAttributes() {
    final StringJoiner joiner = new StringJoiner(",");
    for (final Attribute attribute : getAttributes()) {
      joiner.add(attribute.getDefinition());
    }
    return joiner.toString();
  }
}