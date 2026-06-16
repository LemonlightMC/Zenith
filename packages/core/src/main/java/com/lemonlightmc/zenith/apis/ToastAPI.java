package com.lemonlightmc.zenith.apis;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.bukkit.entity.Player;

// TODO: Implement properly
public class ToastAPI {

  public static enum ToastType {
    TASK,
    GOAL,
    CHALLENGE;

    @Override
    public String toString() {
      return super.toString().toLowerCase();
    }
  }

  public static ToastBuilder builder() {
    return new ToastBuilder();
  }

  public static void show(final ToastBuilder builder) {

  }

  public static void broadcast(final ToastBuilder builder) {

  }

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

  public static void show(final Player[] players, final String icon, final String message, final ToastType style,
      final Object modelData,
      final boolean glowing) {
    show(List.of(players), icon, message, style, modelData, glowing);
  }

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

  public static class ToastBuilder {
    private String icon = "paper";
    private String message = "Toast Message";
    private ToastType style = ToastType.TASK;
    private Object modelData;
    private Boolean glowing = false;
    private Collection<? extends Player> players;
    private boolean isToAll = false;
    private String modelDataType = null;

    public ToastBuilder to(final Collection<? extends Player> players) {
      this.players = players;
      this.isToAll = false;
      return this;
    }

    public ToastBuilder to(final Player... players) {
      this.players = List.of(players);
      this.isToAll = false;
      return this;
    }

    public ToastBuilder toAll() {
      this.isToAll = true;
      return this;
    }

    public ToastBuilder withIcon(final String icon) {
      this.icon = icon;
      return this;
    }

    public ToastBuilder withMessage(final String message) {
      this.message = message;
      return this;
    }

    public ToastBuilder withStyle(final ToastType style) {
      this.style = style;
      return this;
    }

    public ToastBuilder setGlowing(final boolean glowing) {
      this.glowing = glowing;
      return this;
    }

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

    public ToastBuilder withModelData(final Object modelData, final String modelDataType) {
      this.modelData = modelData;
      this.modelDataType = modelDataType;
      return this;
    }

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

    @Override
    public int hashCode() {
      int result = 31 + ((icon == null) ? 0 : icon.hashCode());
      result = 31 * result + ((message == null) ? 0 : message.hashCode());
      result = 31 * result + ((style == null) ? 0 : style.hashCode());
      result = 31 * result + ((glowing == null) ? 0 : glowing.hashCode());
      result = 31 * result + ((modelData == null) ? 0 : modelData.hashCode());
      result = 31 * result + ((modelDataType == null) ? 0 : modelDataType.hashCode());
      result = 31 * result + ((players == null) ? 0 : players.hashCode());
      return 31 * result + (isToAll ? 1231 : 1237);
    }

    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      final ToastBuilder other = (ToastBuilder) obj;
      return Objects.equals(icon, other.icon) && Objects.equals(message, other.message)
          && style == other.style && Objects.equals(glowing, other.glowing)
          && Objects.equals(modelData, other.modelData) && Objects.equals(modelDataType, other.modelDataType)
          && Objects.equals(players, other.players) && isToAll == other.isToAll;
    }

    @Override
    public String toString() {
      return "ToastBuilder [icon=" + icon + ", message=" + message + ", style=" + style + ", modelData=" + modelData
          + ", glowing=" + glowing + ", players=" + players + ", isToAll=" + isToAll + ", modelDataType="
          + modelDataType + "]";
    }

    public String getIcon() {
      return icon;
    }

    public String getMessage() {
      return message;
    }

    public ToastType getStyle() {
      return style;
    }

    public Object getModelData() {
      return modelData;
    }

    public Boolean getGlowing() {
      return glowing;
    }

    public Collection<? extends Player> getPlayers() {
      return players;
    }

    public boolean isToAll() {
      return isToAll;
    }

    public String getModelDataType() {
      return modelDataType;
    }
  }
}
