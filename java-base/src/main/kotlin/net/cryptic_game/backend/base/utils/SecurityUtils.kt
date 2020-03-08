package net.cryptic_game.backend.base.utils

import at.favre.lib.crypto.bcrypt.BCrypt

object SecurityUtils {
    private val HASHER = BCrypt.withDefaults()
    private val VERIFYER = BCrypt.verifyer()

    @JvmStatic
    fun hash(content: String): String = HASHER.hashToString(12, content.toCharArray())

    @JvmStatic
    fun verify(content: String, hash: String) =
            VERIFYER.verify(content.toCharArray(), hash.toCharArray()).verified
}