package com.it332.principal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HistoryRequest {
    private String lrId;
    private String userId;
    private String documentsId;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private boolean created;
    private boolean deleted;
}
