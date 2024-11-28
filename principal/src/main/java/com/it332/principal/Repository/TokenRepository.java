package com.it332.principal.Repository;

import com.it332.principal.Models.Token;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

    // Custom query to find Token by userId and type
    @Query("{ 'userId': ?0, 'type': ?1 }")
    Optional<Token> findByUserIdAndType(String userId, String type);

    // Find token by the token string
    Optional<Token> findByToken(String token);
}
