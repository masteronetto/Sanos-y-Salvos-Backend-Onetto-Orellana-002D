package com.sanosysalvos.contracts

enum class PetSpecies {
    DOG,
    CAT,
    OTHER,
}

enum class PetSize {
    SMALL,
    MEDIUM,
    LARGE,
}

data class PetProfile(
    val id: String? = null,
    val ownerId: String,
    val name: String,
    val species: PetSpecies,
    val breed: String? = null,
    val ageYears: Int? = null,
    val color: String,
    val size: PetSize,
    val photoUrl: String? = null,
    val healthStatus: String? = null,
)
