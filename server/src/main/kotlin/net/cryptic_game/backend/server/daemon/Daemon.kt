package net.cryptic_game.backend.server.daemon

import io.netty.channel.ChannelHandlerContext

class Function(val name: String, val daemon: Daemon) {
    private val mutableParameters: MutableSet<String> = mutableSetOf()
    val parameters: Set<String>
        get() = mutableParameters.toSet()

    fun addParameter(parameter: String) = mutableParameters.add(parameter)

}

class DaemonHandler {
    private val daemons: MutableMap<String, Daemon> = mutableMapOf()
    private val functions: MutableMap<String, Function> = mutableMapOf()

    fun addDaemon(daemon: Daemon): Daemon? {
        return daemons.put(daemon.name, daemon)
    }

    fun getDaemon(name: String): Daemon? {
        return daemons[name.trim().toLowerCase()]
    }

    fun addFunction(function: Function): Function? {
        return functions.put(function.name, function)
    }

    fun getFunction(name: String): Function? {
        return functions[name.trim().toLowerCase()]
    }

}

class Daemon(val name: String, val channelHandlerContext: ChannelHandlerContext)
