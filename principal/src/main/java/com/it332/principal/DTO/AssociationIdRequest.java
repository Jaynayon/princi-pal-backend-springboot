package com.it332.principal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssociationIdRequest {
    private String userId;
    private String schoolId;
}
