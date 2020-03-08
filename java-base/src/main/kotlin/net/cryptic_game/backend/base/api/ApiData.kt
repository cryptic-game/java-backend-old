package net.cryptic_game.backend.base.api

class ApiParameterData(val key: String, val type: Class<*>, val isOptional: Boolean)

abstract class ApiExecutionData