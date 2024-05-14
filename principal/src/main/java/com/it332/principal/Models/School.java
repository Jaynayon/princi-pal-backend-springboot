package com.it332.principal.Models;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

@Document(collection = "Schools")
public class School {
    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    @NotBlank(message = "Name is required")
    private String name;

    public School() {
    }

    public School(String id, @NotBlank(message = "Name is required") String name) {
        this.id = id;
        this.name = name;
    }

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

}
