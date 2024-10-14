package com.it332.principal.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.Association;

public interface AssociationRepository extends MongoRepository<Association, String> {
    // Association findByUserIdAndSchoolId(String userId, String schoolId);
    List<Association> findByUserId(String userId);

    List<Association> findByUserIdAndApprovedFalseAndInvitationFalse(String userId);

    List<Association> findBySchoolId(String schoolId);

    // Get association by school.id and user.id
    Association findBySchoolIdAndUserId(String schoolId, String userId);

    List<Association> findByApprovedTrue();

    List<Association> findBySchoolIdAndApprovedFalseAndInvitationFalse(String schoolId);

    // In your AssociationRepository
    Association findBySchoolIdAndUserIdAndApprovedFalseAndInvitationFalse(String schoolId, String userId);

    List<Association> findBySchoolIdAndApprovedTrue(String schoolId);
}
