package com.sanosysalvos.match.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.sanosysalvos.contracts.MatchEvaluationRequest
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
class MatchHealthControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `evaluate and pending should work`() {
        mockMvc.post("/api/v1/matches/evaluate") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(MatchEvaluationRequest("lost-1", "found-1"))
        }.andExpect {
            status { isOk() }
            jsonPath("$.data.shouldNotify") { value(true) }
        }

        val result = mockMvc.get("/api/v1/matches/pending")
            .andExpect { status { isOk() } }
            .andReturn()

        assertFalse(result.response.contentAsString.isBlank())
    }
}