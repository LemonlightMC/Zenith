dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-additive"))
}

extensions.extraProperties["moduleName"] = "zenith-config"