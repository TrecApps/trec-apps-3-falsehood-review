package com.trecapps.falsehoods.falsehoodReview.security;

import com.azure.spring.aad.webapp.AADOAuth2UserService;
import com.trecapps.base.FalsehoodModel.repos.FalsehoodUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@Component
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AADOAuth2UserService aadoAuth2UserService;

    @Autowired
    FalsehoodUserRepo falsehoodUserRepo;

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
