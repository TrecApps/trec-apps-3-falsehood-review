package com.trecapps.falsehoods.falsehoodReview.security;

import com.trecapps.auth.services.TrecAccountService;
import com.trecapps.auth.services.TrecSecurityContext;
import com.trecapps.falsehoods.falsehoodReview.repos.FalsehoodUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    SecurityConfig(TrecAccountService trecAccountService1, TrecSecurityContext trecSecurityContext1)
    {
        trecAccountService = trecAccountService1;
        trecSecurityContext = trecSecurityContext1;
    }
    TrecAccountService trecAccountService;
    TrecSecurityContext trecSecurityContext;

    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        security.csrf().disable()
                .authorizeRequests()
                .anyRequest()
                .hasAuthority("EMAIL_VERIFIED")
                .and()
                .userDetailsService(trecAccountService)
                .securityContext().securityContextRepository(trecSecurityContext)
        ;
    }

}
