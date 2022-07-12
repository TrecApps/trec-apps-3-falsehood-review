package com.trecapps.falsehoods.falsehoodReview.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trecapps.auth.models.TcUser;
import com.trecapps.auth.services.UserStorageService;
import com.trecapps.base.FalsehoodModel.models.Falsehood;
import com.trecapps.base.FalsehoodModel.models.FalsehoodRecords;
import com.trecapps.base.InfoResource.models.Record;
import com.trecapps.falsehoods.falsehoodReview.repos.FalsehoodRecordsRepo;
import com.trecapps.falsehoods.falsehoodReview.repos.FalsehoodRepo;
import com.trecapps.notifications.services.NotificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

@Service
public class MediaFalsehoodsService {
    @Autowired
    FalsehoodRepo repo;

    @Autowired
    FalsehoodRecordsRepo cRepos;

    @Autowired
    UserStorageService uRepo;

    @Autowired
    NotificationManager notificationManager;

    Logger logger = LoggerFactory.getLogger(PublicFalsehoodsService.class);

    public String addVerdict(BigInteger id, String approve, String comment, String userId)
    {
        // Make sure target falsehood is in repos
        if(!repo.existsById(id))
            return "404: Falsehood does not exist!";

        // Make Sure Request hasn't been rejected or approved
        Falsehood f = repo.getById(id);
        int s = f.getStatus();
        if(s > 1)
            return "400: Falsehood already has a Final Verdict!";

        if(userId.equals(f.getUserId()))
            return "403: Users cannot review Their own Falsehood Entries";

        Record record = new Record("Verdict", approve, new Date(Calendar.getInstance().getTime().getTime()), 0l, comment);
        List<Record> records;
        try {
            records = cRepos.retrieveRecords(id);
            records.add(record);
            FalsehoodRecords fRecords = new FalsehoodRecords();
            fRecords.setFalsehoodId(id);
            fRecords.setRecords(records);
            cRepos.save(fRecords);
        } catch (JsonProcessingException e) {
            logger.error("Poor JSON Data detected for attempt to {} Media Falsehood {} id", approve, id);
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
            logger.info("Media Falsehood {} has been approved!", id);

            notificationManager.notify(f.getUserId(), String.format("Congratulations! Your Media Falsehood:{} has been approved!", id), "Falsehoods");
        }
        else if((safeRej + penRej) >= (appCount * 2))
        {
            f.setStatus((byte)5);
            if(penRej > safeRej)
            {
                TcUser user = uRepo.retrieveUser(f.getUserId());
                user.setCredibilityRating(user.getCredibilityRating() - 5);
                uRepo.saveUser(user);
            }
            logger.info("Media Falsehood {} has been Rejected!", id);
            notificationManager.notify(f.getUserId(), String.format("Your Media Falsehood:{} has been Rejected!", id), "Falsehoods");
        }
        } catch (Exception e)
        {
            logger.error(e.getMessage());
            return "500: " + e.getMessage();
        }
        repo.save(f);
        logger.info("Successfully added Verdict {} to Media Falsehood {}", approve, id);
        return "";
    }
}
