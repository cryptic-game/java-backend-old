package net.cryptic_game.backend.base.interfaces

interface ResponseType : JsonSerializable {
    val code: Int
    val name: String
    val isError: Boolean
    override fun toString(): String
}