package com.lemonlight;

import rife.bld.Project;
import java.util.List;
import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.*;

public class ZenithBuild extends Project {
    public ZenithBuild() {
        pkg = "com.lemonlight";
        name = "Zenith";
        version = version(0,1,0);

        downloadSources = true;
        repositories = List.of(
            MAVEN_CENTRAL,
            RIFE2_RELEASES,
            repository("https://jitpack.io"),
            repository("https://hub.spigotmc.org/nexus/content/groups/public/"),
            repository("http://nexus.hc.to/content/repositories/pub_releases")
        );

        sourceDirectories = List.of(
            "packages/core/src/main/java",
            "packages/commands-common/src/main/java",
            "packages/commands-bukkit/src/main/java",
            "packages/custom/src/main/java",
            "packages/integrations/src/main/java",
            "packages/items/src/main/java",
            "packages/database/src/main/java"
        );

        resourceDirectories = List.of(
            "packages/core/src/main/resources",
            "packages/commands-common/src/main/resources",
            "packages/commands-bukkit/src/main/resources",
            "packages/custom/src/main/resources",
            "packages/integrations/src/resources",
            "packages/items/src/ressources",
            "packages/items/src/main/resources",
            "packages/database/src/ressources",
            "packages/database/src/main/resources"
        );

        scope(provided)
            .include(dependency("org.spigotmc", "spigot-api", "1.21.8-R0.1-SNAPSHOT"))
            .include(dependency("me.clip", "placeholderapi", "2.12.2"))
            .include(dependency("io.th0rgal", "oraxen", "1.213.0"))
            .include(dependency("dev.lone", "api-itemsadder", "4.0.10"))
            .include(dependency("net.luckperms", "api", "5.5"))
            .include(dependency("com.github.MilkBowl", "VaultAPI", "1.7"))
            .include(dependency("net.milkbowl.vault", "VaultUnlockedAPI", "2.19"))
            .include(dependency("com.github.NEZNAMY", "TAB-API", "6.0.0"));

        scope(compile)
            .include(dependency("com.zaxxer", "HikariCP", "7.0.2"));

        scope(test)
            .include(dependency("org.junit.jupiter", "junit-jupiter", version(5,11,4)))
            .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1,11,4)));
    }

    public static void main(String[] args) {
        new ZenithBuild().start(args);
    }
}