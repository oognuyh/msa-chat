package com.oognuyh.configservice.config

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@Configuration
class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .csrf()
                .disable()
            .authorizeRequests()
            .antMatchers("/actuator/**")
                .permitAll()
            .anyRequest()
                .authenticated()
            .and()
                .httpBasic()
    }
}