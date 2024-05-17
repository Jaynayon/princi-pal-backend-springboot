package com.it332.principal.Services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.AssociationIdRequest;
import com.it332.principal.DTO.UserResponse;
import com.it332.principal.Models.Association;
import com.it332.principal.Models.School;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.AssociationRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class AssociationService {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;

    public List<Association> getAllAssociations() {
        return associationRepository.findAll();
    }

    public Association getAssociationById(String userId) {
        return associationRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Association not found with User ID: " + userId));
    }

    public Association createAssociation(Association association) {
        return associationRepository.save(association);
    }

    // public Association updateAssociation(String userId, Association association)
    // {
    // // get if user exists

    // //provide logic
    // association.setUserId(userId);
    // return associationRepository.save(association);
    // }

    public void deleteAssociation(String userId, String schoolId) {
        Association association = associationRepository.findBySchoolIdAndUserId(userId, schoolId);

        if (association != null) {
            associationRepository.delete(association);
        }
    }

    public void deleteAssociationById(String id) {
        Association association = associationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Association not found with User ID: " + id));

        associationRepository.delete(association);
    }

    public List<User> searchMembers(String query) {
        // Implement user search logic here based on the provided query
        // For example:
        // return userRepository.findMembersByUsernameContaining(query);
        // Replace 'UserRepository' with your actual repository for users
        // Make sure to return a list of users who are members and matching the query
        // If no members are found, return an empty list
        throw new UnsupportedOperationException("Method 'searchMembers' is not yet implemented");
    }

    public Association inviteUserToAssociation(AssociationIdRequest association) {
        // Check if user or school exists
        School existSchool = schoolService.getSchoolById(association.getSchoolId());
        UserResponse existUser = userService.getUserById(association.getUserId());

        // Check if the association already exists for the given schoolId and userId
        Association existingAssociation = associationRepository.findBySchoolIdAndUserId(
                existSchool.getId(), existUser.getId());

        if (existingAssociation != null) {
            // If the association already exists and is approved, no need to invite again
            if (existingAssociation.isApproved()) {
                // Return the existing association without modification
                return existingAssociation;
            } else {
                // If the association exists but is not approved, update invitation status to
                // true
                existingAssociation.setInvitation(true);
                // Remove fields that are confidential
                existingAssociation.setApproved(false);
                existingAssociation.setAdmin(false);

                // Save the updated association
                return associationRepository.save(existingAssociation);
            }
        }
        // If the association doesn't exist, create a new one without setting invitation
        // status
        Association newAssociation = new Association();

        newAssociation.setSchoolId(existSchool.getId());
        newAssociation.setUserId(existUser.getId());

        // Remove fields that are confidential
        newAssociation.setInvitation(true);
        newAssociation.setApproved(false);
        newAssociation.setAdmin(false);

        // Save and return the new association
        return associationRepository.save(newAssociation);
    }

    public Association approveUserToAssociation(AssociationIdRequest association) {
        // Check if user or school exists
        Association updatedAssociation = new Association();
        School existSchool = schoolService.getSchoolById(association.getSchoolId());
        UserResponse existUser = userService.getUserById(association.getUserId());

        // Check if the association already exists for the given schoolId and userId
        Association existingAssociation = associationRepository.findBySchoolIdAndUserId(
                existSchool.getId(), existUser.getId());

        if (existingAssociation != null) {
            // If the association already exists and is approved, no need to invite again
            if (existingAssociation.isApproved()) {
                // Return the existing association without modification
                return existingAssociation;
            } else {
                // If the association exists but is not approved, update invitation status to
                // true
                // existingAssociation.setInvitation(true);
                // Remove fields that are confidential
                existingAssociation.setApproved(true);
                // existingAssociation.setAdmin(false);

                // Save the updated association
                updatedAssociation = associationRepository.save(existingAssociation);
            }
        } else {
            throw new NotFoundException("Association not found");
        }

        // Save and return the new association
        return updatedAssociation;
    }
}
