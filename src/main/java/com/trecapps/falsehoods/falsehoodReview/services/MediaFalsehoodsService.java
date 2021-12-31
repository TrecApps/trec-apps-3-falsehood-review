package com.trecapps.falsehoods.falsehoodReview.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trecapps.base.FalsehoodModel.models.Falsehood;
import com.trecapps.base.FalsehoodModel.models.FalsehoodRecords;
import com.trecapps.base.FalsehoodModel.models.FalsehoodUser;
import com.trecapps.base.FalsehoodModel.repos.FalsehoodRecordsRepo;
import com.trecapps.base.FalsehoodModel.repos.FalsehoodRepo;
import com.trecapps.base.FalsehoodModel.repos.FalsehoodUserRepo;
import com.trecapps.base.InfoResource.models.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class MediaFalsehoodsService {
    @Autowired
    FalsehoodRepo repo;

    @Autowired
    FalsehoodRecordsRepo cRepos;

    @Autowired
    FalsehoodUserRepo uRepo;

    public String addVerdict(BigInteger id, String approve, String comment)
    {
        // Make sure target falsehood is in repos
        if(!repo.existsById(id))
            return "404: Falsehood does not exist!";

        // Make Sure Request hasn't been rejected or approved
        Falsehood f = repo.getById(id);
        int s = f.getStatus();
        if(s > 1)
            return "400: Falsehood already has a Final Verdict!";

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
            e.printStackTrace();
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

        if(appCount >= (2 * (safeRej + penRej))) {
            f.setStatus((byte) 2);
            FalsehoodUser user = uRepo.getById(f.getUserId());
            user.setCredibility(user.getCredibility() + 5);
            uRepo.save(user);
        }
        else if((safeRej + penRej) >= (appCount * 2))
        {
            f.setStatus((byte)5);
            if(penRej > safeRej)
            {
                FalsehoodUser user = uRepo.getById(f.getUserId());
                user.setCredibility(user.getCredibility() - 5);
                uRepo.save(user);
            }
        }
        repo.save(f);

        return "";
    }
}
