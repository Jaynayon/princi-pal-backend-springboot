package com.it332.principal.DTO;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HistoryResponse {
    private String lrId;
    private String userId;
    private String documentsId;

    @JsonFormat(pattern = "MM/dd/yyyy hh:mm a")
    private Date updateDate;

    private String fieldName;
    private String oldValue;
    private String newValue;

    public String getUpdateDate() {
        if (updateDate != null) {
            // Convert UTC Date to PH Time and format it
            ZonedDateTime utcZonedDateTime = ZonedDateTime.ofInstant(updateDate.toInstant(), ZoneId.of("UTC"));
            ZonedDateTime phZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Manila"));
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
            return phZonedDateTime.format(formatter); // Format Date object as "MM/dd/yyyy" string
        }
        return null; // Return null if date is null (handle this case as needed)
    }
}
