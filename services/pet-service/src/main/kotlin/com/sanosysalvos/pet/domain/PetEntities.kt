package com.sanosysalvos.pet.domain

import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.PetSize
import com.sanosysalvos.contracts.PetSpecies
import com.sanosysalvos.contracts.ReportType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "pets")
class PetEntity(
    @Id
    var id: String? = null,
    @Column(nullable = false)
    var ownerId: String,
    @Column(nullable = false)
    var name: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var species: PetSpecies,
    @Column
    var breed: String? = null,
    @Column
    var ageYears: Int? = null,
    @Column(nullable = false)
    var color: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var size: PetSize,
    @Column
    var photoUrl: String? = null,
    @Column
    var healthStatus: String? = null,
    @Column(nullable = false)
    var active: Boolean = true,
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PrePersist
    fun onCreate() {
        if (id == null) id = UUID.randomUUID().toString()
        createdAt = Instant.now()
        updatedAt = createdAt
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}

@Entity
@Table(name = "reports")
class ReportEntity(
    @Id
    var id: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: ReportType,
    @Column
    var petId: String? = null,
    @Column(nullable = false)
    var reporterId: String,
    @Column(nullable = false, columnDefinition = "text")
    var description: String,
    @Column(nullable = false)
    var latitude: Double,
    @Column(nullable = false)
    var longitude: Double,
    @Column(nullable = false)
    var eventDate: String,
    @Column
    var photoUrl: String? = null,
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PrePersist
    fun onCreate() {
        if (id == null) id = UUID.randomUUID().toString()
        createdAt = Instant.now()
        updatedAt = createdAt
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}

@Entity
@Table(name = "collaborators")
class CollaboratorEntity(
    @Id
    var id: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: CollaboratorType,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var contactEmail: String,
    @Column
    var contactPhone: String? = null,
    @Column(nullable = false)
    var active: Boolean = true,
    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),
    @Column(nullable = false)
    var updatedAt: Instant = Instant.now(),
) {
    @PrePersist
    fun onCreate() {
        if (id == null) id = UUID.randomUUID().toString()
        createdAt = Instant.now()
        updatedAt = createdAt
    }

    @PreUpdate
    fun onUpdate() {
        updatedAt = Instant.now()
    }
}