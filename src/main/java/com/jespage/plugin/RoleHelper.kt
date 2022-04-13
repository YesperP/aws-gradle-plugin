package com.jespage.plugin

import software.amazon.awscdk.services.iam.CfnRole
import software.amazon.awscdk.services.iam.Effect
import software.amazon.awscdk.services.iam.PolicyDocument
import software.amazon.awscdk.services.iam.ServicePrincipal
import software.amazon.awscdk.services.iam.PolicyStatement as AwsPolicyStatement

@DslMarker
annotation class RoleBuilderMarker

@RoleBuilderMarker
class Statement {
    val actions = mutableListOf<String>()
    val resources = mutableListOf<String>()
    fun actions(vararg actions: String) = this.actions.addAll(actions)
    fun resources(vararg resources: String) = this.resources.addAll(resources)
}

@RoleBuilderMarker
class Role {
    val statements = mutableListOf<Statement>()

    fun statement(builder: Statement.() -> Unit) {
        statements.add(Statement().apply(builder))
    }
}

fun Stack.role(
    resourceName: String,
    roleName: String,
    assumeService: String,
    managedPolicyArn: String? = null,
    builder: Role.() -> Unit
): CfnRole {
    val role = Role().apply(builder)

    val assumeRolePolicyDocument = PolicyDocument.Builder.create().build {
        statements(AwsPolicyStatement.Builder.create().build {
            effect(Effect.ALLOW)
            actions(listOf("sts:AssumeRole"))
            principals(listOf(ServicePrincipal.Builder.create(assumeService).build()))
        }.inList())
    }
    val awsStatements = role.statements.map {
        AwsPolicyStatement.Builder.create().build {
            effect(Effect.ALLOW)
            actions(it.actions)
            resources(it.resources)
        }
    }
    val rolePolicyDocument = PolicyDocument.Builder.create().build { statements(awsStatements) }
    return CfnRole.Builder::class(name = resourceName) {
        roleName(roleName)
        assumeRolePolicyDocument(assumeRolePolicyDocument)
        path("/")
        policies(CfnRole.PolicyProperty.Builder().build {
            policyName("policy")
            policyDocument(rolePolicyDocument)
        }.inList())
        managedPolicyArn?.let { managedPolicyArns(listOf(it)) }
    }
}