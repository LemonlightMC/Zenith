package com.lemonlightmc.zenith.items;

public interface CustomItemProperty {

  public default String id() {
    return name().replaceAll(" ", "").toLowerCase();
  }

  public String name();

}
