package net.cryptic_game.backend.base.api

import com.google.gson.JsonObject
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

private val logger = LoggerFactory.getLogger(ApiCollection::class.java)

abstract class ApiCollection @JvmOverloads constructor(val name: String? = null) {

    fun load(executorClass: Class<out ApiEndpointExecutor>): List<ApiEndpointExecutor> {
        fun handleError(e: Throwable) {
            logger.error("Error while loading Api endpoint.", e)
        }
        val executors: MutableList<ApiEndpointExecutor> = ArrayList()
        this.javaClass.declaredMethods.forEach { method ->
            if (method.isAnnotationPresent(ApiEndpoint::class.java)) {
                val name = method.getAnnotation(ApiEndpoint::class.java).value
                if (validateMethod(name, method)) {
                    try {
                        executors.add(executorClass.getDeclaredConstructor(String::class.java, ApiCollection::class.java, Method::class.java).newInstance(name, this, method))
                    } catch (e: NoSuchMethodException) {
                        handleError(e)
                    } catch (e: IllegalAccessException) {
                        handleError(e)
                    } catch (e: InstantiationException) {
                        handleError(e)
                    } catch (e: InvocationTargetException) {
                        handleError(e)
                    }
                }
            }
        }
        if (executors.size == 0) logger.warn("""Api Collection "$name" has no endpoints.""")
        return executors
    }


    private fun validateMethod(name: String, method: Method): Boolean {
        if (method.returnType != JsonObject::class.java) {
            logger.warn("""Endpoint "${this.name}/ $name " has not the return type "${JsonObject::class.simpleName}".""")
            return false
        }
        return true
    }
}