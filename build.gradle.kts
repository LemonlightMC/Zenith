allprojects {
    version = "0.1.0"
    group = "com.lemonlightmc"
    description = "Zenith"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withSourcesJar()
        //withJavadocJar()
    }

    val projectName = name

    tasks.named("build") {
        doLast {
            println("Finished Building: $projectName")
        }
    }

    extensions.configure<PublishingExtension> {
        publications.create(
            "maven", MavenPublication::class
        ) {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}