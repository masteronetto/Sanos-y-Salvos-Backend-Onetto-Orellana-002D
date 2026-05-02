package com.sanosysalvos.user.api

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class UserHealthControllerIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `health check should work`() {
        mockMvc.get("/api/v1/users/health")
            .andExpect { status { isOk() } }
    }
}