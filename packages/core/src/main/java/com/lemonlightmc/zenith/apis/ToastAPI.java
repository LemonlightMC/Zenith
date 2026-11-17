package com.lemonlightmc.zenith.apis;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.bukkit.entity.Player;

/**
 * Provides helper methods for building and displaying toast-style notifications.
 *
 * <p>The current implementation acts as a placeholder API surface and does not yet render toasts.
 */
// TODO: Implement properly
public class ToastAPI {

  /**
   * Defines the visual frame style used for a toast notification.
   */
  public enum ToastType {
    /** The standard task-style toast frame. */
    TASK,
    /** The goal-style toast frame. */
    GOAL,
    /** The challenge-style toast frame. */
    CHALLENGE;

    /**
     * Returns the lowercase name of this toast type.
     *
     * @return the lowercase toast type name
     */
    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }
  }

  /**
   * Creates a new toast builder.
   *
   * @return a new toast builder
   */
  public static ToastBuilder builder() {
    return new ToastBuilder();
  }

  /**
   * Displays a toast using the provided builder configuration.
   *
   * @param builder the toast configuration to display
   */
  public static void show(final ToastBuilder builder) {

  }

  /**
   * Broadcasts a toast using the provided builder configuration.
   *
   * @param builder the toast configuration to broadcast
   */
  public static void broadcast(final ToastBuilder builder) {

  }

  /**
   * Displays a toast to a collection of players.
   *
   * @param players the players who should receive the toast
   * @param icon the icon identifier for the toast
   * @param message the toast message
   * @param style the toast frame style
   * @param modelData optional model data for the icon
   * @param glowing whether the icon should appear glowing
   */
  public static void show(final Collection<? extends Player> players, final String icon, final String message,
      final ToastType style,
      final Object modelData, final boolean glowing) {
    show(builder()
        .withIcon(icon)
        .withMessage(message)
        .withStyle(style)
        .withModelData(modelData)
        .setGlowing(glowing)
        .to(players));
  }

  /**
   * Displays a toast to an array of players.
   *
   * @param players the players who should receive the toast
   * @param icon the icon identifier for the toast
   * @param message the toast message
   * @param style the toast frame style
   * @param modelData optional model data for the icon
   * @param glowing whether the icon should appear glowing
   */
  public static void show(final Player[] players, final String icon, final String message, final ToastType style,
      final Object modelData,
      final boolean glowing) {
    show(List.of(players), icon, message, style, modelData, glowing);
  }

  /**
   * Broadcasts a toast to all players using the provided properties.
   *
   * @param icon the icon identifier for the toast
   * @param message the toast message
   * @param style the toast frame style
   * @param modelData optional model data for the icon
   * @param glowing whether the icon should appear glowing
   */
  public static void broadcast(final String icon, final String message, final ToastType style, final Object modelData,
      final boolean glowing) {
    show(builder()
        .withIcon(icon)
        .withMessage(message)
        .withStyle(style)
        .withModelData(modelData)
        .setGlowing(glowing)
        .toAll());
  }

  /**
   * Builder for configuring toast notifications before display.
   */
  public static class ToastBuilder {
    /** The icon identifier used for the toast. */
    private String icon = "paper";
    /** The message displayed by the toast. */
    private String message = "Toast Message";
    /** The visual style of the toast. */
    private ToastType style = ToastType.TASK;
    /** Optional custom model data for the toast icon. */
    private Object modelData;
    /** Whether the toast icon should appear glowing. */
    private boolean glowing = false;
    /** The target players for the toast, if not broadcasting to all players. */
    private Collection<? extends Player> players;
    /** Whether the toast should be sent to all online players. */
    private boolean isToAll = false;
    /** The inferred or explicit custom model data type. */
    private String modelDataType = null;

    /**
     * Sets the target players for this toast.
     *
     * @param players the players who should receive the toast
     * @return this builder
     */
    public ToastBuilder to(final Collection<? extends Player> players) {
      this.players = players;
      this.isToAll = false;
      return this;
    }

    /**
     * Sets the target players for this toast.
     *
     * @param players the players who should receive the toast
     * @return this builder
     */
    public ToastBuilder to(final Player... players) {
      this.players = List.of(players);
      this.isToAll = false;
      return this;
    }

    /**
     * Configures this toast to target all online players.
     *
     * @return this builder
     */
    public ToastBuilder toAll() {
      this.isToAll = true;
      return this;
    }

    /**
     * Sets the icon identifier for this toast.
     *
     * @param icon the icon identifier
     * @return this builder
     */
    public ToastBuilder withIcon(final String icon) {
      this.icon = icon;
      return this;
    }

    /**
     * Sets the message displayed by this toast.
     *
     * @param message the toast message
     * @return this builder
     */
    public ToastBuilder withMessage(final String message) {
      this.message = message;
      return this;
    }

    /**
     * Sets the visual style of this toast.
     *
     * @param style the toast style
     * @return this builder
     */
    public ToastBuilder withStyle(final ToastType style) {
      this.style = style;
      return this;
    }

    /**
     * Sets whether the toast icon should appear glowing.
     *
     * @param glowing {@code true} to enable the glowing effect; {@code false} otherwise
     * @return this builder
     */
    public ToastBuilder setGlowing(final boolean glowing) {
      this.glowing = glowing;
      return this;
    }

    /**
     * Sets custom model data for the toast icon and infers its type.
     *
     * @param modelData the custom model data value
     * @return this builder
     */
    public ToastBuilder withModelData(final Object modelData) {
      this.modelData = modelData;

      if (modelData instanceof String) {
        this.modelDataType = "string";
      } else if (modelData instanceof Float || modelData instanceof Double) {
        this.modelDataType = "float";
      } else {
        this.modelDataType = "integer";
      }
      return this;
    }

    /**
     * Sets custom model data for the toast icon with an explicit type.
     *
     * @param modelData the custom model data value
     * @param modelDataType the custom model data type identifier
     * @return this builder
     */
    public ToastBuilder withModelData(final Object modelData, final String modelDataType) {
      this.modelData = modelData;
      this.modelDataType = modelDataType;
      return this;
    }

    /**
     * Displays the configured toast using the current target settings.
     *
     * @return this builder
     * @throws IllegalStateException if no target players are configured for a non-broadcast toast
     */
    public ToastBuilder show() {
      if (isToAll) {
        ToastAPI.broadcast(this);
      } else {
        if (players == null || players.isEmpty()) {
          throw new IllegalStateException("[ERROR TOAST POP-UP] No player found!");
        }
        ToastAPI.show(this);
      }
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
      int result = 31 + ((icon == null) ? 0 : icon.hashCode());
      result = 31 * result + ((message == null) ? 0 : message.hashCode());
      result = 31 * result + ((style == null) ? 0 : style.hashCode());
      result = 31 * result + ((modelData == null) ? 0 : modelData.hashCode());
      result = 31 * result + ((modelDataType == null) ? 0 : modelDataType.hashCode());
      result = 31 * result + ((players == null) ? 0 : players.hashCode());
      result = 31 * result + (glowing ? 1231 : 1237);
      return 31 * result + (isToAll ? 1231 : 1237);
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
      final ToastBuilder other = (ToastBuilder) obj;
      return Objects.equals(message, other.message)
          && Objects.equals(icon, other.icon)
          && isToAll == other.isToAll && glowing == other.glowing
          && Objects.equals(style, other.style)
          && Objects.equals(modelData, other.modelData) && Objects.equals(modelDataType, other.modelDataType)
          && Objects.equals(players, other.players);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
      return "ToastBuilder [icon=" + icon + ", message=" + message + ", style=" + style + ", modelData=" + modelData
          + ", glowing=" + glowing + ", players=" + players + ", isToAll=" + isToAll + ", modelDataType="
          + modelDataType + "]";
    }

    /**
     * Returns the configured icon identifier.
     *
     * @return the icon identifier
     */
    public String getIcon() {
      return icon;
    }

    /**
     * Returns the configured toast message.
     *
     * @return the toast message
     */
    public String getMessage() {
      return message;
    }

    /**
     * Returns the configured toast style.
     *
     * @return the toast style
     */
    public ToastType getStyle() {
      return style;
    }

    /**
     * Returns the configured custom model data.
     *
     * @return the custom model data value
     */
    public Object getModelData() {
      return modelData;
    }

    /**
     * Returns whether the toast icon is configured as glowing.
     *
     * @return {@code true} if glowing is enabled; {@code false} otherwise
     */
    public Boolean getGlowing() {
      return glowing;
    }

    /**
     * Returns the configured target players.
     *
     * @return the target players, or {@code null} if none are set
     */
    public Collection<? extends Player> getPlayers() {
      return players;
    }

    /**
     * Returns whether this toast targets all online players.
     *
     * @return {@code true} if the toast is configured for broadcast; {@code false} otherwise
     */
    public boolean isToAll() {
      return isToAll;
    }

    /**
     * Returns the configured custom model data type.
     *
     * @return the custom model data type identifier
     */
    public String getModelDataType() {
      return modelDataType;
    }
  }
}
