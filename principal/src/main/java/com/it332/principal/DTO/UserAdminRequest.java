package com.it332.principal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAdminRequest {
    private String adminId;
    private String fname;
    private String mname;
    private String lname;
    private String username;
    private String email;
    private String password;
    private String position;

}
