package com.sanosysalvos.user.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sanosysalvos.contracts.UserLoginRequest
import com.sanosysalvos.contracts.UserRegistrationRequest
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.emptyOrNullString
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
class UserHealthControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `register login and me should work`() {
        val registerRequest = UserRegistrationRequest(
            fullName = "Test User",
            email = "test@example.com",
            phone = "555-1234",
            password = "secret123",
        )

        mockMvc.post("/api/v1/users/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(registerRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.success") { value(true) }
            jsonPath("$.data.token") { value(not(emptyOrNullString())) }
        }

        val loginResult = mockMvc.post("/api/v1/users/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(UserLoginRequest(email = "test@example.com", password = "secret123"))
        }.andExpect {
            status { isOk() }
        }.andReturn()

        val loginJson = loginResult.response.contentAsString
        assertFalse(loginJson.isBlank())
        val token = objectMapper.readTree(loginJson).path("data").path("token").asText()

        mockMvc.get("/api/v1/users/me") {
            header("Authorization", "Bearer $token")
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.email") { value("test@example.com") }
        }
    }
}