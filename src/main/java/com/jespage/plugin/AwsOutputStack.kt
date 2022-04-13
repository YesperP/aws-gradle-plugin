package com.jespage.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import software.amazon.awscdk.App
import software.amazon.awscdk.Environment
import software.amazon.awscdk.Stack as CdkStack

@Suppress("LeakingThis")
abstract class AwsOutputStack : AwsAccountRegionAware, DefaultTask() {

    @get:Input
    abstract val stackName: Property<String>

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        group = "aws"
        outputDir.convention(project.layout.buildDirectory.dir(stackName.map { "stacks/$it" }))
    }

    fun buildStack(builder: Stack.() -> Unit) {
        doFirst {
            val app = App.Builder.create()
                .outdir(outputDir.get().toString())
                .build()
            val stack = CdkStack.Builder
                .create(app)
                .stackName(stackName.get())
                .env(Environment.builder().account(awsAccount.get()).region(awsRegion.get()).build())
                .build()
            Stack(stack).builder()
            app.synth()
        }
    }
}