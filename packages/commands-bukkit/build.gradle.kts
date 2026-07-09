dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    //compileOnly("dev.jorel:commandapi-bukkit-shade:10.1.2")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-commands-common"))
    implementation(project(":zenith-additive"))
}

extensions.extraProperties["moduleName"] = "zenith-commands-bukkit"