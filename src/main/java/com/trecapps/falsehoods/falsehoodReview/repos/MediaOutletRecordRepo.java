package com.trecapps.falsehoods.falsehoodReview.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trecapps.base.InfoResource.config.StorageClient;
import com.trecapps.base.InfoResource.models.MediaOutletRecords;
import com.trecapps.base.InfoResource.models.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaOutletRecordRepo {
    @Autowired
    StorageClient client;

    ObjectMapper mapper = new ObjectMapper();

    public void save(MediaOutletRecords records) throws JsonProcessingException {
        if(records.getOutletId() == null)
            throw new NullPointerException("Null Media-Outlet Id Provided!");

        String name = "Media-Outlet-Records-" + records.getOutletId();

        client.SubmitJson(name, mapper.writeValueAsString(records.getRecords()), "Trec-Apps-Resource", "Resource");
    }

    public List<Record> retrieveRecords(long id) throws JsonProcessingException {
        String name = "Media-Outlet-Records-" + id;

        String contents = client.getContents(name, "Resource").block();

        return mapper.readValue(contents, new TypeReference<List<Record>>() {
        });
    }
}
