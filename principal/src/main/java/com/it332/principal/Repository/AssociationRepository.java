package com.it332.principal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.Association;

public interface AssociationRepository extends MongoRepository<Association, String> {
    Association findByUserIdAndSchoolId(String userId, String schoolId);

    // Get association by school.id and user.id
    Association findBySchoolIdAndUserId(String schoolId, String userId);
}
