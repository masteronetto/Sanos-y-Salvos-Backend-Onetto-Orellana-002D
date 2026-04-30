package com.sanosysalvos.pet.service

import com.sanosysalvos.common.NotFoundServiceException
import com.sanosysalvos.contracts.CollaboratorProfile
import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.GeoPoint
import com.sanosysalvos.contracts.PetProfile
import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.pet.domain.CollaboratorEntity
import com.sanosysalvos.pet.domain.CollaboratorRepository
import com.sanosysalvos.pet.domain.PetEntity
import com.sanosysalvos.pet.domain.PetRepository
import com.sanosysalvos.pet.domain.ReportEntity
import com.sanosysalvos.pet.domain.ReportRepository
import org.springframework.stereotype.Service

@Service
class PetManagementService(
    private val petRepository: PetRepository,
    private val reportRepository: ReportRepository,
    private val collaboratorRepository: CollaboratorRepository,
) {
    fun registerPet(request: PetProfile): PetProfile = toProfile(
        petRepository.save(
            PetEntity(
                ownerId = request.ownerId,
                name = request.name,
                species = request.species,
                breed = request.breed,
                ageYears = request.ageYears,
                color = request.color,
                size = request.size,
                photoUrl = request.photoUrl,
                healthStatus = request.healthStatus,
            ),
        ),
    )

    fun updatePet(id: String, request: PetProfile): PetProfile {
        val pet = findPet(id)
        pet.ownerId = request.ownerId
        pet.name = request.name
        pet.species = request.species
        pet.breed = request.breed
        pet.ageYears = request.ageYears
        pet.color = request.color
        pet.size = request.size
        pet.photoUrl = request.photoUrl
        pet.healthStatus = request.healthStatus
        return toProfile(petRepository.save(pet))
    }

    fun deletePet(id: String): String {
        petRepository.delete(findPet(id))
        return id
    }

    fun getPet(id: String): PetProfile = toProfile(findPet(id))

    fun listPets(): List<PetProfile> = petRepository.findAll().map { toProfile(it) }

    fun listByOwner(ownerId: String): List<PetProfile> = petRepository.findAllByOwnerId(ownerId).map { toProfile(it) }

    fun reportHistory(petId: String): List<ReportSummary> = reportRepository.findAllByPetId(petId).map { toSummary(it) }

    fun createLostReport(petId: String, reportSummary: ReportSummary): ReportSummary = createReport(petId, reportSummary, ReportType.LOST)

    fun createFoundReport(petId: String, reportSummary: ReportSummary): ReportSummary = createReport(petId, reportSummary, ReportType.FOUND)

    fun createReport(request: ReportSummary): ReportSummary = toSummary(reportRepository.save(reportEntity(request)))

    fun updateReport(id: String, request: ReportSummary): ReportSummary {
        val report = findReport(id)
        report.type = request.type
        report.petId = request.petId
        report.reporterId = request.reporterId
        report.description = request.description
        report.latitude = request.location.latitude
        report.longitude = request.location.longitude
        report.eventDate = request.eventDate
        report.photoUrl = request.photoUrl
        return toSummary(reportRepository.save(report))
    }

    fun deleteReport(id: String): String {
        reportRepository.delete(findReport(id))
        return id
    }

    fun getReport(id: String): ReportSummary = toSummary(findReport(id))

    fun listReports(): List<ReportSummary> = reportRepository.findAll().map { toSummary(it) }

    fun myReports(reporterId: String): List<ReportSummary> = reportRepository.findAllByReporterId(reporterId).map { toSummary(it) }

    fun searchReports(latitude: Double, longitude: Double, radiusMeters: Int, reportType: ReportType?): List<ReportSummary> = reportRepository.findAll()
        .asSequence()
        .filter { reportType == null || it.type == reportType }
        .map { report -> report to haversineMeters(latitude, longitude, report.latitude, report.longitude) }
        .filter { (_, distance) -> distance <= radiusMeters }
        .sortedBy { it.second }
        .map { (report, _) -> toSummary(report) }
        .toList()

    fun createCollaborator(request: CollaboratorProfile): CollaboratorProfile = toProfile(
        collaboratorRepository.save(
            CollaboratorEntity(
                type = request.type,
                name = request.name,
                contactEmail = request.contactEmail,
                contactPhone = request.contactPhone,
                active = request.active,
            ),
        ),
    )

    fun updateCollaborator(id: String, request: CollaboratorProfile): CollaboratorProfile {
        val collaborator = findCollaborator(id)
        collaborator.type = request.type
        collaborator.name = request.name
        collaborator.contactEmail = request.contactEmail
        collaborator.contactPhone = request.contactPhone
        collaborator.active = request.active
        return toProfile(collaboratorRepository.save(collaborator))
    }

    fun deleteCollaborator(id: String): String {
        collaboratorRepository.delete(findCollaborator(id))
        return id
    }

    fun getCollaborator(id: String): CollaboratorProfile = toProfile(findCollaborator(id))

    fun listCollaborators(): List<CollaboratorProfile> = collaboratorRepository.findAll().map { toProfile(it) }

    fun listCollaboratorsByType(type: CollaboratorType): List<CollaboratorProfile> = collaboratorRepository.findAllByType(type).map { toProfile(it) }

    fun collaboratorIncident(collaboratorType: CollaboratorType, reportSummary: ReportSummary): ReportSummary {
        if (collaboratorRepository.findAllByType(collaboratorType).isEmpty()) {
            collaboratorRepository.save(
                CollaboratorEntity(
                    type = collaboratorType,
                    name = collaboratorType.name,
                    contactEmail = "${collaboratorType.name.lowercase()}@example.com",
                ),
            )
        }
        return createReport(reportSummary)
    }

    private fun createReport(petId: String, reportSummary: ReportSummary, type: ReportType): ReportSummary = toSummary(
        reportRepository.save(reportEntity(reportSummary.copy(petId = petId, type = type))),
    )

    private fun reportEntity(request: ReportSummary): ReportEntity = ReportEntity(
        type = request.type,
        petId = request.petId,
        reporterId = request.reporterId,
        description = request.description,
        latitude = request.location.latitude,
        longitude = request.location.longitude,
        eventDate = request.eventDate,
        photoUrl = request.photoUrl,
    )

    private fun findPet(id: String): PetEntity = petRepository.findById(id)
        .orElseThrow { NotFoundServiceException("Pet not found") }

    private fun findReport(id: String): ReportEntity = reportRepository.findById(id)
        .orElseThrow { NotFoundServiceException("Report not found") }

    private fun findCollaborator(id: String): CollaboratorEntity = collaboratorRepository.findById(id)
        .orElseThrow { NotFoundServiceException("Collaborator not found") }

    private fun toProfile(entity: PetEntity): PetProfile = PetProfile(
        id = entity.id,
        ownerId = entity.ownerId,
        name = entity.name,
        species = entity.species,
        breed = entity.breed,
        ageYears = entity.ageYears,
        color = entity.color,
        size = entity.size,
        photoUrl = entity.photoUrl,
        healthStatus = entity.healthStatus,
    )

    private fun toSummary(entity: ReportEntity): ReportSummary = ReportSummary(
        id = entity.id,
        type = entity.type,
        petId = entity.petId,
        reporterId = entity.reporterId,
        description = entity.description,
        location = GeoPoint(latitude = entity.latitude, longitude = entity.longitude),
        eventDate = entity.eventDate,
        photoUrl = entity.photoUrl,
    )

    private fun toProfile(entity: CollaboratorEntity): CollaboratorProfile = CollaboratorProfile(
        id = entity.id,
        type = entity.type,
        name = entity.name,
        contactEmail = entity.contactEmail,
        contactPhone = entity.contactPhone,
        active = entity.active,
    )

    private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadiusMeters = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
            kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
            kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        return 2 * earthRadiusMeters * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
    }
}