package com.lemonlightmc.zenith.updater.platform;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lemonlightmc.zenith.updater.HttpUtil;
import com.lemonlightmc.zenith.updater.PlatformData.AbstractPlatformData;
import com.lemonlightmc.zenith.updater.PlatformData.ModrinthData;
import com.lemonlightmc.zenith.updater.PluginData;
import com.lemonlightmc.zenith.updater.VersionChecker;
import com.lemonlightmc.zenith.version.Version;

public class ModrinthVersionChecker extends VersionChecker {
    public static final String ENDPOINT = "https://api.modrinth.com/v2";

    @Override
    public Version getLatestVersion(PluginData pluginData, AbstractPlatformData platformData)
            throws IOException, InterruptedException {
        if (!(platformData instanceof ModrinthData modrinthData)) {
            return null;
        }

        JsonObject currVersionJson = getLatestVersionInternal(pluginData, modrinthData);
        return new Version(currVersionJson.get("version_number").getAsString());
    }

    @Override
    public String getDownloadUrl(PluginData pluginData, AbstractPlatformData platformData)
            throws IOException, InterruptedException {
        if (!(platformData instanceof ModrinthData modrinthData)) {
            return null;
        }

        JsonObject currVersionJson = getLatestVersionInternal(pluginData, modrinthData);
        return currVersionJson.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();
    }

    private static JsonArray getVersions(PluginData pluginData, ModrinthData modrinthData)
            throws IOException, InterruptedException {
        StringBuilder uriBuilder = new StringBuilder(String.format("%s/project/%s/version",
                ENDPOINT, modrinthData.getModrinthProjectId()))
                .append("?loaders=[%22bukkit%22,%22spigot%22,%22paper%22,%22purpur%22,%22folia%22]");

        if (modrinthData.specifiesVersionType()) {
            uriBuilder.append("&version_type=").append(modrinthData.getVersionType());
        }

        if (modrinthData.includeFeaturedOnly()) {
            uriBuilder.append("&featured=true");
        }

        HttpResponse<String> response = HttpUtil.sendRequest(uriBuilder.toString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Received invalid response code (%s) whilst checking '%s' for updates."
                    .formatted(response.statusCode(), pluginData.getPluginName()));
        }

        return JsonParser.parseString(response.body()).getAsJsonArray();
    }

    private static JsonObject getLatestVersionInternal(PluginData pluginData, ModrinthData modrinthData)
            throws IOException, InterruptedException {
        JsonArray versions = getVersions(pluginData, modrinthData);
        if (versions.isEmpty()) {
            throw new IllegalStateException(
                    "Failed to collect versions for '%s'".formatted(pluginData.getPluginName()));
        }

        return versions.get(0).getAsJsonObject();
    }
}
