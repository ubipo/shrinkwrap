import org.openstreetmap.josm.gradle.plugin.config.JosmPluginExtension
import java.net.URL

plugins {
    kotlin("jvm") version "1.3.72"
    id("org.openstreetmap.josm").version("0.7.0").apply(false)
}

group = "net.pfiers";
version = "v1.0.0";
//archivesBaseName = "Shrinkwrap"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

apply(plugin = "org.openstreetmap.josm")

configure<JosmPluginExtension> {
    josmCompileVersion = "16239"
    manifest {
        description = "Create a \"shrinkwrapped\" way around the selection."
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
