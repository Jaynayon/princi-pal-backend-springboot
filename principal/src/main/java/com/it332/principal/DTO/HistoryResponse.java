package com.it332.principal.DTO;

import com.it332.principal.Models.History;
import com.it332.principal.Models.LR;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HistoryResponse {
    private String id;
    private String lrId;
    private UserDetails user;
    private String documentsId;

    // @JsonFormat(pattern = "MM/dd/yyyy hh:mm a")
    private String updateDate;

    private String fieldName;
    private String oldValue;
    private String newValue;

    private boolean created;
    private boolean deleted;

    private LR lrCopy;

    public HistoryResponse() {
    }

    public HistoryResponse(History req) {
        setId(req.getId());
        setLrId(req.getLrId());
        setDocumentsId(req.getDocumentsId());
        setUpdateDate(req.getUpdateDate());
        setFieldName(req.getFieldName());
        setOldValue(req.getOldValue());
        setNewValue(req.getNewValue());
        setCreated(req.isCreated());
        setDeleted(req.isDeleted());
        setLrCopy(req.getLrCopy());
    }
}
