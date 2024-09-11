package com.it332.principal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssociationEmailRequest {
    private String email;
    private String schoolId;
}
