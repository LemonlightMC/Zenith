dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.8-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation(project(":zenith-core"))
}

extensions.extraProperties["moduleName"] = "zenith-database"