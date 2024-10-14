package com.it332.principal.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.AssociationEmailRequest;
import com.it332.principal.DTO.AssociationIdRequest;
import com.it332.principal.DTO.AssociationSchoolInfo;
import com.it332.principal.DTO.UserAssociation;
import com.it332.principal.DTO.UserResponse;
import com.it332.principal.Models.Association;
import com.it332.principal.Models.Notification;
import com.it332.principal.Models.School;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.AssociationRepository;
import com.it332.principal.Repository.NotificationRepository;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Security.NotFoundException;

import java.util.ArrayList;

@Service
public class AssociationService extends Exception {

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private SchoolService schoolService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Association> getAllAssociations() {
        return associationRepository.findAll();
    }

    public Association getAssociationById(String userId) {
        return associationRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Association not found with User ID: " + userId));
    }

    public Association getAssociationByUserIdAndSchoolId(AssociationIdRequest association) {
        // Check if user or school exists
        School existSchool = schoolService.getSchoolById(association.getSchoolId());
        UserResponse existUser = userService.getUserAssociationsById(association.getUserId());

        // Check if the association already exists for the given schoolId and userId
        return associationRepository.findBySchoolIdAndUserId(existSchool.getId(), existUser.getId());
    }

    public UserAssociation getUserAssocation(AssociationIdRequest association) {
        try {
            // Get user association
            Association existingAssociation = getAssociationByUserIdAndSchoolId(association);

            // Get user
            User existingUser = userService.getUserById(association.getUserId());

            return new UserAssociation(existingUser, existingAssociation);
        } catch (Exception e) {
            throw e;
        }
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

    // public Boolean checkPrincipalAssociation(String schoolId) {
    // // Get user associations for school
    // List<User> users = schoolService.getUsersBySchoolId(schoolId);

    // for (User user : users) {
    // //User user = userService.getUserById(association.getUserId());
    // if ("Principal".equals(user.getPosition())) {
    // return true;
    // }
    // }

    // return false;
    // }

    // public List<Association> getBySchoolId(String schoolId) {
    // return associationRepository.findBySchoolId(schoolId);
    // }

    public void deleteAssociation(String schoolId, String userId) {
        Association association = associationRepository.findBySchoolIdAndUserId(schoolId, userId);

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

    // This service is for inserting a user, preferrably a principal, directly to
    // the school/association
    public Association insertUserToAssociation(AssociationIdRequest association) {
        // Check if user or school exists
        School existSchool = schoolService.getSchoolById(association.getSchoolId());
        UserResponse existUser = userService.getUserAssociationsById(association.getUserId());

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
                existingAssociation.setInvitation(false);
                // Remove fields that are confidential
                existingAssociation.setApproved(true);
                existingAssociation.setAdmin(true);

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
        newAssociation.setInvitation(false);
        newAssociation.setApproved(true);
        newAssociation.setAdmin(true);

        // Save and return the new association
        return associationRepository.save(newAssociation);
    }

    /*public Association inviteUserToAssociation(AssociationIdRequest association) {
        // Check if user or school exists
        School existSchool = schoolService.getSchoolById(association.getSchoolId());
        UserResponse existUser = userService.getUserAssociationsById(association.getUserId());

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
    }*/
    
    public Association promoteAssociation(AssociationIdRequest association) {
        // Check if user or school exists
        School existSchool = schoolService.getSchoolById(association.getSchoolId());
        UserResponse existUser = userService.getUserAssociationsById(association.getUserId());

        // Check if the association already exists for the given schoolId and userId
        Association existingAssociation = associationRepository.findBySchoolIdAndUserId(
                existSchool.getId(), existUser.getId());

        if (!existingAssociation.isAdmin()) {
            existingAssociation.setAdmin(true);
        }
        return associationRepository.save(existingAssociation);
    }

    public Association demoteAssociation(AssociationIdRequest association) {
        // Check if user or school exists
        School existSchool = schoolService.getSchoolById(association.getSchoolId());
        UserResponse existUser = userService.getUserAssociationsById(association.getUserId());

        // Check if the association already exists for the given schoolId and userId
        Association existingAssociation = associationRepository.findBySchoolIdAndUserId(
                existSchool.getId(), existUser.getId());

        if (existingAssociation.isAdmin()) {
            existingAssociation.setAdmin(false);
        }
        return associationRepository.save(existingAssociation);
    }

    public Association applyToSchool(AssociationIdRequest associationRequest) {
        // Check if the school exists
        School existingSchool = schoolService.getSchoolById(associationRequest.getSchoolId());
        // Check if the user exists
        UserResponse existingUser = userService.getUserAssociationsById(associationRequest.getUserId());

        // Check if the association already exists for the given schoolId and userId
        Association existingAssociation = associationRepository.findBySchoolIdAndUserId(
                existingSchool.getId(), existingUser.getId());

        if (existingAssociation != null) {
            // If the association already exists, return it
            return existingAssociation;
        }

        // If the association doesn't exist, create a new one with 'applied' status
        Association newAssociation = new Association();
        newAssociation.setSchoolId(existingSchool.getId());
        newAssociation.setUserId(existingUser.getId());
        newAssociation.setInvitation(false);    
        newAssociation.setApproved(false);
        newAssociation.setAdmin(false);

        // Save and return the new association
        return associationRepository.save(newAssociation);
    }

    public Association approveUserToAssociation(AssociationIdRequest associationRequest) {
        // Check if the school exists
        School existingSchool = schoolService.getSchoolById(associationRequest.getSchoolId());
    
        // Check if the user exists
        UserResponse existingUser = userService.getUserAssociationsById(associationRequest.getUserId());
    
        // Check if the association exists where the user has applied but not yet approved or invited
        Association existingAssociation = associationRepository.findBySchoolIdAndUserIdAndApprovedFalseAndInvitationFalse(
                existingSchool.getId(), existingUser.getId());
    
        // Handle the case where no association exists
        if (existingAssociation == null) {
            // If no association exists, throw an exception
            throw new IllegalStateException("No application found for this user or user already invited/approved.");
        }
    
        // Update the association to mark the user as accepted
        existingAssociation.setApproved(true);
    
        // Save the updated association
        return associationRepository.save(existingAssociation);
    }  
    
    public void rejectUserFromAssociation(AssociationIdRequest associationRequest) {
        // Check if the school exists
        School existingSchool = schoolService.getSchoolById(associationRequest.getSchoolId());
    
        // Check if the user exists
        UserResponse existingUser = userService.getUserAssociationsById(associationRequest.getUserId());
    
        // Check if the association exists where the user has applied but not yet approved or invited
        Association existingAssociation = associationRepository.findBySchoolIdAndUserIdAndApprovedFalseAndInvitationFalse(
                existingSchool.getId(), existingUser.getId());
    
        // Handle the case where no association exists
        if (existingAssociation == null) {
            throw new IllegalStateException("No application found for this user or user already invited/approved.");
        }
    
        // Delete the association
        associationRepository.delete(existingAssociation);
    }
    

    public Association approveInvitation(String notificationId) {
        // Validate and fetch the notification
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid notificationId: " + notificationId));
        
        // Extract the assocId from the notification
        String assocId = notification.getAssocId();
        
        if (assocId == null || assocId.isEmpty()) {
            throw new IllegalStateException("Association ID is missing from the notification.");
        }
        
        // Fetch the existing association
        Association existingAssociation = associationRepository.findById(assocId)
            .orElseThrow(() -> new IllegalStateException("No association found for assocId: " + assocId));
        
        // Update the association status and save
        existingAssociation.setApproved(true);
        Association updatedAssociation = associationRepository.save(existingAssociation);
        
        // You might need to add logic to handle user association here.
        // Example: addUserToAssociation(userId, updatedAssociation);
        
        return updatedAssociation;
    }
    
    public void deleteAssociation(String notificationId) {
        // Validate and fetch the notification
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid notificationId: " + notificationId));
        
        // Extract the assocId from the notification
        String assocId = notification.getAssocId();
        
        if (assocId == null || assocId.isEmpty()) {
            throw new IllegalStateException("Association ID is missing from the notification.");
        }
        
        // Fetch the existing association
        Association existingAssociation = associationRepository.findById(assocId)
            .orElseThrow(() -> new IllegalStateException("No association found for assocId: " + assocId));
        
        // Delete the association
        associationRepository.delete(existingAssociation);
    }
    
    

     
    
    
    /*public List<Association> getApplicationsForSchool(String schoolId) {
    return associationRepository.findBySchoolIdAndApprovedFalseAndInvitationFalse(schoolId);
    }*/

    public List<UserAssociation> getApplicationsForSchool(String schoolId) {
    // Fetch associations that are not approved and not invitations
    List<Association> associations = associationRepository.findBySchoolIdAndApprovedFalseAndInvitationFalse(schoolId);

    // Extract user IDs from associations
    List<String> userIds = associations.stream()
            .map(Association::getUserId)
            .distinct() // Ensure no duplicate user IDs
            .collect(Collectors.toList());

    // Get all users for the extracted IDs
    List<User> schoolUsers = userRepository.findAllById(userIds);

    // Map each user to their respective association to create UserAssociation DTOs
    return schoolUsers.stream()
            .map(user -> {
                // Find the corresponding association for the user
                Association assoc = associations.stream()
                        .filter(a -> a.getUserId().equals(user.getId()))
                        .findFirst()
                        .orElseThrow(
                                () -> new RuntimeException("Association not found for user ID: " + user.getId()));

                // Create and return UserAssociation DTO
                return new UserAssociation(user, assoc);
            })
            .collect(Collectors.toList());
}

    public Association inviteUserToAssociation(AssociationEmailRequest associationRequest) {
        if (associationRequest.getEmail() == null || associationRequest.getSchoolId() == null) {
            throw new IllegalArgumentException("Email and School ID are required.");
        }
    
        User existingUser = userService.getUserByEmail(associationRequest.getEmail());
    
        Association existingAssociation = associationRepository.findBySchoolIdAndUserId(
                associationRequest.getSchoolId(), existingUser.getId());
    
        if (existingAssociation != null) {
            if (existingAssociation.isInvitation()) {
                throw new IllegalStateException("User has already been invited.");
            } else {
                throw new IllegalStateException("User is already associated with this school.");
            }
        }
    
        Association newAssociation = new Association();
        newAssociation.setUserId(existingUser.getId());
        newAssociation.setSchoolId(associationRequest.getSchoolId());
        newAssociation.setInvitation(true);
        newAssociation.setApproved(false);
        newAssociation.setAdmin(associationRequest.getAdmin() != null && associationRequest.getAdmin()); // Set admin status from request
    
        return associationRepository.save(newAssociation);
    }    

    public List<Association> getAssociationsByUserId(String userId) {
        return associationRepository.findByUserId(userId);
    }

    public List<AssociationSchoolInfo> getAppliedSchools(String userId) {
        List<Association> associations = associationRepository.findByUserIdAndApprovedFalseAndInvitationFalse(userId);

        List<AssociationSchoolInfo> schoolIds = associations.stream()
                                     .map(association -> new AssociationSchoolInfo(association.getId(), schoolService.getSchoolById(association.getSchoolId())))
                                     .collect(Collectors.toList());
        
        return schoolIds;
    }

}
