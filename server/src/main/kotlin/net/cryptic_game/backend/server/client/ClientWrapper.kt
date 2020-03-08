package net.cryptic_game.backend.server.client

import io.netty.channel.ChannelHandlerContext
import net.cryptic_game.backend.data.user.User

object ClientWrapper {
    private val clients: MutableMap<ChannelHandlerContext, Client> = HashMap()

    @JvmStatic
    val onlineCount: Int
        get() = clients.size

    @JvmStatic
    fun addClient(ctx: ChannelHandlerContext) {
        clients[ctx] = Client(ctx)
    }

    @JvmStatic
    fun removeClient(client: Client) {
        clients.remove(client.channelHandlerContext)
    }

    @JvmStatic
    fun getClient(ctx: ChannelHandlerContext) = clients[ctx]

    @JvmStatic
    fun getClients(user: User) = clients.values.filter {
        it.user?.id == user.id
    }

}