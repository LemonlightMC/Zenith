dependencies {
    compileOnly("com.google.code.gson:gson:2.14.0")
    implementation(project(":zenith-core"))
    implementation(project(":zenith-additive"))
}

extensions.extraProperties["moduleName"] = "zenith-dependency-common"