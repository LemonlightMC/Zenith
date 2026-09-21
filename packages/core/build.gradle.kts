dependencies {
    compileOnly("me.clip:placeholderapi:2.12.3")
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("net.luckperms:api:5.5")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.slf4j:slf4j-api:2.0.19")
    implementation("org.spongepowered:configurate-yaml:4.2.0")
    //compileOnly("net.milkbowl.vault:VaultUnblockedAPI:2.19")
    implementation(project(":zenith-additive"))
}

extensions.extraProperties["moduleName"] = "zenith-core"