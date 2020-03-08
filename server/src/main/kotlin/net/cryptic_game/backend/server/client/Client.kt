package net.cryptic_game.backend.server.client

import io.netty.channel.ChannelHandlerContext
import net.cryptic_game.backend.data.user.User
import net.cryptic_game.backend.data.user.UserWrapper
import net.cryptic_game.backend.data.user.session.Session
import net.cryptic_game.backend.data.user.session.SessionWrapper
import java.util.*

class Client(val channelHandlerContext: ChannelHandlerContext) {
    var session: Session? = null
        private set

    fun setSession(session: Session) {
        this.session = session
        SessionWrapper.setLastToCurrentTime(session)
        UserWrapper.setLastToCurrentTime(session.user)
    }

    val user: User?
        get() = session?.user

    fun setSession(user: User?, token: UUID?, deviceName: String?) {
        session = SessionWrapper.openSession(user, token, deviceName)
        UserWrapper.setLastToCurrentTime(session!!.user)
    }

    fun logout() {
        SessionWrapper.closeSession(session)
        session = null
    }

}