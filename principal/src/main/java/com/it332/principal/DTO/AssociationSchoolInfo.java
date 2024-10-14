package com.it332.principal.DTO;

import com.it332.principal.Models.School;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssociationSchoolInfo {
    private String assocId;
    private String name;
    private String fullname;

    public AssociationSchoolInfo(String id, School school) {
        this.assocId = id;
        this.name = school.getName();
        this.fullname = school.getFullName();
    }
}
