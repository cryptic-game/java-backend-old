package net.cryptic_game.backend.base.api

import com.google.gson.JsonObject
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.*
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

private val logger = LoggerFactory.getLogger(ApiCollection::class.java)

abstract class ApiCollection @JvmOverloads constructor(val name: String? = null) {

    @ExperimentalStdlibApi // using hasAnnotation is safe since you would do the exact same implementation anyways
    fun load(executorClass: KClass<out ApiEndpointExecutor>): List<ApiEndpointExecutor> {
        val executors: MutableList<ApiEndpointExecutor> = ArrayList()
        this::class.declaredFunctions.forEach { function ->
            if (function.hasAnnotation<ApiEndpoint>()) {
                val name = function.findAnnotation<ApiEndpoint>()!!.value
                if (validateMethod(name, function)) {
                    val constructor = executorClass.constructors.firstOrNull { constructor ->
                        val parameters = constructor.parameters
                        parameters.size == 3 && parameters.map { it.type.jvmErasure } == (listOf(String::class, ApiCollection::class, Method::class))
                    } ?: error("No matching constructor found")
                    executors.add(constructor.call(name, this, function.javaMethod))
                }
            }
        }

        if (executors.size == 0) logger.warn("""Api Collection "$name" has no endpoints.""")
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