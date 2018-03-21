package org.vld.template.configuration

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy

@EnableWebSecurity
open class KeycloakConfiguration : KeycloakWebSecurityConfigurerAdapter() {

    override fun keycloakAuthenticationProvider(): KeycloakAuthenticationProvider {
        val authenticationProvider = KeycloakAuthenticationProvider()
        authenticationProvider.setGrantedAuthoritiesMapper(SimpleAuthorityMapper()) // no ROLE_ prefix
        return authenticationProvider
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth?.authenticationProvider(keycloakAuthenticationProvider())
    }

    @Bean
    open fun keycloakConfigResolver(): KeycloakConfigResolver = KeycloakSpringBootConfigResolver() // no keycloak.json

    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
            RegisterSessionAuthenticationStrategy(SessionRegistryImpl()) // use session for authenticated Users

    override fun configure(http: HttpSecurity?) {
        super.configure(http)
        http
                ?.authorizeRequests()
                ?.antMatchers("/products*")?.hasRole("ProductReader")
                ?.antMatchers("/customers*")?.hasRole("CustomerReader")
                ?.anyRequest()?.permitAll()
    }
}

@Configuration
open class OAuth2ClientConfiguration {

    @Bean
    open fun resourceOwnerPasswordResourceDetails(): ResourceOwnerPasswordResourceDetails {
        val resourceDetails = ResourceOwnerPasswordResourceDetails()
        resourceDetails.grantType = "password"
        resourceDetails.username = "vlad"
        resourceDetails.password = "vlad"
        resourceDetails.clientId = "ApiClient"
        resourceDetails.clientSecret = "743960ec-97b2-4cb8-85c8-2346ab96a9a3"
        resourceDetails.accessTokenUri = "http://localhost:9090/auth/realms/WebApplication/protocol/openid-connect/token"
        return resourceDetails
    }

    @Bean
    open fun clientCredentialsResourceDetails(): ClientCredentialsResourceDetails {
        val resourceDetails = ClientCredentialsResourceDetails()
        resourceDetails.clientId = "ApiClient"
        resourceDetails.clientSecret = "743960ec-97b2-4cb8-85c8-2346ab96a9a3"
        resourceDetails.accessTokenUri = "http://localhost:9090/auth/realms/WebApplication/protocol/openid-connect/token"
        return resourceDetails
    }
}
