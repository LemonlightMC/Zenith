package com.lemonlightmc.zenith.modular;

import java.util.List;

import com.lemonlightmc.zenith.interfaces.Reloadable;

public interface IModule extends Reloadable {

  public String getKey();

  public List<String> getDepends();

  public List<String> getSoftDepends();

  public boolean isEnabled();

  public void enable();

  public void reload();

  public void disable();

  public void onEnable();

  public void onReload();

  public void onDisable();

  public int hashCode();

  public String toString();

  public boolean equals(Object module);
}
