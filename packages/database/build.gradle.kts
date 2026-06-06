dependencies {
    compileOnly("org.spigotmc:spigot-api:26.1.2-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation(project(":zenith-core"))
}

extensions.extraProperties["moduleName"] = "zenith-database"