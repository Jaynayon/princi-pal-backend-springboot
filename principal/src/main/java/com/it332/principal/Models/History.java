package com.it332.principal.Models;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.it332.principal.DTO.HistoryRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection = "History")
@AllArgsConstructor
@Data
public class History {
    @Id
    private String id;
    private String lrId;
    private String userId;
    private String documentsId;

    @JsonFormat(pattern = "MM/dd/yyyy hh:mm a")
    private Date updateDate;

    private String fieldName;
    private String oldValue;
    private String newValue;

    private boolean created;
    private boolean deleted;

    // Default constructor setting the updateDate to the current date and time in
    // the Philippines time zone
    public History() {
        setLrId(null);
        setUserId(null);
        setDocumentsId(null);
        this.updateDate = getCurrentPHDate(); // Set to current PH date and time
        setFieldName("");
        setOldValue("");
        setNewValue("");
        setCreated(false);
        setDeleted(false);
    }

    public History(HistoryRequest req) {
        setLrId(req.getLrId());
        setUserId(req.getUserId());
        setDocumentsId(req.getDocumentsId());
        this.updateDate = getCurrentPHDate(); // Set to current PH date and time
        setFieldName(req.getFieldName());
        setOldValue(req.getOldValue());
        setNewValue(req.getNewValue());
        setCreated(req.isCreated());
        setDeleted(req.isDeleted());
    }

    // Helper method to get current date and time in PH time zone
    private Date getCurrentPHDate() {
        ZonedDateTime phDateTime = ZonedDateTime.now(ZoneId.of("Asia/Manila"));
        return Date.from(phDateTime.toInstant());
    }

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
