package org.vld.template.service

import org.keycloak.adapters.RefreshableKeycloakSecurityContext
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.vld.template.controller.WebController

data class JwtDetails(
        var user: String = "Unknown",
        var isAuthenticated: Boolean = false,
        var accessToken: String = "Empty",
        var isActive: Boolean = false,
        var authorities: List<String> = listOf(),
        var roles: List<String> = listOf()
)

fun extractJwtDetails(authentication: Authentication): JwtDetails {
    val jwtDetails = JwtDetails()
    if (authentication is KeycloakAuthenticationToken) {
        jwtDetails.user = authentication.principal.toString()
        jwtDetails.isAuthenticated = authentication.isAuthenticated
        jwtDetails.authorities = authentication.authorities.map { it.authority }
        val credentials = authentication.credentials
        if (credentials is RefreshableKeycloakSecurityContext) {
            jwtDetails.accessToken = credentials.tokenString
            jwtDetails.isActive = credentials.isActive
        }
        val details = authentication.details
        if (details is SimpleKeycloakAccount) {
            jwtDetails.roles = details.roles.map { it }
        }
    }
    return jwtDetails
}

interface ProductService {
    fun findAll(): String
}

@Service
class ApiProductService(
        private val resourceOwnerPasswordResourceDetails: ResourceOwnerPasswordResourceDetails,
        private val clientCredentialsResourceDetails: ClientCredentialsResourceDetails
) : ProductService {

    companion object {
        val logger = LoggerFactory.getLogger(ApiProductService::class.java)
    }

    override fun findAll(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwtDetails = extractJwtDetails(authentication)
        logger.info("Jwt Details = $jwtDetails")

        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer ${jwtDetails.accessToken}")
        val entity = HttpEntity<String>(headers)
        val restTemplage = RestTemplate()
        val response: ResponseEntity<String> =
                restTemplage.exchange("http://localhost:8081/api/products", HttpMethod.GET, entity, String::class.java)
        val statusCode = response.statusCode
        val responseBody = response.body
        logger.info("Status Code = $statusCode, Response Body = $responseBody")
        return "$responseBody!"

//        val restTemplate = OAuth2RestTemplate(resourceOwnerPasswordResourceDetails, DefaultOAuth2ClientContext())
        /*val restTemplate = OAuth2RestTemplate(clientCredentialsResourceDetails, DefaultOAuth2ClientContext())
        val response = restTemplate.getForObject("http://localhost:8081/api/products", String::class.java)
        return response*/
    }
}

interface CustomerService {
    fun findAll(): String
}

@Service
class ApiCustomerService : CustomerService {

    override fun findAll(): String {
        return "CUSTOMER"
    }
}
