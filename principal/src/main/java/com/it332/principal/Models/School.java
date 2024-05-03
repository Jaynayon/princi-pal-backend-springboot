package com.it332.principal.Models;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection = "Schools")
@Data
@AllArgsConstructor
public class School {
    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    @NotBlank(message = "Name is required")
    private String name;
}
