package com.it332.principal.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.Association;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.AssociationRepository;

@Service
public class AssociationService {
    
    @Autowired
    private AssociationRepository associationRepository;

    public List<Association> getAllAssociations() {
        return associationRepository.findAll();
    }

    public Optional<Association> getAssociationById(String userId) {
        return associationRepository.findById(userId);
    }

    public Association createAssociation(Association association) {
        return associationRepository.save(association);
    }
    
    public Association updateAssociation(String userId, Association association) {
        association.setUserId(userId);
        return associationRepository.save(association);
    }

    public void deleteAssociation(String userId, String schoolId) {
        Optional<Association> associationOptional = associationRepository.findByUserAndSchool(userId, schoolId);
        associationOptional.ifPresent(association -> associationRepository.delete(association));
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

    public Association inviteUserToAssociation(Association association) {
        // Check if the association already exists for the given schoolId and userId
        Optional<Association> existingAssociation = associationRepository.findBySchoolIdAndUserId(association.getSchoolId(), association.getUserId());
        
        if (existingAssociation.isPresent()) {
            Association currentAssociation = existingAssociation.get();
            
            // If the association already exists and is approved, no need to invite again
            if (currentAssociation.isApproved()) {
                // Return the existing association without modification
                return currentAssociation;
            } else {
                // If the association exists but is not approved, update invitation status to true
                currentAssociation.setInvitation(true);
                // Remove fields that are confidential
                currentAssociation.setApproved(false);
                currentAssociation.setAdmin(false);
                
                // Save the updated association
                return associationRepository.save(currentAssociation);
            }
        } else {
            // If the association doesn't exist, create a new one without setting invitation status
            association.setSchool(association.getSchool());
            association.setUser(association.getUser());
            
            Association newAssociation = associationRepository.save(association);
            // Remove fields that are confidential
            newAssociation.setApproved(false);
            newAssociation.setAdmin(false);
            
            // Return the new association
            return newAssociation;
        }
    }
}    
