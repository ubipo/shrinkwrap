import org.openstreetmap.josm.gradle.plugin.config.JosmPluginExtension
import java.net.URI

val jvmTarget = 11
val kotlinStdlibDependency = "stdlib-jdk8"

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.openstreetmap.josm").version("0.8.2")
}

repositories {
    mavenCentral()
}

dependencies {
    val kotlinStdlibDependencyId = kotlin(kotlinStdlibDependency)
    implementation(kotlinStdlibDependencyId)
    packIntoJar(kotlinStdlibDependencyId)
}

kotlin {
    jvmToolchain(jvmTarget)
}

configure<JosmPluginExtension> {
    josmCompileVersion = "19096"
    manifest {
        description = "Create a \"shrinkwrapped\" way or a convex hull around the selection, or a \"ballooned\" way around the cursor (inner concave hull). Useful for mapping landuse or areas."
        mainClass = "net.pfiers.shrinkwrap.Shrinkwrap"
        minJosmVersion = "19017"
        author = "Pieter Fiers (Ubipo)"
        canLoadAtRuntime = true
        iconPath = "icons/icon.svg"
        website = URI("https://github.com/ubipo/shrinkwrap").toURL()
    }
    i18n {
        bugReportEmail = "pieter@pfiers.net"
        copyrightHolder = "Pieter Fiers"
    }
}
