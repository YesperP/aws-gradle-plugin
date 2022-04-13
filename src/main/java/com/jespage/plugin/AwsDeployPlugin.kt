package com.jespage.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

abstract class AwsDeployExtension {
    abstract val account: Property<String>
    abstract val region: Property<String>
    abstract val profile: Property<String>
}

interface AwsAccountRegionAware : Task {
    @get:Input
    val awsRegion: Property<String>

    @get:Input
    val awsAccount: Property<String>
}

interface AwsCredentialsAware : Task {
    @get:Input
    val awsProfile: Property<String>
}


class AwsDeployPlugin : Plugin<Project> {
    companion object {
        private const val EXTENSION_NAME = "aws-deploy"
        const val DEFAULT_TASK_GROUP = "aws"
    }

    override fun apply(project: Project) {
        val ext = project.extensions.create(EXTENSION_NAME, AwsDeployExtension::class.java)
        project.tasks.withType(AwsAccountRegionAware::class.java) {
            it.awsAccount.set(ext.account)
            it.awsRegion.set(ext.region)
        }
        project.tasks.withType(AwsCredentialsAware::class.java) {
            it.awsProfile.set(ext.profile)
        }
    }
}

