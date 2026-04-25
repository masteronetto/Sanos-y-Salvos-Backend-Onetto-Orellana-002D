package com.sanosysalvos.common

data class ErrorPayload(
    val code: String,
    val message: String,
    val details: Map<String, Any?> = emptyMap(),
)

open class ServiceException(
    override val message: String,
    val code: String,
) : RuntimeException(message)
