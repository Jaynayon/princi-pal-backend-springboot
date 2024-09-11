package com.it332.principal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExcelRequest {
    private String userId;
    private String schoolId;
    private String documentId;
    private String month;
    private String year;
}
