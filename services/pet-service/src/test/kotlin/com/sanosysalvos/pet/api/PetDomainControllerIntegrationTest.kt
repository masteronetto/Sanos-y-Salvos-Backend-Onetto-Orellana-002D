package com.sanosysalvos.pet.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sanosysalvos.contracts.PetProfile
import com.sanosysalvos.contracts.PetSize
import com.sanosysalvos.contracts.PetSpecies
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class PetDomainControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `create pet and list by owner should work`() {
        val request = PetProfile(
            ownerId = "owner-1",
            name = "Firulais",
            species = PetSpecies.DOG,
            breed = "Mixed",
            ageYears = 3,
            color = "Brown",
            size = PetSize.MEDIUM,
            photoUrl = null,
            healthStatus = "Healthy",
        )

        mockMvc.post("/api/v1/pets/create") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.name") { value("Firulais") }
        }

        val result = mockMvc.get("/api/v1/pets/list_by_owner/owner-1")
            .andExpect {
                status { isOk() }
            }
            .andReturn()

        assertFalse(result.response.contentAsString.isBlank())
    }
}