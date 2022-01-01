package com.trecapps.falsehoods.falsehoodReview.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trecapps.base.InfoResource.config.StorageClient;
import com.trecapps.base.InfoResource.models.PublicFigureRecords;
import com.trecapps.base.InfoResource.models.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PublicFigureRecordRepo {
    @Autowired
    StorageClient client;

    ObjectMapper mapper = new ObjectMapper();

    public void save(PublicFigureRecords records) throws JsonProcessingException {
        if(records.getFigureId() == null)
            throw new NullPointerException("Null Public-Figure Id Provided!");

        String name = "Public-Figure-Records-" + records.getFigureId();

        client.SubmitJson(name, mapper.writeValueAsString(records.getRecords()), "Trec-Apps-Resource", "Resource");
    }

    public List<Record> retrieveRecords(long id) throws JsonProcessingException {
        String name = "Public-Figure-Records-" + id;

        String contents = client.getContents(name, "Resource").block();

        return mapper.readValue(contents, new TypeReference<List<Record>>() {
        });
    }
}
