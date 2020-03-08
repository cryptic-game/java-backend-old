package net.cryptic_game.backend.base.api

import kotlin.reflect.KClass

class ApiHandler(private val executorClass: Class<out ApiEndpointExecutor>) {
    constructor(executorClass: KClass<out ApiEndpointExecutor>) : this(executorClass.java)

    private val executors: MutableMap<String, ApiEndpointExecutor> = mutableMapOf()

    fun getEndpointExecutor(endpoint: String): ApiEndpointExecutor? = executors[endpoint]

    fun registerEndpoint(executor: ApiEndpointExecutor) {
        executors[executor.getName()] = executor
    }

    fun registerApiCollection(collection: ApiCollection) =
            collection.load(executorClass).forEach { executor: ApiEndpointExecutor -> registerEndpoint(executor) }
}