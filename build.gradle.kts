import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain
import org.openstreetmap.josm.gradle.plugin.config.JosmPluginExtension
import java.net.URI
import java.net.URL

val jvmTarget = 11
val kotlinStdlibDependency = "stdlib-jdk8"

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openstreetmap.josm").version("0.8.2")
}

//archivesBaseName = "Shrinkwrap"

repositories {
    mavenCentral()
}

dependencies {
    val kotlinStdlibDependencyId = kotlin(kotlinStdlibDependency)
    implementation(kotlinStdlibDependencyId)
    packIntoJar(kotlinStdlibDependencyId)
}

val service = project.extensions.getByType<JavaToolchainService>()
val customLauncher = service.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(jvmTarget))
}
project.tasks.withType<UsesKotlinJavaToolchain>().configureEach {
    kotlinJavaToolchain.toolchain.use(customLauncher)
}

kotlin {
    jvmToolchain(jvmTarget)
}

configure<JosmPluginExtension> {
    josmCompileVersion = "19017"
    manifest {
        description = "Create a \"shrinkwrapped\" way or a convex hull around the selection, or a \"ballooned\" way around the cursor (inner concave hull). Useful for mapping landuse or areas."
        mainClass = "net.pfiers.shrinkwrap.Shrinkwrap"
        minJosmVersion = "19017"
        author = "Pieter Fiers (Ubipo)"
        canLoadAtRuntime = false
        iconPath = "icons/icon.svg"
        website = URI("https://github.com/ubipo/shrinkwrap").toURL()
    }
    i18n {
        bugReportEmail = "pieter@pfiers.net"
        copyrightHolder = "Pieter Fiers"
    }
}
