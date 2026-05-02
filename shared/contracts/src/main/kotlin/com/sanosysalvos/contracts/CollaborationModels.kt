package com.sanosysalvos.contracts

enum class CollaboratorType {
    VETERINARY_CLINIC,
    SHELTER,
    MUNICIPALITY,
}

data class CollaboratorProfile(
    val id: String? = null,
    val type: CollaboratorType,
    val name: String,
    val contactEmail: String,
    val contactPhone: String? = null,
    val active: Boolean = true,
)
