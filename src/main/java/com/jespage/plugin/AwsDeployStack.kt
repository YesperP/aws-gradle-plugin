package com.jespage.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider

abstract class AwsDeployStack : DefaultTask() {
    @get:InputDirectory
    abstract val outputStackDir: DirectoryProperty

    @get:Input
    abstract val profile: Property<String>

    fun outputStack(task: TaskProvider<AwsOutputStack>) {
        outputStackDir.set(task.flatMap { it.outputDir })
    }

    init {
        group = AwsDeployPlugin.DEFAULT_TASK_GROUP
    }

    @TaskAction
    fun deploy() {
        project.cmd(
            "cdk", "deploy",
            "--app", outputStackDir.get().toString(),
            "--profile", profile.get(),
            "--require-approval", "never"
        )
    }
}