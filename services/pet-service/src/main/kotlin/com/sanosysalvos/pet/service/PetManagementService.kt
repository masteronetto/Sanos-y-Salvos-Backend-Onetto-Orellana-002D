package com.sanosysalvos.pet.service

import com.sanosysalvos.contracts.CollaboratorProfile
import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.PetProfile
import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.pet.client.XanoPetClient
import org.springframework.stereotype.Service

@Service
class PetManagementService(
    private val xanoPetClient: XanoPetClient,
) {
    
    fun registerPet(request: PetProfile): PetProfile =
        xanoPetClient.createPet(request, "")

    fun listPets(): List<PetProfile> =
        xanoPetClient.listPets("")

    fun getPet(id: String): PetProfile =
        xanoPetClient.getPetDetails(id, "")

    fun updatePet(id: String, request: PetProfile): PetProfile =
        xanoPetClient.updatePet(id, request, "")

    fun deletePet(id: String): String =
        xanoPetClient.deletePet(id, "")

    fun listByOwner(ownerId: String): List<PetProfile> =
        xanoPetClient.listPetsByOwner(ownerId, "")

    fun createReport(request: ReportSummary): ReportSummary =
        xanoPetClient.createReport(request, "")

    fun getReport(id: String): ReportSummary =
        xanoPetClient.getReportDetails(id, "")

    fun updateReport(id: String, request: ReportSummary): ReportSummary =
        xanoPetClient.updateReport(id, request, "")

    fun deleteReport(id: String): String =
        xanoPetClient.deleteReport(id, "")

    fun listReports(): List<ReportSummary> =
        xanoPetClient.listReports("")

    fun myReports(reporterId: String): List<ReportSummary> =
        xanoPetClient.myReports(reporterId, "")

    fun searchReports(latitude: Double, longitude: Double, radiusMeters: Int, reportType: ReportType?): List<ReportSummary> =
        xanoPetClient.searchReports(latitude, longitude, radiusMeters, reportType, "")

    fun createLostReport(petId: String, reportSummary: ReportSummary): ReportSummary {
        val lostReport = reportSummary.copy(petId = petId, type = ReportType.LOST)
        return xanoPetClient.createReport(lostReport, "")
    }

    fun createFoundReport(petId: String, reportSummary: ReportSummary): ReportSummary {
        val foundReport = reportSummary.copy(petId = petId, type = ReportType.FOUND)
        return xanoPetClient.createReport(foundReport, "")
    }

    fun reportHistory(petId: String): List<ReportSummary> {
        return xanoPetClient.listReports("").filter { it.petId == petId }
    }

    fun collaboratorIncident(collaboratorType: CollaboratorType, reportSummary: ReportSummary): ReportSummary =
        xanoPetClient.createReport(reportSummary, "")

    fun createCollaborator(request: CollaboratorProfile): CollaboratorProfile =
        xanoPetClient.createCollaborator(request, "")

    fun listCollaborators(): List<CollaboratorProfile> =
        xanoPetClient.listCollaborators("")

    fun listCollaboratorsByType(type: CollaboratorType): List<CollaboratorProfile> =
        xanoPetClient.listCollaboratorsByType(type, "")

    fun getCollaborator(id: String): CollaboratorProfile =
        xanoPetClient.getCollaboratorDetails(id, "")

    fun updateCollaborator(id: String, request: CollaboratorProfile): CollaboratorProfile =
        xanoPetClient.updateCollaborator(id, request, "")

    fun deleteCollaborator(id: String): String =
        xanoPetClient.deleteCollaborator(id, "")
}
