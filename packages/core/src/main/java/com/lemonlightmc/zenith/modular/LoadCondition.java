package com.lemonlightmc.zenith.modular;

import java.util.Optional;

import com.lemonlightmc.zenith.utils.Plugins;

public class LoadCondition {

  private static final LoadCondition SUCCESS = new LoadCondition(true, null);

  private final boolean success;
  private final String reason;

  private LoadCondition(boolean success, String reason) {
    this.success = success;
    this.reason = reason;
  }

  public static LoadCondition success() {
    return SUCCESS;
  }

  public static LoadCondition failure(String reason) {
    return new LoadCondition(false, reason);
  }

  public static LoadCondition packetLibrary() {
    return Plugins.hasPacketLibrary() ? LoadCondition.success()
        : LoadCondition.failure("No packet library plugin installed. Install %s or %s for the module to work."
            .formatted("packetevents", "ProtocolLib"));
  }

  public boolean isSuccess() {
    return this.success;
  }

  public Optional<String> reason() {
    return Optional.ofNullable(this.reason);
  }
}
