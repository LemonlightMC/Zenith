dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1.2-R0.1-SNAPSHOT")
    //compileOnly("com.github.Traqueur-dev:RecipesAPI:2.0.2")
    //compileOnly("dev.jorel:commandapi-bukkit-shade:10.1.2")
    //compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.19")
    compileOnly("net.luckperms:api:5.5")
    compileOnly("com.github.NEZNAMY:TAB-API:5.5.0")
    implementation(project(":zenith-core"))
}

extensions.extraProperties["moduleName"] = "zenith-integrations"