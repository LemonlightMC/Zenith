dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-config"))
    compileOnly("org.apache.logging.log4j:log4j-api:2.26.1")
    runtimeOnly("org.apache.logging.log4j:log4j-to-jul:2.26.1")
}

extensions.extraProperties["moduleName"] = "zenith-base"