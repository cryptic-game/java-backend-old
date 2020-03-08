package net.cryptic_game.backend.server.api

import com.google.gson.JsonObject
import net.cryptic_game.backend.base.api.*
import net.cryptic_game.backend.server.client.Client
import net.cryptic_game.backend.server.server.ServerResponseType
import net.cryptic_game.backend.server.server.websocket.WebSocketUtils

class ServerApiExecutor(apiHandler: ApiHandler?) : ApiExecutor(apiHandler!!) {
    @Throws(ApiException::class)
    override fun execute(data: ApiExecutionData): JsonObject {
        if (data !is ServerApiExecutionData) throw ApiException("Provided " + data.javaClass.toString()
                + " is not instance of \"" + ServerApiExecutionData::class.java.toString() + "\".")
        val endpoint = apiHandler.getEndpointExecutor(data.endpoint)
                ?: return WebSocketUtils.build(ServerResponseType.NOT_FOUND, "UNKNOWN_ACTION")
        return try {
            endpoint.execute(data)
        } catch (e: ApiParameterException) {
            WebSocketUtils.build(ServerResponseType.BAD_REQUEST, e.message)
        }
    }
}


class ServerApiExecutionData(val endpoint: String, val client: Client, val data: JsonObject) : ApiExecutionData()
