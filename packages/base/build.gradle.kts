dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1.2-R0.1-SNAPSHOT")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-config"))
}

extensions.extraProperties["moduleName"] = "zenith-base"