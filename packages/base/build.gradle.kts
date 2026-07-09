dependencies {
    compileOnly("org.spigotmc:spigot-api:26.2-R0.1-SNAPSHOT")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-config"))
    compileOnly("org.apache.logging.log4j:log4j-api:2.26.0")
    runtimeOnly("org.apache.logging.log4j:log4j-to-jul:2.26.0")
}

extensions.extraProperties["moduleName"] = "zenith-base"