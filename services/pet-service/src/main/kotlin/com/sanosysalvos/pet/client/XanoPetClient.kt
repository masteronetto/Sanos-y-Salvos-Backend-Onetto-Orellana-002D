package com.sanosysalvos.pet.client

import com.sanosysalvos.contracts.CollaboratorProfile
import com.sanosysalvos.contracts.CollaboratorType
import com.sanosysalvos.contracts.GeoPoint
import com.sanosysalvos.contracts.PetProfile
import com.sanosysalvos.contracts.PetSize
import com.sanosysalvos.contracts.PetSpecies
import com.sanosysalvos.contracts.ReportSummary
import com.sanosysalvos.contracts.ReportType
import com.sanosysalvos.common.PrefixedIdGenerator
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject

@Component
class XanoPetClient(
    private val restTemplate: RestTemplate,
    @Value("\${xano.api.baseUrl:https://x8ki-letl-twmt.n7.xano.io}") val baseUrl: String,
) {

    fun createPet(pet: PetProfile, token: String): PetProfile {
        val url = "$baseUrl/api:pets/create"
        val headers = authHeaders(token)
        val generatedId = PrefixedIdGenerator.next("M", listPets(token).map { it.id })
        val entity = HttpEntity(pet.copy(id = generatedId), headers)
        return restTemplate.postForObject(url, entity)
    }

    fun listPets(token: String): List<PetProfile> {
        val url = "$baseUrl/api:pets/list"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parsePetProfile(it) } ?: emptyList()
    }

    fun getPetDetails(id: String, token: String): PetProfile {
        val url = "$baseUrl/api:pets/details/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parsePetProfile(typedResponse)
    }

    fun updatePet(id: String, pet: PetProfile, token: String): PetProfile {
        val url = "$baseUrl/api:pets/update/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity(pet, headers)
        val response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parsePetProfile(typedResponse)
    }

    fun deletePet(id: String, token: String): String {
        val url = "$baseUrl/api:pets/delete/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void::class.java)
        return id
    }

    fun listPetsByOwner(ownerId: String, token: String): List<PetProfile> {
        val url = "$baseUrl/api:pets/list_by_owner/$ownerId"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parsePetProfile(it) } ?: emptyList()
    }

    fun createReport(report: ReportSummary, token: String): ReportSummary {
        val url = "$baseUrl/api:reports/create"
        val headers = authHeaders(token)
        val generatedId = PrefixedIdGenerator.next("R", listReports(token).map { it.id })
        val entity = HttpEntity(report.copy(id = generatedId), headers)
        return restTemplate.postForObject(url, entity)
    }

    fun getReportDetails(id: String, token: String): ReportSummary {
        val url = "$baseUrl/api:reports/details/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseReportSummary(typedResponse)
    }

    fun updateReport(id: String, report: ReportSummary, token: String): ReportSummary {
        val url = "$baseUrl/api:reports/update/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity(report, headers)
        val response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseReportSummary(typedResponse)
    }

    fun deleteReport(id: String, token: String): String {
        val url = "$baseUrl/api:reports/delete/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void::class.java)
        return id
    }

    fun listReports(token: String): List<ReportSummary> {
        val url = "$baseUrl/api:reports/list"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseReportSummary(it) } ?: emptyList()
    }

    fun myReports(reporterId: String, token: String): List<ReportSummary> {
        val url = "$baseUrl/api:reports/my_reports"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseReportSummary(it) } ?: emptyList()
    }

    fun searchReports(latitude: Double, longitude: Double, radiusMeters: Int, reportType: ReportType?, token: String): List<ReportSummary> {
        val url = "$baseUrl/api:reports/search?latitude=$latitude&longitude=$longitude&radiusMeters=$radiusMeters${reportType?.let { "&reportType=$it" } ?: ""}"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseReportSummary(it) } ?: emptyList()
    }

    fun createCollaborator(collaborator: CollaboratorProfile, token: String): CollaboratorProfile {
        val url = "$baseUrl/api:collaborators/create"
        val headers = authHeaders(token)
        val generatedId = PrefixedIdGenerator.next("E", listCollaborators(token).map { it.id })
        val entity = HttpEntity(collaborator.copy(id = generatedId), headers)
        return restTemplate.postForObject(url, entity)
    }

    fun listCollaborators(token: String): List<CollaboratorProfile> {
        val url = "$baseUrl/api:collaborators/list"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseCollaboratorProfile(it) } ?: emptyList()
    }

    fun listCollaboratorsByType(type: CollaboratorType, token: String): List<CollaboratorProfile> {
        val url = "$baseUrl/api:collaborators/list_by_type?type=$type"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        @Suppress("UNCHECKED_CAST")
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, List::class.java).body as? List<Map<String, Any>>
        return response?.map { parseCollaboratorProfile(it) } ?: emptyList()
    }

    fun getCollaboratorDetails(id: String, token: String): CollaboratorProfile {
        val url = "$baseUrl/api:collaborators/details/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseCollaboratorProfile(typedResponse)
    }

    fun updateCollaborator(id: String, collaborator: CollaboratorProfile, token: String): CollaboratorProfile {
        val url = "$baseUrl/api:collaborators/update/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity(collaborator, headers)
        val response = restTemplate.exchange(url, HttpMethod.PUT, entity, Map::class.java).body
        @Suppress("UNCHECKED_CAST")
        val typedResponse = response as? Map<String, Any>
        return parseCollaboratorProfile(typedResponse)
    }

    fun deleteCollaborator(id: String, token: String): String {
        val url = "$baseUrl/api:collaborators/delete/$id"
        val headers = authHeaders(token)
        val entity = HttpEntity<Void>(headers)
        restTemplate.exchange(url, HttpMethod.DELETE, entity, Void::class.java)
        return id
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePetProfile(response: Map<String, Any>?): PetProfile {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        return PetProfile(
            id = data["id"]?.toString(),
            ownerId = data["ownerId"]?.toString() ?: "",
            name = data["name"]?.toString() ?: "",
            species = PetSpecies.values().find { it.name == data["species"]?.toString()?.uppercase() } ?: PetSpecies.DOG,
            breed = data["breed"]?.toString(),
            ageYears = (data["ageYears"] as? Number)?.toInt(),
            color = data["color"]?.toString() ?: "",
            size = PetSize.values().find { it.name == data["size"]?.toString()?.uppercase() } ?: PetSize.SMALL,
            photoUrl = data["photoUrl"]?.toString(),
            healthStatus = data["healthStatus"]?.toString(),
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseReportSummary(response: Map<String, Any>?): ReportSummary {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        val latitude = (data["latitude"] as? Number)?.toDouble()
            ?: ((data["location"] as? Map<*, *>)?.get("latitude") as? Number)?.toDouble()
            ?: 0.0
        val longitude = (data["longitude"] as? Number)?.toDouble()
            ?: ((data["location"] as? Map<*, *>)?.get("longitude") as? Number)?.toDouble()
            ?: 0.0
        return ReportSummary(
            id = data["id"]?.toString(),
            type = ReportType.values().find { it.name == data["type"]?.toString()?.uppercase() } ?: ReportType.LOST,
            petId = data["petId"]?.toString(),
            reporterId = data["reporterId"]?.toString() ?: "",
            description = data["description"]?.toString() ?: "",
            location = GeoPoint(latitude = latitude, longitude = longitude),
            eventDate = data["eventDate"]?.toString() ?: "",
            photoUrl = data["photoUrl"]?.toString(),
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseCollaboratorProfile(response: Map<String, Any>?): CollaboratorProfile {
        val data = (response?.get("data") as? Map<String, Any>) ?: response ?: emptyMap()
        return CollaboratorProfile(
            id = data["id"]?.toString(),
            type = CollaboratorType.values().find { it.name == data["type"]?.toString()?.uppercase() } ?: CollaboratorType.VETERINARY_CLINIC,
            name = data["name"]?.toString() ?: "",
            contactEmail = data["contactEmail"]?.toString() ?: data["email"]?.toString() ?: "",
            contactPhone = data["contactPhone"]?.toString() ?: data["phone"]?.toString(),
        )
    }

    private fun authHeaders(token: String): HttpHeaders {
        return HttpHeaders().apply {
            add("Authorization", "Bearer $token")
            contentType = MediaType.APPLICATION_JSON
        }
    }
}
