package net.cryptic_game.backend.base.api

import com.google.gson.JsonObject

abstract class ApiExecutor(protected val apiHandler: ApiHandler) {
    @Throws(ApiException::class)
    abstract fun execute(data: ApiExecutionData): JsonObject

}