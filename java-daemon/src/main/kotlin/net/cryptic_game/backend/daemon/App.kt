package net.cryptic_game.backend.daemon

import net.cryptic_game.backend.base.AppBootstrap
import net.cryptic_game.backend.daemon.client.DaemonClientCodec
import net.cryptic_game.backend.daemon.client.NettyClientHandler
import net.cryptic_game.backend.daemon.config.DaemonConfig

class App : AppBootstrap(DaemonConfig.CONFIG, "Java-Daemon") {
    private lateinit var clientHandler: NettyClientHandler
    override fun init() {
        clientHandler = NettyClientHandler()
        clientHandler.addClient("daemon",
                "localhost", 4201,
                DaemonClientCodec())
    }

    override fun start() = clientHandler.start()

    override fun initApi() = Unit

}

fun main() {
    App()
}