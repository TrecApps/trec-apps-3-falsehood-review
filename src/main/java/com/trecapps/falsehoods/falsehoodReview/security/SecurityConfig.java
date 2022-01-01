package com.trecapps.falsehoods.falsehoodReview.security;

//import com.azure.spring.aad.webapp.AADOAuth2UserService;
//import com.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.trecapps.base.FalsehoodModel.models.FalsehoodUser;
import com.trecapps.falsehoods.falsehoodReview.repos.FalsehoodUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@EnableWebSecurity
@Configuration
public class SecurityConfig // extends WebSecurityConfigurerAdapter
{
    @Value("${azure.activedirectory.tenant-id}")
    String tenentId;

    @Value("${azure.activedirectory.client-id}")
    String clientId;
    //@Autowired
    FalsehoodUserRepo falsehoodUserRepo;

    Map<Integer, String> creditAuthorities;

    @Autowired
    public SecurityConfig(FalsehoodUserRepo userRepo)
    {
        falsehoodUserRepo = userRepo;

        creditAuthorities.put(5, "Submitter");
        creditAuthorities.put(65, "Reviewer");
        creditAuthorities.put(200, "Editor");
    }

    @Bean
    public SecurityWebFilterChain getFilterChain(ServerHttpSecurity http)
    {
        http.authorizeExchange((exchanges) -> exchanges
                .anyExchange().authenticated().and()
                .oauth2ResourceServer().jwt()
                .jwtDecoder(jwtDecoder())
                .jwtAuthenticationConverter(grantedAuthoritiesExtractor()));


        return http.build();
    }

    ReactiveJwtDecoder jwtDecoder() {
        String issuerUri = "" + tenentId;
        NimbusReactiveJwtDecoder ret = (NimbusReactiveJwtDecoder)
                ReactiveJwtDecoders.fromIssuerLocation(issuerUri);
        OAuth2TokenValidator<Jwt> tokenValidator = new DelegatingOAuth2TokenValidator<Jwt>(
                JwtValidators.createDefaultWithIssuer(issuerUri),
                new JwtClaimValidator<List<String>>("aud", (aud) -> {
                    return aud != null && aud.contains(clientId);
                }));

        ret.setJwtValidator(tokenValidator);

        return ret;
    }




    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
                ((jwt) -> {

                    var claims = jwt.getClaims();

                    FalsehoodUser user1 = null;
                    String userSub = claims.getOrDefault("sub", "").toString();

                    if(!falsehoodUserRepo.existsById(userSub))
                    {
                        // Start with 5 points Credibility
                        user1 = new FalsehoodUser(userSub, 5);
                        user1 = falsehoodUserRepo.save(user1);
                    }
                    else
                        user1 = falsehoodUserRepo.getById(userSub);

                    Collection<?> authorities = (Collection<?>)
                            jwt.getClaims().getOrDefault("roles", Collections.emptyList());

                    FalsehoodUser finalUser = user1;
                    List<GrantedAuthority> newAuth = new ArrayList<>();
                    creditAuthorities.forEach((Integer i, String s) -> {
                        if(finalUser.getCredibility() >= i)
                            newAuth.add(new SimpleGrantedAuthority(s));
                    });

                    newAuth.addAll(authorities.stream()
                            .map(Object::toString)
                            //.map(role -> "ROLE_".concat(role))	// TODO: seriously? find a better way
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));

                    return newAuth;
                });
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
