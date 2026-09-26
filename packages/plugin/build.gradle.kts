dependencies {
    compileOnly("org.spigotmc:spigot-api:26.3-R0.1-SNAPSHOT")
    implementation(project(":zenith-additive"))
    implementation(project(":zenith-core"))
    implementation("org.spongepowered:configurate-yaml:4.2.0")
}

extensions.extraProperties["moduleName"] = "zenith-plugin"