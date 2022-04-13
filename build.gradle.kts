plugins {
    `java-gradle-plugin`  // plugins
    `maven-publish`  // metadata
    kotlin("jvm") version "1.6.20"  // kotlin code
    id("com.gradle.plugin-publish") version "1.0.0-rc-1"  // publishing
}

repositories {
    mavenCentral()
}

version = "1.2"
group = "com.jespage"

dependencies {
    implementation("software.amazon.awscdk:aws-cdk-lib:2.19.0")
    testImplementation("junit:junit:4.13.2")
}

gradlePlugin {
    // Define the plugin
    plugins {
        create("awsDeployPlugin") {
            id = "com.jespage.plugin.aws-deploy"
            implementationClass = "com.jespage.plugin.AwsDeployPlugin"
            displayName = "Aws Deploy Plugin"
            description = "Plugin for deploying AWS CDK from gradle, including other aws utils."
        }
    }
}

pluginBundle {
    website = "https://jespage.com"
    vcsUrl = website
    tags = listOf("aws", "cdk")
}