dependencies {
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")
    //compileOnly("com.github.Traqueur-dev:RecipesAPI:2.0.2")
    compileOnly("io.th0rgal:oraxen:1.217.0")
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("net.luckperms:api:5.5")
    //compileOnly("dev.jorel:commandapi-bukkit-shade:10.1.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("org.apache.logging.log4j:log4j-api:2.26.0")
    runtimeOnly("org.apache.logging.log4j:log4j-to-jul:2.26.0")
    //compileOnly("net.milkbowl.vault:VaultUnblockedAPI:2.19")
    implementation(project(":zenith-additive"))
}

extensions.extraProperties["moduleName"] = "zenith-core"