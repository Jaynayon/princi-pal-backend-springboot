package com.it332.principal.DTO;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.it332.principal.Models.History;
import com.it332.principal.Models.LR;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HistoryResponse {
    private String lrId;
    private UserDetails user;
    private String documentsId;

    @JsonFormat(pattern = "MM/dd/yyyy hh:mm a")
    private Date updateDate;

    private String fieldName;
    private String oldValue;
    private String newValue;

    private boolean created;
    private boolean deleted;

    private LR lrCopy;

    public HistoryResponse() {
    }

    public HistoryResponse(History req) {
        setLrId(req.getLrId());
        setDocumentsId(req.getDocumentsId());
        setFieldName(req.getFieldName());
        setOldValue(req.getOldValue());
        setNewValue(req.getNewValue());
        setCreated(req.isCreated());
        setDeleted(req.isDeleted());
        setLrCopy(req.getLrCopy());
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
