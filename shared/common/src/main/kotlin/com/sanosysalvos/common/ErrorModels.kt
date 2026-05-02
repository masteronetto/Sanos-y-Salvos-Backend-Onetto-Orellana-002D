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

class NotFoundServiceException(
    message: String,
) : ServiceException(message = message, code = "NOT_FOUND")

class UnauthorizedServiceException(
    message: String,
) : ServiceException(message = message, code = "UNAUTHORIZED")

class ConflictServiceException(
    message: String,
) : ServiceException(message = message, code = "CONFLICT")
