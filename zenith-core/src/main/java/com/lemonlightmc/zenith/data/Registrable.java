package com.lemonlightmc.zenith.data;

import org.bukkit.NamespacedKey;

public interface Registrable {

  public NamespacedKey getKey();

  public boolean isRegistered();

  public default void onRegister() {
  }

  public default void onRemove() {
  }
}
