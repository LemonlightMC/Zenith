package com.lemonlightmc.zenith.updater.platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lemonlightmc.zenith.updater.HttpUtil;
import com.lemonlightmc.zenith.updater.PluginData;
import com.lemonlightmc.zenith.updater.VersionChecker;
import com.lemonlightmc.zenith.updater.PlatformData.AbstractPlatformData;
import com.lemonlightmc.zenith.updater.PlatformData.GithubData;
import com.lemonlightmc.zenith.version.Version;

import java.io.IOException;
import java.net.http.HttpResponse;

public class GithubVersionChecker extends VersionChecker {
    public static final String ENDPOINT = "https://api.github.com";

    @Override
    public Version getLatestVersion(PluginData pluginData, AbstractPlatformData platformData)
            throws IOException, InterruptedException {
        if (!(platformData instanceof GithubData githubData)) {
            return null;
        }

        JsonObject releaseJson = getLatestRelease(pluginData, githubData);
        return new Version(releaseJson.get("tag_name").getAsString());
    }

    @Override
    public String getDownloadUrl(PluginData pluginData, AbstractPlatformData platformData)
            throws IOException, InterruptedException {
        if (!(platformData instanceof GithubData githubData)) {
            return null;
        }

        JsonObject releaseJson = getLatestRelease(pluginData, githubData);
        JsonArray assetsJson = releaseJson.get("assets").getAsJsonArray();
        return assetsJson.get(0).getAsJsonObject().get("browser_download_url").getAsString();
    }

    private static JsonObject getLatestRelease(PluginData pluginData, GithubData githubData)
            throws IOException, InterruptedException {
        HttpResponse<String> response = HttpUtil.sendRequest(String.format("%s/repos/%s/releases/latest",
                ENDPOINT, githubData.getGithubRepo()));

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Received invalid response code (%s) whilst checking '%s' for updates."
                    .formatted(response.statusCode(), pluginData.getPluginName()));
        }

        return JsonParser.parseString(response.body()).getAsJsonObject();
    }
}
