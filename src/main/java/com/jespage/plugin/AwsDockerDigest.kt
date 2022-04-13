package com.jespage.plugin

import org.gradle.api.Project

/**
 * Needs login for this to succeed.
 */
fun Project.getLatestDigest(
    imageTag: String,
    awsAccount: String,
    awsRegion: String
) = cmd(
    "docker",
    "inspect",
    "--format='{{index .RepoDigests 0}}'",
    awsImage(awsAccount, awsRegion, imageTag),
    printStdout = false
).trim('\'', '\n').also {
    println("Latest image digest: $it")
}
