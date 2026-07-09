package com.lemonlightmc.zenith.apis;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import com.lemonlightmc.zenith.ZenithProvider;
import com.lemonlightmc.zenith.additive.math.NumberConversions;
import com.lemonlightmc.zenith.apis.ToastAPI.ToastType;
import com.lemonlightmc.zenith.exceptions.OutdatedVersionError;
import com.lemonlightmc.zenith.messages.MessageFormatter;
import com.lemonlightmc.zenith.version.MCVersion;

public class AdvancementAPI {
  @SuppressWarnings("deprecation")
  void showToast(Collection<? extends Player> players, String icon, String message, ToastType style, Object modelData,
      String modelDataType, boolean glowing) {
    NamespacedKey advancementKey = createAdvancement(icon, message, style, modelData, modelDataType, glowing);

    for (Player p : players) {
      ZenithProvider.instance().getScheduler().runLater(() -> {
        grantAdvancement(p, advancementKey);
        ZenithProvider.instance().getScheduler().runLater(() -> revokeAdvancement(p, advancementKey), 10);
      }, 1);
    }

    ZenithProvider.instance().getScheduler().runLater(() -> Bukkit.getUnsafe().removeAdvancement(advancementKey),
        40);
  }

  /**
   * Show a popup (advancement toast) to all online players.
   *
   * @param icon          the icon material name (minecraft id) used for the toast
   * @param message       the message (already formatted as json component text)
   * @param style         the toast frame style
   * @param modelData     optional model data for the icon
   * @param modelDataType optional model data type string
   * @param glowing       whether the icon should glow
   */
  @SuppressWarnings("deprecation")
  void showToastToAll(String icon, String message, ToastType style, Object modelData, String modelDataType,
      boolean glowing) {
    Collection<? extends Player> allPlayers = Bukkit.getOnlinePlayers();

    if (allPlayers.isEmpty()) {
      return;
    }

    NamespacedKey advancementKey = createAdvancement(icon, message, style, modelData, modelDataType, glowing);

    for (Player p : allPlayers) {
      ZenithProvider.instance().getScheduler().runLater(() -> {
        grantAdvancement(p, advancementKey);
        ZenithProvider.instance().getScheduler().runLater(() -> revokeAdvancement(p, advancementKey), 10);
      }, 1);
    }

    ZenithProvider.instance().getScheduler().runLater(() -> Bukkit.getUnsafe().removeAdvancement(advancementKey),
        40);
  }

  @SuppressWarnings("deprecation")
  private static NamespacedKey legacyType(String icon, String message, ToastType style, Object modelData,
      boolean glowing,
      NamespacedKey advancementKey) {

    int modelDataInt = 0;
    if (modelData instanceof Integer) {
      modelDataInt = (Integer) modelData;
    } else if (modelData instanceof Float) {
      modelDataInt = ((Float) modelData).intValue();
    } else if (modelData instanceof String) {
      try {
        modelDataInt = NumberConversions.parseInt(modelData.toString());
      } catch (NumberFormatException ignored) {
        // TODO: If parsing fails, modelDataInt remains 0
      }
    }

    String json = "{\n" +
        " \"criteria\": {\n" +
        " \"trigger\": {\n" +
        " \"trigger\": \"minecraft:impossible\"\n" +
        " }\n" +
        " },\n" +
        " \"display\": {\n" +
        " \"icon\": {\n" +
        " \"item\": \"minecraft:" + icon + "\",\n" +
        " \"nbt\": \"{CustomModelData:" + modelDataInt +
        (glowing ? ",Enchantments:[{lvl:1,id:\\\"minecraft:protection\\\"}]" : "") + "}\"\n" +
        " },\n" +
        " \"title\": " + message + ",\n" +
        " \"description\": {\n" +
        " \"text\": \"\"\n" +
        " },\n" +
        " \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
        " \"frame\": \"" + style.toString().toLowerCase() + "\",\n" +
        " \"announce_to_chat\": false,\n" +
        " \"show_toast\": true,\n" +
        " \"hidden\": true\n" +
        " }\n" +
        "}";

    Bukkit.getUnsafe().loadAdvancement(advancementKey, json);
    return advancementKey;
  }

  @SuppressWarnings("deprecation")
  private static NamespacedKey middleType(String icon, String message, ToastType style, Object modelData,
      boolean glowing,
      NamespacedKey advancementKey) {
    int modelDataInt = 0;
    if (modelData instanceof Integer) {
      modelDataInt = (Integer) modelData;
    } else if (modelData instanceof Float) {
      modelDataInt = ((Float) modelData).intValue();
    } else if (modelData instanceof String) {
      try {
        modelDataInt = NumberConversions.parseInt(modelData.toString());
      } catch (NumberFormatException ignored) {
        // TODO: If parsing fails, modelDataInt remains 0
      }
    }

    String json = "{\n" +
        " \"criteria\": {\n" +
        "   \"trigger\": {\n" +
        "     \"trigger\": \"minecraft:impossible\"\n" +
        "   }\n" +
        " },\n" +
        " \"display\": {\n" +
        "   \"icon\": {\n" +
        "     \"id\": \"minecraft:" + icon + "\",\n" +
        "     \"components\": {\n" +
        "       \"minecraft:custom_model_data\": " + modelDataInt +
        (glowing ? ",\n       \"minecraft:enchantments\": {\n" +
            "         \"levels\": {\n" +
            "           \"minecraft:protection\": 1\n" +
            "         }\n" +
            "       }" : "")
        +
        "\n     },\n" +
        "     \"count\": 1\n" +
        "   },\n" +
        "   \"title\": " + message + ",\n" +
        "   \"description\": {\n" +
        "     \"text\": \"\"\n" +
        "   },\n" +
        "   \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
        "   \"frame\": \"" + style.toString().toLowerCase() + "\",\n" +
        "   \"announce_to_chat\": false,\n" +
        "   \"show_toast\": true,\n" +
        "   \"hidden\": true\n" +
        " }\n" +
        "}";

    Bukkit.getUnsafe().loadAdvancement(advancementKey, json);
    return advancementKey;
  }

  @SuppressWarnings("deprecation")
  private static NamespacedKey modernType(String icon, String message, ToastType style, Object modelData,
      String modelDataType,
      boolean glowing, NamespacedKey advancementKey) {
    String customModelData;
    if (modelDataType == null) {
      modelDataType = modelData instanceof String ? "string"
          : (modelData instanceof Float || modelData instanceof Double) ? "float" : "integer";
    }

    if ("float".equals(modelDataType) || "integer".equals(modelDataType)) {
      customModelData = "\"minecraft:custom_model_data\": {\n" +
          " \"floats\": [" + modelData + "]\n" +
          " }";
    } else {
      customModelData = "\"minecraft:custom_model_data\": {\n" +
          " \"strings\": [\n" +
          " \"" + modelData + "\"\n" +
          " ]\n" +
          " }";
    }

    String json = "{\n" +
        " \"criteria\": {\n" +
        " \"trigger\": {\n" +
        " \"trigger\": \"minecraft:impossible\"\n" +
        " }\n" +
        " },\n" +
        " \"display\": {\n" +
        " \"icon\": {\n" +
        " \"id\": \"minecraft:" + icon + "\",\n" +
        " \"components\": {\n" +
        " " + customModelData +
        (glowing ? ",\n \"minecraft:enchantments\": {\n" +
            " \"minecraft:protection\": 1\n" +
            " }" : "")
        + "\n" +
        " },\n" +
        " \"count\": 1\n" +
        " },\n" +
        " \"title\": " + message + ",\n" +
        " \"description\": {\n" +
        " \"text\": \"\"\n" +
        " },\n" +
        " \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
        " \"frame\": \"" + style.toString().toLowerCase() + "\",\n" +
        " \"announce_to_chat\": false,\n" +
        " \"show_toast\": true,\n" +
        " \"hidden\": true\n" +
        " }\n" +
        "}";

    Bukkit.getUnsafe().loadAdvancement(advancementKey, json);
    return advancementKey;
  }

  private static NamespacedKey createAdvancement(String icon, String message, ToastType style, Object modelData,
      String modelDataType, boolean glowing) {

    message = MessageFormatter.format(message, true, false);

    UUID randomUUID = UUID.randomUUID();
    NamespacedKey advancementKey = new NamespacedKey(ZenithProvider.instance(), "anelib_" + randomUUID);

    if (MCVersion.isBetween(MCVersion.v1_16_0, MCVersion.v1_20_4)) {
      // 1.16 - 1.20.4: NBT format with integer CustomModelData
      if (modelData == null) {
        modelData = 0;
      }
      return legacyType(icon, message, style, modelData, glowing, advancementKey);
    } else if (MCVersion.isBetween(MCVersion.v1_20_5, MCVersion.v1_21_3)) {
      // 1.20.5 - 1.21.3: Components format with integer CustomModelData
      if (modelData == null) {
        modelData = 0;
      }
      return middleType(icon, message, style, modelData, glowing, advancementKey);
    } else if (MCVersion.isNewerThan(MCVersion.v1_21_3)) {
      // 1.21.4+: Components format with floats/strings arrays
      if (modelData == null) {
        modelData = "anemys";
        modelDataType = "string";
      }
      return modernType(icon, message, style, modelData, modelDataType, glowing, advancementKey);
    } else {
      throw new OutdatedVersionError("Advancements are not supported on " + MCVersion.current());
    }

  }

  private static void grantAdvancement(Player p, NamespacedKey advancementKey) {
    p.getAdvancementProgress(Objects.requireNonNull(Bukkit.getAdvancement(advancementKey)))
        .awardCriteria("trigger");
  }

  private static void revokeAdvancement(Player p, NamespacedKey advancementKey) {
    p.getAdvancementProgress(Objects.requireNonNull(Bukkit.getAdvancement(advancementKey)))
        .revokeCriteria("trigger");
  }
}
