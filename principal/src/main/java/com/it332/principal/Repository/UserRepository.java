package com.it332.principal.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.User;

public interface UserRepository extends MongoRepository<User, String> {
    // Define custom query methods here if needed
    User findByEmail(String email);

    User findByUsername(String username);

    User findByToken(String token);

    List<User> findAllById(Iterable<String> ids);
}