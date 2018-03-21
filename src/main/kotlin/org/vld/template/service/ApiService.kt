package org.vld.template.service

import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext
import org.springframework.security.oauth2.client.OAuth2RestTemplate
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails
import org.springframework.stereotype.Service

interface ProductService {
    fun findAll(): String
}

@Service
class ApiProductService(
        private val resourceOwnerPasswordResourceDetails: ResourceOwnerPasswordResourceDetails,
        private val clientCredentialsResourceDetails: ClientCredentialsResourceDetails
) : ProductService {

    override fun findAll(): String {
//        val restTemplate = OAuth2RestTemplate(resourceOwnerPasswordResourceDetails, DefaultOAuth2ClientContext())
        val restTemplate = OAuth2RestTemplate(clientCredentialsResourceDetails, DefaultOAuth2ClientContext())
        val response = restTemplate.getForObject("http://localhost:8081/api/products", String::class.java)
        return response
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
