package com.it332.principal.Models;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;

@Document(collection = "Schools")
@AllArgsConstructor
public class School {
    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    @NotBlank(message = "Name is required")
    private String name;
    @Indexed(unique = true)
    @NonNull
    @NotBlank(message = "Full Name is required")
    private String fullName;

    public School() {
    }

    // public School(String id, @Valid @NotBlank(message = "Name is required")
    // String name,
    // @Valid @NotBlank(message = "Full Name is required") String fullName) {
    // this.id = id;
    // this.name = name;
    // this.fullName = fullName;
    // }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

}
