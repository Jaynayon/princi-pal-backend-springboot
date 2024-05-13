package com.it332.principal.Models;

import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mongodb.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Document(collection = "Uacs")
@Data
@AllArgsConstructor
public class Uacs {
    @Id
    private String id;
    @Indexed(unique = true)
    @NonNull
    @NotBlank(message = "Code is required")
    private String code;
    @NotBlank(message = "Name is required")
    private String name;
}
