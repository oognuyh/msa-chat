package com.oognuyh.notificationservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .authorizeRequests()
                .antMatchers("/ws/**")
                .permitAll()
                .anyRequest()
                .authenticated()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
    }

    private fun jwtAuthenticationConverter(): Converter<Jwt, out AbstractAuthenticationToken> {
        val jwtConverter = JwtAuthenticationConverter()

        jwtConverter.setJwtGrantedAuthoritiesConverter(RealmRoleConverter())

        return jwtConverter
    }

    inner class RealmRoleConverter : Converter<Jwt, Collection<GrantedAuthority>> {

        @Suppress("UNCHECKED_CAST")
        override fun convert(jwt: Jwt): Collection<GrantedAuthority>? {
            val realmAccess: Map<String, List<String>> = jwt.claims["realm_access"] as Map<String, List<String>>

            return realmAccess["roles"]?.map { "ROLE_${it}" }?.map { SimpleGrantedAuthority(it) }
        }
    }
}