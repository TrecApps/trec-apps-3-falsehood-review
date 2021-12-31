package com.trecapps.falsehoods.falsehoodReview.security;

import com.azure.spring.aad.webapp.AADOAuth2UserService;
import com.trecapps.base.FalsehoodModel.models.FalsehoodUser;
import com.trecapps.base.FalsehoodModel.repos.FalsehoodUserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;


public class TrecActiveDirectoryService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    AADOAuth2UserService aadoAuth2UserService;

    FalsehoodUserRepo falsehoodUserRepo;

    public TrecActiveDirectoryService(AADOAuth2UserService aadoAuth2UserService1, FalsehoodUserRepo falsehoodUserRepo1)
    {
        aadoAuth2UserService = aadoAuth2UserService1;
        falsehoodUserRepo = falsehoodUserRepo1;
    }


    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = aadoAuth2UserService.loadUser(userRequest);

        String userSub = user.getSubject();
        FalsehoodUser user1 = null;
        if(!falsehoodUserRepo.existsById(userSub))
        {
            // Start with 5 points Credibility
            user1 = new FalsehoodUser(userSub, 5);
            user1 = falsehoodUserRepo.save(user1);
        }
        else
            user1 = falsehoodUserRepo.getById(userSub);

        user.getClaims().put("FalsehoodUser", user1);

        return user;
    }
}
