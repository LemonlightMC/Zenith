dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-config"))
    compileOnly("org.slf4j:slf4j-api:2.0.18")
}

extensions.extraProperties["moduleName"] = "zenith-base"