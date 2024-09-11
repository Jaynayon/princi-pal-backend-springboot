package com.it332.principal.DTO;

import com.it332.principal.Models.User;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDetails {
    private String id;
    private String fname;
    private String mname;
    private String lname;
    private String position;
    private String email;

    public UserDetails() {
    }

    public UserDetails(User user) {
        setId(user.getId());
        setFname(user.getFname());
        setMname(user.getMname());
        setLname(user.getLname());
        setPosition(user.getPosition());
        setEmail(user.getEmail());
    }
}
