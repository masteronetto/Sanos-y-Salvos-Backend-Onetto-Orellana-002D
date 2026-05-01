package com.sanosysalvos.pet.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.CollaboratorProfile
import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.PetProfile
import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.pet.service.PetManagementService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping

@RestController
@RequestMapping("/api/v1/pets")
class PetDomainController(
    private val petManagementService: PetManagementService,
) {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "pet-service",
        "status" to "up",
    )

    @PostMapping("", "/create")
    fun registerPet(@RequestBody petProfile: PetProfile): ApiEnvelope<PetProfile> = ApiEnvelope(
        success = true,
        message = "Pet registered",
        data = petManagementService.registerPet(petProfile),
    )

    @GetMapping("", "/list")
    fun listPets(): ApiEnvelope<List<PetProfile>> = ApiEnvelope(
        success = true,
        message = "Pets list",
        data = petManagementService.listPets(),
    )

    @GetMapping("/{petId}", "/details/{petId}")
    fun details(@PathVariable petId: String): ApiEnvelope<PetProfile> = ApiEnvelope(
        success = true,
        message = "Pet details",
        data = petManagementService.getPet(petId),
    )

    @PutMapping("/{id}", "/update/{id}")
    fun update(@PathVariable id: String, @RequestBody request: PetProfile): ApiEnvelope<PetProfile> = ApiEnvelope(
        success = true,
        message = "Pet updated",
        data = petManagementService.updatePet(id, request),
    )

    @PostMapping("/{petId}/reports/lost")
    fun createLostReport(
        @PathVariable petId: String,
        @RequestBody reportSummary: ReportSummary,
    ): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Lost report created for pet $petId",
        data = petManagementService.createLostReport(petId, reportSummary),
    )

    @PostMapping("/{petId}/reports/found")
    fun createFoundReport(
        @PathVariable petId: String,
        @RequestBody reportSummary: ReportSummary,
    ): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Found report created for pet $petId",
        data = petManagementService.createFoundReport(petId, reportSummary),
    )

    @GetMapping("/{petId}/reports")
    fun reportHistory(@PathVariable petId: String): ApiEnvelope<List<ReportSummary>> = ApiEnvelope(
        success = true,
        message = "Report history for pet $petId",
        data = petManagementService.reportHistory(petId),
    )

    @PostMapping("/collaboration/incidents")
    fun collaboratorIncident(
        @RequestParam collaboratorType: CollaboratorType,
        @RequestBody reportSummary: ReportSummary,
    ): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Collaboration incident accepted from $collaboratorType",
        data = petManagementService.collaboratorIncident(collaboratorType, reportSummary),
    )

    @DeleteMapping("/{id}", "/delete/{id}")
    fun deletePet(@PathVariable id: String): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Pet deleted",
        data = petManagementService.deletePet(id),
    )

    @GetMapping("/list_by_owner/{ownerId}")
    fun listByOwner(@PathVariable ownerId: String): ApiEnvelope<List<PetProfile>> = ApiEnvelope(
        success = true,
        message = "Pets for owner $ownerId",
        data = petManagementService.listByOwner(ownerId),
    )

    @GetMapping("/reports/search")
    fun searchReports(
        @RequestParam latitude: Double,
        @RequestParam longitude: Double,
        @RequestParam(required = false, defaultValue = "3000") radiusMeters: Int,
        @RequestParam(required = false) reportType: ReportType?,
    ): ApiEnvelope<List<ReportSummary>> = ApiEnvelope(
        success = true,
        message = "Report search results",
        data = petManagementService.searchReports(latitude, longitude, radiusMeters, reportType),
    )

    @GetMapping("/reports/list")
    fun listReports(): ApiEnvelope<List<ReportSummary>> = ApiEnvelope(
        success = true,
        message = "Reports list",
        data = petManagementService.listReports(),
    )

    @GetMapping("/reports/details/{id}")
    fun reportDetails(@PathVariable id: String): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Report details",
        data = petManagementService.getReport(id),
    )

    @PostMapping("/reports/create")
    fun createReport(@RequestBody reportSummary: ReportSummary): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Report created",
        data = petManagementService.createReport(reportSummary),
    )

    @PutMapping("/reports/update/{id}")
    fun updateReport(@PathVariable id: String, @RequestBody reportSummary: ReportSummary): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Report updated",
        data = petManagementService.updateReport(id, reportSummary),
    )

    @DeleteMapping("/reports/delete/{id}")
    fun deleteReport(@PathVariable id: String): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Report deleted",
        data = petManagementService.deleteReport(id),
    )

    @GetMapping("/reports/my_reports")
    fun myReports(@RequestParam reporterId: String): ApiEnvelope<List<ReportSummary>> = ApiEnvelope(
        success = true,
        message = "Reports of the current user",
        data = petManagementService.myReports(reporterId),
    )

    @GetMapping("/collaborators/list_by_type")
    fun listCollaborators(@RequestParam collaboratorType: CollaboratorType): ApiEnvelope<List<CollaboratorProfile>> = ApiEnvelope(
        success = true,
        message = "Collaborators of type $collaboratorType",
        data = petManagementService.listCollaboratorsByType(collaboratorType),
    )

    @GetMapping("/collaborators/list")
    fun listCollaborators(): ApiEnvelope<List<CollaboratorProfile>> = ApiEnvelope(
        success = true,
        message = "Collaborators list",
        data = petManagementService.listCollaborators(),
    )

    @GetMapping("/collaborators/details/{id}")
    fun collaboratorDetails(@PathVariable id: String): ApiEnvelope<CollaboratorProfile> = ApiEnvelope(
        success = true,
        message = "Collaborator details",
        data = petManagementService.getCollaborator(id),
    )

    @PostMapping("/collaborators/create")
    fun createCollaborator(@RequestBody collaboratorProfile: CollaboratorProfile): ApiEnvelope<CollaboratorProfile> = ApiEnvelope(
        success = true,
        message = "Collaborator created",
        data = petManagementService.createCollaborator(collaboratorProfile),
    )

    @PutMapping("/collaborators/update/{id}")
    fun updateCollaborator(@PathVariable id: String, @RequestBody collaboratorProfile: CollaboratorProfile): ApiEnvelope<CollaboratorProfile> = ApiEnvelope(
        success = true,
        message = "Collaborator updated",
        data = petManagementService.updateCollaborator(id, collaboratorProfile),
    )

    @DeleteMapping("/collaborators/delete/{id}")
    fun deleteCollaborator(@PathVariable id: String): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Collaborator deleted",
        data = petManagementService.deleteCollaborator(id),
    )
}
