package net.cryptic_game.backend.server

import net.cryptic_game.backend.base.AppBootstrap
import net.cryptic_game.backend.base.api.ApiHandler
import net.cryptic_game.backend.server.api.ServerApiEndpointExecutor
import net.cryptic_game.backend.server.config.ServerConfig
import net.cryptic_game.backend.server.daemon.DaemonHandler
import net.cryptic_game.backend.server.server.NettyServerHandler
import net.cryptic_game.backend.server.server.http.HttpCodec
import net.cryptic_game.backend.server.server.websocket.WebSocketCodec
import net.cryptic_game.backend.server.server.websocket.endpoints.InfoEndpoints
import net.cryptic_game.backend.server.server.websocket.endpoints.UserEndpoints

class App : AppBootstrap(ServerConfig.CONFIG, "Java-Server") {
    private lateinit var daemonHandler: DaemonHandler
    private lateinit var serverHandler: NettyServerHandler

    override fun init() {
        daemonHandler = DaemonHandler()
        serverHandler = NettyServerHandler()
        serverHandler.addServer("daemon",
                "localhost", 4201,
                HttpCodec())
        serverHandler.addServer("websocket",
                config.getAsString(ServerConfig.WEBSOCKET_HOST),
                config.getAsInt(ServerConfig.WEBSOCKET_PORT),
                WebSocketCodec())
        serverHandler.addServer("http",
                config.getAsString(ServerConfig.HTTP_HOST),
                config.getAsInt(ServerConfig.HTTP_PORT),
                HttpCodec())
    }

    override fun start() = serverHandler.start()

    override fun initApi() {
        apiHandler = ApiHandler(ServerApiEndpointExecutor::class.java)
        apiHandler.registerApiCollection(UserEndpoints())
        apiHandler.registerApiCollection(InfoEndpoints())
    }
}

fun main() {
    App()
}
