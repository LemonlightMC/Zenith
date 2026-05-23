dependencies {
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("org.spigotmc:spigot-api:26.1.2-R0.1-SNAPSHOT")
    //compileOnly("com.github.Traqueur-dev:RecipesAPI:2.0.2")
    //compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    implementation(project(":zenith-core"))
}

extensions.extraProperties["moduleName"] = "zenith-items"