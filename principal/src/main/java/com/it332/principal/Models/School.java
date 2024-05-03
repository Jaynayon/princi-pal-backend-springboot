package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection = "Schools")
@Data
@AllArgsConstructor
public class School {
    @Id
    private String id;
    private String name;
}
