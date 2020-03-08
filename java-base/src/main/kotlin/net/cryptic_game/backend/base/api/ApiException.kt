package net.cryptic_game.backend.base.api

import java.io.IOException

open class ApiException : IOException {
    constructor(message: String?) : super(message)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}