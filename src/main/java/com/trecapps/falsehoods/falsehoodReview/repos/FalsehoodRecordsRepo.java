package com.trecapps.falsehoods.falsehoodReview.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trecapps.base.FalsehoodModel.models.FalsehoodRecords;
import com.trecapps.falsehoods.falsehoodReview.config.StorageClient;
import com.trecapps.base.InfoResource.models.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public class FalsehoodRecordsRepo //extends CosmosRepository<FalsehoodRecords, BigInteger>
{
    @Autowired
    StorageClient client;

    ObjectMapper mapper = new ObjectMapper();

    public void save(FalsehoodRecords records) throws JsonProcessingException {
        if(records.getFalsehoodId() == null)
            throw new NullPointerException("Null Falsehood Id Provided!");

        String name = "Falsehood-Records-" + records.getFalsehoodId();

        client.SubmitJson(name, mapper.writeValueAsString(records.getRecords()), "Trec-Apps-Falsehood", "Falsehood");
    }

    public List<Record> retrieveRecords(BigInteger id) throws JsonProcessingException {
        String name = "Falsehood-Records-" + id;

        String contents = client.getContents(name, "Falsehood").getBody();

        return mapper.readValue(contents, new TypeReference<List<Record>>() {
        });
    }

}
