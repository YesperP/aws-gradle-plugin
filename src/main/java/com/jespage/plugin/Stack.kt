package com.jespage.plugin

import software.amazon.awscdk.CfnResource
import software.amazon.awscdk.CfnTag
import software.amazon.awscdk.ExportValueOptions
import software.amazon.jsii.JsiiSerializable
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.staticFunctions
import software.amazon.awscdk.Stack as CdkStack

class Stack(val cdkStack: CdkStack) {
    val account: String
        get() = cdkStack.account
    val region: String
        get() = cdkStack.region

    private val resourceNames = mutableSetOf<String>()

    fun addResourceName(name: String): String {
        if (name in resourceNames) throw Exception("Resource name $name already exists!")
        resourceNames.add(name)
        return name
    }

    fun createResourceName(clazz: Class<*>): String {
        val base = clazz.simpleName.removePrefix("Cfn")
        var n: Int? = null

        while (true) {
            val name = "${base}${n ?: ""}"
            if (name !in resourceNames) return name
            n = (n ?: 0) + 1
        }
    }

    fun exportValue(exportedValue: Any, name: String) {
        cdkStack.account
        cdkStack.exportValue(exportedValue, ExportValueOptions.builder().build { name(name) })
    }

    @Suppress("UNCHECKED_CAST")
    inline operator fun <reified T : CfnResource, reified B : software.amazon.jsii.Builder<T>> KClass<B>.invoke(
        name: String? = null,
        builder: B.() -> Unit
    ): T {
        val resourceName = name ?: createResourceName(T::class.java)
        addResourceName(resourceName)
        try {
            return (B::class.staticFunctions.first().call(cdkStack, resourceName) as B).apply(builder).build()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Error creating $resourceName", e)
        }
    }

    inline operator fun <reified R : JsiiSerializable, reified T : software.amazon.jsii.Builder<R>> KClass<T>.invoke(
        builder: T.() -> Unit
    ): R = this.createInstance().build(builder)

}

inline fun <reified R : JsiiSerializable, T : software.amazon.jsii.Builder<R>> T.build(
    builder: T.() -> Unit
): R {
    try {
        this.builder()
        return this.build()
    } catch (e: Exception) {
        e.printStackTrace()
        throw Exception("Error creating property of type: ${R::class.simpleName}", e)
    }
}

fun <T : JsiiSerializable> T.inList(): List<T> = listOf(this)
fun tagsOf(vararg tags: Pair<String, String>) =
    tags.map { CfnTag.builder().key(it.first).value(it.second).build() }
