package com.it332.principal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.User;

public interface UserRepository extends MongoRepository<User, String> {
    // Define custom query methods here if needed
    User findByEmail(String email);

    User findByUsername(String username);

}