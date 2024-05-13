package com.it332.principal.Repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.it332.principal.Models.Association;

@Repository
public interface AssociationRepository extends MongoRepository<Association, String> {
    Optional<Association> findByUserAndSchool(String userId, String schoolId);

    Optional<Association> findBySchoolIdAndUserId(String schoolId, String userId);
}
