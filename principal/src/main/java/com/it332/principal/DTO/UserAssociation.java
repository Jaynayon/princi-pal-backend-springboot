package com.it332.principal.DTO;

import com.it332.principal.Models.Association;
import com.it332.principal.Models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAssociation {
    private String id;
    private String email;
    private String fname;
    private String mname;
    private String lname;
    private String position;
    private Boolean admin;

    public UserAssociation(User user, Association association) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.fname = user.getFname();
        this.mname = user.getMname();
        this.lname = user.getLname();
        this.position = user.getPosition();
        this.admin = association.isAdmin();
    }
}
