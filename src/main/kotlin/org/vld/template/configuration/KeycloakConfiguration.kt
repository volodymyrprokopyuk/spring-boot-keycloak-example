package org.vld.template.configuration

import org.keycloak.adapters.KeycloakConfigResolver
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy

@EnableWebSecurity
open class KeycloakConfiguration : KeycloakWebSecurityConfigurerAdapter() {

    @Autowired
    open fun configureGlobal(auth: AuthenticationManagerBuilder) {
        val authenticationProvider = KeycloakAuthenticationProvider()
        authenticationProvider.setGrantedAuthoritiesMapper(SimpleAuthorityMapper()) // no ROLE_ prefix
        auth.authenticationProvider(authenticationProvider)
    }

    @Bean
    open fun keycloakConfigResolver(): KeycloakConfigResolver = KeycloakSpringBootConfigResolver() // no keycloak.json

    override fun sessionAuthenticationStrategy(): SessionAuthenticationStrategy =
            RegisterSessionAuthenticationStrategy(SessionRegistryImpl())

    override fun configure(http: HttpSecurity?) {
        super.configure(http)
        http
                ?.authorizeRequests()
                ?.antMatchers("/products*")?.hasRole("ProductReader")
                ?.antMatchers("/customers*")?.hasRole("CustomerReader")
                ?.anyRequest()?.permitAll()
    }
}
