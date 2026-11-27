dependencies {
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.17.0")
    compileOnly("org.spongepowered:configurate-core:4.2.0")
    compileOnly("org.slf4j:slf4j-api:2.0.7")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-additive"))
    implementation(project(":zenith-actions-common"))
}

extensions.extraProperties["moduleName"] = "zenith-actions-bukkit"
