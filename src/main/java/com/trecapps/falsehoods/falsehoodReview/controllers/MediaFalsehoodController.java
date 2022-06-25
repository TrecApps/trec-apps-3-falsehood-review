package com.trecapps.falsehoods.falsehoodReview.controllers;

import com.trecapps.auth.models.TcUser;
import com.trecapps.auth.services.UserStorageService;
import com.trecapps.falsehoods.falsehoodReview.services.MediaFalsehoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/Media")
public class MediaFalsehoodController extends FalsehoodControllerBase{

    MediaFalsehoodsService mediaFalsehoodsService;

    Logger logger = LoggerFactory.getLogger(MediaFalsehoodController.class);

    @Autowired
    public MediaFalsehoodController(UserStorageService userStorageService1,
                                    MediaFalsehoodsService mediaFalsehoodsService1) {
        super(userStorageService1);
        mediaFalsehoodsService = mediaFalsehoodsService1;
    }

    @PostMapping(value = "/Approve", consumes = MediaType.APPLICATION_JSON_VALUE
            ,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> approveFalsehood(@RequestBody ReviewEntry request)
    {
        TcUser user = null;

        try {
            user = getUserDetails(SecurityContextHolder.getContext());
        }catch (Exception e)
        {
            return new ResponseEntity<String>
                    (e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(user.getCredibilityRating() < MIN_CREDIT_SUBMIT_NEW)
            return new ResponseEntity<String>
                    ("Your Credibility Is too low. Please build up your credibility to 60 points before reviewing other falsehoods!",
                            HttpStatus.FORBIDDEN);
        BigInteger id = null;
        try
        {
            id = new BigInteger(request.getFalsehood());
        } catch (Exception e)
        {
            logger.error("Failed to Get a Public Falsehood id for Approval", e);
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        logger.info("Attempting to Approve Public Falsehood {}!", id);
        String results = mediaFalsehoodsService.addVerdict(id, "Approved", request.getComment(), user.getId());
        return this.getResult(results);
    }

    @PostMapping(value = "/Reject", consumes = MediaType.APPLICATION_JSON_VALUE
            ,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> rejectFalsehood(@RequestBody ReviewEntry request)
    {
        TcUser user = null;

        try {
            user = getUserDetails(SecurityContextHolder.getContext());
        }catch (Exception e)
        {
            return new ResponseEntity<String>
                    (e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(user.getCredibilityRating() < MIN_CREDIT_SUBMIT_NEW)
            return new ResponseEntity<String>
                    ("Your Credibility Is too low. Please build up your credibility to 60 points before reviewing other falsehoods!",
                            HttpStatus.FORBIDDEN);
        BigInteger id = null;
        try
        {
            id = new BigInteger(request.getFalsehood());
        } catch (Exception e)
        {
            logger.error("Failed to Get a Public Falsehood id for Soft Rejection", e);
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        logger.info("Attempting to Reject Public Falsehood {}!", id);
        String results = mediaFalsehoodsService.addVerdict(id, "Safe-Reject", request.getComment(), user.getId());
        return this.getResult(results);
    }


    @PostMapping(value = "/Penalize", consumes = MediaType.APPLICATION_JSON_VALUE
        ,produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> penalizeFalsehood(@RequestBody ReviewEntry request)
    {
        TcUser user = null;

        try {
            user = getUserDetails(SecurityContextHolder.getContext());
        }catch (Exception e)
        {
            return new ResponseEntity<String>
                    (e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if(user.getCredibilityRating() < MIN_CREDIT_SUBMIT_NEW)
            return new ResponseEntity<String>
                    ("Your Credibility Is too low. Please build up your credibility to 60 points before reviewing other falsehoods!",
                            HttpStatus.FORBIDDEN);
        BigInteger id = null;
        try
        {
            id = new BigInteger(request.getFalsehood());
        } catch (Exception e)
        {
            logger.error("Failed to Get a Public Falsehood id for Hard Rejection", e);
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }
        logger.info("Attempting to Penalize Public Falsehood {}!", id);
        String results = mediaFalsehoodsService.addVerdict(id, "Penalize", request.getComment(), user.getId());
        return this.getResult(results);
    }
}
