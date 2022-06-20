package com.trecapps.falsehoods.falsehoodReview.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trecapps.auth.models.TcUser;
import com.trecapps.auth.services.UserStorageService;
import com.trecapps.falsehoods.falsehoodReview.models.PublicFalsehood;
import com.trecapps.falsehoods.falsehoodReview.models.PublicFalsehoodRecords;
import com.trecapps.falsehoods.falsehoodReview.models.Record;
import com.trecapps.falsehoods.falsehoodReview.repos.PublicFalsehoodRecordsRepo;
import com.trecapps.falsehoods.falsehoodReview.repos.PublicFalsehoodRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class PublicFalsehoodsService {

    @Autowired
    PublicFalsehoodRepo repo;

    @Autowired
    PublicFalsehoodRecordsRepo cRepos;

    @Autowired
    UserStorageService uRepo;

    Logger logger = LoggerFactory.getLogger(PublicFalsehoodsService.class);

    public String addVerdict(BigInteger id, String approve, String comment, String userId)
    {
        if( !repo.existsById(id))
            return "404: Falsehood does not exist!";

        // Make Sure Request hasn't been rejected or approved
        PublicFalsehood f = repo.getById(id);
        int s = f.getStatus();
        if(s > 1)
            return "400: Falsehood already has a Final Verdict!";

        // To-Do: Once User Support is added, make sure that this user is not the submitting user
        if(userId.equals(f.getUserId()))
            return "403: Users cannot review Their own Falsehood Entries";

        // End To-DO

        Record record = new Record("Verdict", approve, new Date(Calendar.getInstance().getTime().getTime()), 0l, comment);

        List<Record> records;
        try {
            records = cRepos.retrieveRecords(id);
            records.add(record);
            PublicFalsehoodRecords fRecords = new PublicFalsehoodRecords();
            fRecords.setFalsehoodId(id);
            fRecords.setRecords(records);
            cRepos.save(fRecords);
        } catch (JsonProcessingException e) {
            logger.error("Poor JSON Data detected for attempt to {} Public Falsehood {} id", approve, id);
            return "500: Detected poorly formatted data for Falsehood Entry " + id;
        }

        int appCount = 0, safeRej = 0, penRej = 0;
        for(Record r: records)
        {
            if("Verdict".equals(r.getRecordType()))
            {
                switch(r.getDetails())
                {
                    case "Approved":
                        appCount++;
                        break;
                    case "Safe-Reject":
                        safeRej++;
                        break;
                    case "Penalize":
                        penRej++;
                }
            }
        }

        try{
        if(appCount >= (2 * (safeRej + penRej))) {
            f.setStatus((byte) 2);

            TcUser user = uRepo.retrieveUser(f.getUserId());
            user.setCredibilityRating(user.getCredibilityRating() + 5);
            uRepo.saveUser(user);
            logger.info("Public Falsehood {} has been approved!", id);
        }
        if((safeRej + penRej) >= (appCount * 2))
        {
            f.setStatus((byte)5);
            if(penRej > safeRej)
            {
                TcUser user = uRepo.retrieveUser(f.getUserId());
                user.setCredibilityRating(user.getCredibilityRating() - 5);
                uRepo.saveUser(user);
            }
            logger.info("Public Falsehood {} has been Rejected!", id);
        }
        } catch (Exception e)
        {
            logger.error(e.getMessage());
            return "500: " + e.getMessage();
        }
        repo.save(f);
        logger.info("Successfully added Verdict {} to Public Falsehood {}", approve, id);
        return "";
    }

}
