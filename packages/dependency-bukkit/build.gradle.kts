dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("org.apache.logging.log4j:log4j-api:2.26.1")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-dependency-common"))
    implementation(project(":zenith-additive"))
}

extensions.extraProperties["moduleName"] = "zenith-dependency-bukkit"