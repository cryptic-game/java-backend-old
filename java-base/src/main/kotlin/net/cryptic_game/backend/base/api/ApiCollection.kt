package net.cryptic_game.backend.base.api

import com.google.gson.JsonObject
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

private val logger = LoggerFactory.getLogger(ApiCollection::class.java)

abstract class ApiCollection @JvmOverloads constructor(val name: String? = null) {

    fun load(executorClass: KClass<out ApiEndpointExecutor>): List<ApiEndpointExecutor> {
        val executors = this::class.declaredFunctions.filter { function ->
            val annotation = function.findAnnotation<ApiEndpoint>()
            annotation != null && validateMethod(annotation.value, function)
        }.map { function ->
            val constructor = executorClass.constructors.firstOrNull { constructor ->
                val parameters = constructor.parameters
                parameters.size == 3 && parameters.map { it.type.jvmErasure } == (listOf(String::class, ApiCollection::class, Method::class))
            } ?: error("No matching constructor found")
            constructor.call(name, this, function.javaMethod)
        }
        if (executors.isEmpty()) logger.warn("""Api Collection "$name" has no endpoints.""")
        return executors
    }

    private fun validateMethod(name: String, function: KFunction<*>): Boolean {
        if (function.returnType.jvmErasure != JsonObject::class) {
            logger.warn("""Endpoint "${this.name}/ $name " has not the return type "${JsonObject::class.simpleName}".""")
            return false
        }
        return true
    }

}