package com.it332.principal.Repository;

import com.it332.principal.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;



@Repository
public interface ForgotPasswordRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByToken(String token); // Ensure this returns Optional<User>
}
