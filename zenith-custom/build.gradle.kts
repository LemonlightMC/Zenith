dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1.2-R0.1-SNAPSHOT")
    compileOnly("io.th0rgal:oraxen:1.213.0")
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    //compileOnly("com.github.Traqueur-dev:RecipesAPI:2.0.2")
    //compileOnly("dev.jorel:commandapi-bukkit-shade:10.1.2")
    implementation(project(":zenith-core"))
}

extensions.extraProperties["moduleName"] = "zenith-custom"