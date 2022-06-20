package com.trecapps.falsehoods.falsehoodReview.controllers;

import com.trecapps.auth.models.TcUser;
import com.trecapps.auth.services.UserStorageService;
import com.trecapps.falsehoods.falsehoodReview.services.PublicFalsehoodsService;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/Review/Public")
public class PublicFalsehoodController extends FalsehoodControllerBase{
    PublicFalsehoodsService publicFalsehoodsService;

    Logger logger = LoggerFactory.getLogger(PublicFalsehoodController.class);

    @Autowired
    public PublicFalsehoodController(UserStorageService userStorageService1,
                                     PublicFalsehoodsService publicFalsehoodsService1) {
        super(userStorageService1);
        publicFalsehoodsService = publicFalsehoodsService1;
    }

    @PostMapping(value = "/Approve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> approveFalsehood(RequestEntity<MultiValueMap<String, String>> request)
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
        MultiValueMap<String, String> values = request.getBody();
        BigInteger id = null;
        
        try
        {
            id = new BigInteger(values.getFirst("Falsehood"));
        } catch (Exception e)
        {
            logger.error("Failed to Get a Public Falsehood id for Approval", e);
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        logger.info("Attempting to Approve Public Falsehood {}!", id);
        String results = publicFalsehoodsService.addVerdict(id, "Approved", values.getFirst("Comment"), user.getId());
        return this.getResult(results);
    }

    @PostMapping(value = "/Reject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> rejectFalsehood(RequestEntity<MultiValueMap<String, String>> request)
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
        MultiValueMap<String, String> values = request.getBody();
        BigInteger id = null;
        try
        {
            id = new BigInteger(values.getFirst("Falsehood"));
        } catch (Exception e)
        {
            logger.error("Failed to Get a Public Falsehood id for Soft Rejection", e);
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        logger.info("Attempting to Reject Public Falsehood {}!", id);
        String results = publicFalsehoodsService.addVerdict(id, "Safe-Reject", values.getFirst("Comment"), user.getId());
        return this.getResult(results);
    }


    @PostMapping(value = "/Penalize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> penalizeFalsehood(RequestEntity<MultiValueMap<String, String>> request)
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
        MultiValueMap<String, String> values = request.getBody();
        BigInteger id = null;
        try
        {
            id = new BigInteger(values.getFirst("Falsehood"));
        } catch (Exception e)
        {
            logger.error("Failed to Get a Public Falsehood id for Hard Rejection", e);
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        logger.info("Attempting to Penalize Public Falsehood {}!", id);
        String results = publicFalsehoodsService.addVerdict(id, "Penalize", values.getFirst("Comment"), user.getId());
        return this.getResult(results);
    }
}
