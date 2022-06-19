package com.trecapps.falsehoods.falsehoodReview.models;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Container(containerName = "institutions", ru = "400")
@ToString
public class InstitutionRecords
{
    Long institutionId;

    //@PartitionKey
    byte partition;

    List<Record> records;
}
