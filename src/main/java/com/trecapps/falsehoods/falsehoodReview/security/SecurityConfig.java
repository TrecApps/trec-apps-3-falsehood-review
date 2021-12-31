package com.trecapps.falsehoods.falsehoodReview.security;

import com.azure.spring.aad.webapp.AADOAuth2UserService;
import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.trecapps.base.FalsehoodModel.repos.FalsehoodUserRepo;
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
    SecurityConfig(AADAuthenticationProperties aadAuthProps,
                    FalsehoodUserRepo falsehoodUserRepo)
    {
        aadoAuth2UserService = new AADOAuth2UserService(aadAuthProps);
        this.falsehoodUserRepo = falsehoodUserRepo;
    }
    FalsehoodUserRepo falsehoodUserRepo;
    AADOAuth2UserService aadoAuth2UserService;


    @Override
    protected void configure(HttpSecurity security) throws Exception
    {
        security
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .oidcUserService(getTrecDirectoryService());
    }

    @Bean
    protected TrecActiveDirectoryService getTrecDirectoryService()
    {
        return new TrecActiveDirectoryService(aadoAuth2UserService, falsehoodUserRepo);
    }

}
