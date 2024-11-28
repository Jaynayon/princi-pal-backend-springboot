package com.it332.principal.Models;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "Codes")
@NoArgsConstructor
@Data
public class Code {
    @Id
    public String id;
    private String code;
    private LocalDateTime tokenCreationDate;
    private String schoolId; // Link to User

    public Code(String code, String schoolId) {
        this.code = code;
        this.schoolId = schoolId;
        this.tokenCreationDate = LocalDateTime.now();
    }
}
