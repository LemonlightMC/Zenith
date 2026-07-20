dependencies {
    compileOnly("com.google.code.gson:gson:2.14.0")
    compileOnly("org.apache.logging.log4j:log4j-api:2.26.1")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-additive"))
}

extensions.extraProperties["moduleName"] = "zenith-dependency-common"