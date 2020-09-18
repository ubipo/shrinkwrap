import org.openstreetmap.josm.gradle.plugin.config.JosmPluginExtension
import java.net.URL

plugins {
    kotlin("jvm") version "1.4.10"
    id("org.openstreetmap.josm").version("0.7.0")
}

//archivesBaseName = "Shrinkwrap"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    packIntoJar(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

configure<JosmPluginExtension> {
    josmCompileVersion = "17013"
    manifest {
        description = "Create a \"shrinkwrapped\" way or a convex hull around the selection, or a \"ballooned\" way around the cursor (inner concave hull). Useful for mapping landuse or areas."
        mainClass = "net.pfiers.shrinkwrap.Shrinkwrap"
        minJosmVersion = "16239"
        author = "Pieter Fiers (Ubipo)"
        canLoadAtRuntime = true
        iconPath = "icons/icon.svg"
        website = URL("https://github.com/ubipo/shrinkwrap")
    }
    i18n {
        bugReportEmail = "pieter@pfiers.net"
        copyrightHolder = "Pieter Fiers"
    }
}
