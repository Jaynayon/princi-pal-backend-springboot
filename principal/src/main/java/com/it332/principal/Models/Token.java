package com.it332.principal.Models;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Tokens")
public class Token {
    @Id
    private String id;
    private String token;
    private LocalDateTime tokenCreationDate;
    private String userId; // Link to User
    private TokenType type; // Token type (for password reset or email verification)

    // Enum to represent the type of token
    public enum TokenType {
        PASSWORD_RESET,
        EMAIL_VERIFICATION
    }

    // Constructors
    public Token() {
    }

    public Token(String token, LocalDateTime tokenCreationDate, String userId, TokenType type) {
        this.token = token;
        this.tokenCreationDate = tokenCreationDate;
        this.userId = userId;
        this.type = type;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getTokenCreationDate() {
        return tokenCreationDate;
    }

    public void setTokenCreationDate(LocalDateTime tokenCreationDate) {
        this.tokenCreationDate = tokenCreationDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }
}
