package com.trecapps.falsehoods.falsehoodReview.repos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trecapps.base.InfoResource.config.StorageClient;
import com.trecapps.base.InfoResource.models.Record;
import com.trecapps.base.InfoResource.models.RegionRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RegionRecordsRepo {

    @Autowired
    StorageClient client;

    ObjectMapper mapper = new ObjectMapper();

    public void save(RegionRecords records) throws JsonProcessingException {
        if(records.getRegionId() == null)
            throw new NullPointerException("Null Region Id Provided!");

        String name = "Region-Records-" + records.getRegionId();

        client.SubmitJson(name, mapper.writeValueAsString(records.getRecords()), "Trec-Apps-Resource", "Resource");
    }

    public List<Record> retrieveRecords(long id) throws JsonProcessingException {
        String name = "Region-Records-" + id;

        String contents = client.getContents(name, "Resource").block();

        return mapper.readValue(contents, new TypeReference<List<Record>>() {
        });
    }


}
