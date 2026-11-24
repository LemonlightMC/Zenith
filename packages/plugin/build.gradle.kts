dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("org.slf4j:slf4j-api:2.0.7")
    implementation(project(":zenith-additive"))
    implementation(project(":zenith-core"))
    implementation(project(":zenith-actions"))
    implementation("org.spongepowered:configurate-yaml:4.2.0")
}

extensions.extraProperties["moduleName"] = "zenith-plugin"