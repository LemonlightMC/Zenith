dependencies {
    compileOnly("com.google.code.gson:gson:2.14.0")
    compileOnly("org.apache.logging.log4j:log4j-api:2.26.0")
    implementation(project(":zenith-core"))
}

extensions.extraProperties["moduleName"] = "zenith-dependency-common"