package com.trecapps.falsehoods.falsehoodReview.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trecapps.auth.models.TcUser;
import com.trecapps.auth.models.TrecAuthentication;
import com.trecapps.auth.models.primary.TrecAccount;
import com.trecapps.auth.services.UserStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;

public class FalsehoodControllerBase {
    public static final int MIN_CREDIT_SUBMIT_NEW = 5;

    public static final int MIN_CREDIT_APPROVE_REJECT = 60;

    public static final int MIN_CREDIT_APPROVE_REJECT_RESOURCE = 200;

    public static final int MIN_CREDIT_ADD_OUTLET = 40;
    UserStorageService userStorageService;

    public FalsehoodControllerBase(UserStorageService userStorageService1)
    {
        this.userStorageService = userStorageService1;
    }

    protected TcUser getUserDetails(SecurityContext context) throws JsonProcessingException {
        TrecAuthentication auth = (TrecAuthentication) context.getAuthentication();
        TrecAccount account = auth.getAccount();
        return userStorageService.retrieveUser(account.getId());
    }

    protected ResponseEntity<String> getResult(String result)
    {
        if(result.length() > 2)
        switch(result.substring(0, 3)) {
            case "400":
                return new ResponseEntity<>(result.substring(4).trim(), HttpStatus.BAD_REQUEST);
            case "404":
                return new ResponseEntity<>(result.substring(4).trim(), HttpStatus.NOT_FOUND);
            case "500":
                return new ResponseEntity<>(result.substring(4).trim(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
