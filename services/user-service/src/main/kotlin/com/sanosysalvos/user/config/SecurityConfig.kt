package com.sanosysalvos.user.config

import com.sanosysalvos.common.JwtTokenService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    @Value("\${security.jwt.secret:change-me-change-me-change-me-change-me-32}") private val jwtSecret: String,
) {
    @Bean
    fun jwtTokenService(): JwtTokenService = JwtTokenService(jwtSecret)

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationFilter: JwtAuthenticationFilter): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .cors(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/v1/users/health",
                    "/api/v1/users/register",
                    "/api/v1/users/login",
                    "/api/v1/users/refresh",
                    "/api/v1/users/reset/**",
                    "/api/v1/users/message/send_welcome_email",
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Bean
    fun jwtAuthenticationFilter(jwtTokenService: JwtTokenService): JwtAuthenticationFilter = JwtAuthenticationFilter(jwtTokenService)
}

class JwtAuthenticationFilter(
    private val jwtTokenService: JwtTokenService,
) : OncePerRequestFilter() {
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.requestURI
        return path.endsWith("/health") || path.endsWith("/register") || path.endsWith("/login") || path.endsWith("/refresh") || path.startsWith("/api/v1/users/reset/") || path.endsWith("/message/send_welcome_email")
    }

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authHeader = request.getHeader("Authorization")
        if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            if (jwtTokenService.isValid(token)) {
                val subject = jwtTokenService.extractSubject(token)
                if (!subject.isNullOrBlank()) {
                    val authentication = UsernamePasswordAuthenticationToken(subject, null, emptyList())
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        filterChain.doFilter(request, response)
    }
}