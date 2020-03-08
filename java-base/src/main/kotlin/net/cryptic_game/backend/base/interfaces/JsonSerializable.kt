package net.cryptic_game.backend.base.interfaces

import com.google.gson.JsonObject

interface JsonSerializable {
    fun serialize(): JsonObject
}