package com.jespage.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

fun awsRepo(awsAccount: String, awsRegion: String) =
    "${awsAccount}.dkr.ecr.${awsRegion}.amazonaws.com"

fun awsImage(awsAccount: String, awsRegion: String, imageName: String, version: String = "latest") =
    "${awsRepo(awsAccount, awsRegion)}/${imageName}:${version}"

abstract class AwsDockerLogin : AwsAccountRegionAware, AwsCredentialsAware, DefaultTask() {

    @TaskAction
    fun login() {
        val loginPassword = project.cmd(
            "aws", "ecr", "get-login-password",
            "--profile", awsProfile.get(),
            "--region", awsRegion.get(),
            printStdout = false,
            printStderr = false
        )
        val awsRepo = awsRepo(awsAccount.get(), awsRegion.get())
        project.cmd(
            "docker", "login",
            "--username", "AWS",
            "--password", loginPassword,
            awsRepo
        )
    }
}