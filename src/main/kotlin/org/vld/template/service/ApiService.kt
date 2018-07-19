package org.vld.template.service

import org.keycloak.adapters.RefreshableKeycloakSecurityContext
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

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
class ApiProductService() : ProductService {

    companion object {
        val logger = LoggerFactory.getLogger(ApiProductService::class.java)
    }

    override fun findAll(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwtDetails = extractJwtDetails(authentication)
        logger.info("Product JWT = $jwtDetails")

        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer ${jwtDetails.accessToken}")
        val entity = HttpEntity<String>(headers)

        val restTemplate = RestTemplate()
        val response: ResponseEntity<String> =
                restTemplate.exchange("http://localhost:8081/api/products", HttpMethod.GET, entity, String::class.java)
        val statusCode = response.statusCode
        val responseBody = response.body
        logger.info("Status Code = $statusCode, Response Body = $responseBody")
        return responseBody
    }
}

interface CustomerService {
    fun findAll(): String
}

@Service
class ApiCustomerService : CustomerService {

    companion object {
        val logger = LoggerFactory.getLogger(ApiCustomerService::class.java)
    }

    override fun findAll(): String {
        val authentication = SecurityContextHolder.getContext().authentication
        val jwtDetails = extractJwtDetails(authentication)
        logger.info("Customer JWT = $jwtDetails")

        val resourceDetails = ClientCredentialsResourceDetails()
        resourceDetails.clientId = "WebClient"
        resourceDetails.accessTokenUri = "http://localhost:9090/auth/realms/WebApplication/protocol/openid-connect/token"

        val restTemplate = OAuth2RestTemplate(resourceDetails, DefaultOAuth2ClientContext())
        restTemplate.oAuth2ClientContext.accessToken = DefaultOAuth2AccessToken(jwtDetails.accessToken)
        val response = restTemplate.getForObject("http://localhost:8081/api/customers", String::class.java)
        return response
    }
}
