package com.trecapps.falsehoods.falsehoodReview.controllers;

import com.trecapps.base.FalsehoodModel.models.FalsehoodUser;
import com.trecapps.falsehoods.falsehoodReview.services.MediaFalsehoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
@RequestMapping("/Review/Media")
public class MediaFalsehoodController extends FalsehoodControllerBase{

    @Autowired
    MediaFalsehoodsService mediaFalsehoodsService;

    @PostMapping(value = "/Approve", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> approveFalsehood(RequestEntity<MultiValueMap<String, String>> request,
                                                   @AuthenticationPrincipal OidcUser principal)
    {
        FalsehoodUser user = principal.getClaim("FalsehoodUser");
        if(user.getCredibility() < MIN_CREDIT_SUBMIT_NEW)
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
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        String results = mediaFalsehoodsService.addVerdict(id, "Approved", values.getFirst("Comment"));
        return this.getResult(results);
    }

    @PostMapping(value = "/Reject", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> rejectFalsehood(RequestEntity<MultiValueMap<String, String>> request,
                                                  @AuthenticationPrincipal OidcUser principal)
    {
        FalsehoodUser user = principal.getClaim("FalsehoodUser");
        if(user.getCredibility() < MIN_CREDIT_SUBMIT_NEW)
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
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        String results = mediaFalsehoodsService.addVerdict(id, "Safe-Reject", values.getFirst("Comment"));
        return this.getResult(results);
    }


    @PostMapping(value = "/Penalize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> penalizeFalsehood(RequestEntity<MultiValueMap<String, String>> request,
                                                    @AuthenticationPrincipal OidcUser principal)
    {
        FalsehoodUser user = principal.getClaim("FalsehoodUser");
        if(user.getCredibility() < MIN_CREDIT_SUBMIT_NEW)
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
            return new ResponseEntity<String>("Could not derive an id from the Falsehood field!", HttpStatus.BAD_REQUEST);
        }

        String results = mediaFalsehoodsService.addVerdict(id, "Penalize", values.getFirst("Comment"));
        return this.getResult(results);
    }
}
