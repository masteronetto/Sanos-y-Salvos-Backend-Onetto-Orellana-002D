package com.sanosysalvos.pet.api

import com.sanosysalvos.contracts.ApiEnvelope
import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.PetProfile
import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/pets")
class PetDomainController {

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf(
        "service" to "pet-service",
        "status" to "up",
    )

    @PostMapping
    fun registerPet(@RequestBody petProfile: PetProfile): ApiEnvelope<PetProfile> = ApiEnvelope(
        success = true,
        message = "Pet registered",
        data = petProfile,
    )

    @PostMapping("/{petId}/reports/lost")
    fun createLostReport(
        @PathVariable petId: String,
        @RequestBody reportSummary: ReportSummary,
    ): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Lost report created for pet $petId",
        data = reportSummary.copy(type = ReportType.LOST, petId = petId),
    )

    @PostMapping("/{petId}/reports/found")
    fun createFoundReport(
        @PathVariable petId: String,
        @RequestBody reportSummary: ReportSummary,
    ): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Found report created for pet $petId",
        data = reportSummary.copy(type = ReportType.FOUND, petId = petId),
    )

    @GetMapping("/{petId}/reports")
    fun reportHistory(@PathVariable petId: String): ApiEnvelope<List<ReportSummary>> = ApiEnvelope(
        success = true,
        message = "Report history for pet $petId",
        data = emptyList(),
    )

    @PostMapping("/collaboration/incidents")
    fun collaboratorIncident(
        @RequestParam collaboratorType: CollaboratorType,
        @RequestBody reportSummary: ReportSummary,
    ): ApiEnvelope<ReportSummary> = ApiEnvelope(
        success = true,
        message = "Collaboration incident accepted from $collaboratorType",
        data = reportSummary,
    )

    @DeleteMapping("/{id}")
    fun deletePet(@PathVariable id: String): ApiEnvelope<String> = ApiEnvelope(
        success = true,
        message = "Pet deleted",
        data = id,
    )

    @GetMapping("/list_by_owner/{ownerId}")
    fun listByOwner(@PathVariable ownerId: String): ApiEnvelope<List<PetProfile>> = ApiEnvelope(
        success = true,
        message = "Pets for owner $ownerId",
        data = emptyList(),
    )

    @PostMapping("/reports/search")
    fun searchReports(@RequestBody params: Map<String, Any>): ApiEnvelope<List<ReportSummary>> = ApiEnvelope(
        success = true,
        message = "Report search results",
        data = emptyList(),
    )

    @GetMapping("/collaborators/list_by_type")
    fun listCollaborators(@RequestParam collaboratorType: CollaboratorType): ApiEnvelope<List<CollaboratorProfile>> = ApiEnvelope(
        success = true,
        message = "Collaborators of type $collaboratorType",
        data = listOf(
            CollaboratorProfile(id = "col1", type = collaboratorType, name = "Demo", contactEmail = "col@example.com")
        ),
    )
}
