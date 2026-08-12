import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.toolchain.JavaLanguageVersion

allprojects {
    version = "0.1.0"
    group = "com.lemonlightmc"
    description = "A Toolbox for LemonlightMC"
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
        // withJavadocJar()
    }

    /*
     * Make sourcesJar produce:
     *
     *   zenith-<project>-<version>.jar
     *
     * instead of:
     *
     *   <project>-<version>-sources.jar
     */
    tasks.named<Jar>("sourcesJar") {
        archiveBaseName.set("${project.name}")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")
    }

    /*
     * Disable the normal compiled JAR from being produced.
     *
     * The sources JAR is now the main artifact.
     */
    tasks.named<Jar>("jar") {
        enabled = false
    }

    /*
     * Publish sourcesJar as the main Maven artifact.
     */
    extensions.configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                groupId = project.group.toString()
                artifactId = project.name
                version = project.version.toString()

                artifact(tasks.named("sourcesJar"))
            }
        }
    }
    val projectName = name

    tasks.named("build") {
        doLast {
            println("Finished Building: $projectName")
        }
    }
}