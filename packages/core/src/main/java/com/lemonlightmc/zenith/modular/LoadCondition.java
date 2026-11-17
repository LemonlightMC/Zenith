package com.lemonlightmc.zenith.modular;

import java.util.Optional;

import com.lemonlightmc.zenith.utils.Plugins;

/**
 * Represents whether a module can be loaded, optionally providing a failure reason.
 */
public class LoadCondition {

  private static final LoadCondition SUCCESS = new LoadCondition(true, null);

  private final boolean success;
  private final String reason;

  /**
   * Creates a new load condition result.
   *
   * @param success whether loading is allowed
   * @param reason the failure reason, or {@code null} on success
   */
  private LoadCondition(boolean success, String reason) {
    this.success = success;
    this.reason = reason;
  }

  /**
   * Creates a successful load condition.
   *
   * @return a successful condition
   */
  public static LoadCondition success() {
    return SUCCESS;
  }

  /**
   * Creates a failed load condition with the given reason.
   *
   * @param reason the reason loading cannot proceed
   * @return a failed condition
   */
  public static LoadCondition failure(String reason) {
    return new LoadCondition(false, reason);
  }

  /**
   * Creates a load condition that requires an available packet library integration.
   *
   * @return a successful condition if a packet library is available; otherwise a failed condition
   */
  public static LoadCondition packetLibrary() {
    return Plugins.hasPacketLibrary() ? LoadCondition.success()
        : LoadCondition.failure("No packet library plugin installed. Install %s or %s for the module to work."
            .formatted("packetevents", "ProtocolLib"));
  }

  /**
   * Checks whether loading is allowed.
   *
   * @return {@code true} if loading can proceed; {@code false} otherwise
   */
  public boolean isSuccess() {
    return this.success;
  }

  /**
   * Gets the failure reason, if any.
   *
   * @return an optional containing the failure reason
   */
  public Optional<String> reason() {
    return Optional.ofNullable(this.reason);
  }
}
