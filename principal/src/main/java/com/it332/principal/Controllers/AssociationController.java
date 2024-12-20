package com.it332.principal.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it332.principal.DTO.AssociationEmailRequest;
import com.it332.principal.DTO.AssociationIdRequest;
import com.it332.principal.DTO.AssociationReferral;
import com.it332.principal.DTO.AssociationSchoolInfo;
import com.it332.principal.DTO.UserAssociation;
import com.it332.principal.Models.Association;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.AssociationService;

@RestController
@RequestMapping("/api/associations")
public class AssociationController {

    @Autowired
    private AssociationService associationService;

    @GetMapping("/all")
    public ResponseEntity<Object> getAllAssociation() {
        try {
            List<Association> associationOptional = associationService.getAllAssociations();
            return ResponseEntity.ok().body(associationOptional);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getAssociationById(@PathVariable String id) {
        try {
            Association associationOptional = associationService.getAssociationById(id);

            return ResponseEntity.ok().body(associationOptional);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createAssociation(@RequestBody Association association) {
        try {
            Association createdAssociation = associationService.createAssociation(association);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/user")
    public ResponseEntity<Object> getUserAssociation(@RequestBody AssociationIdRequest association) {
        try {
            UserAssociation createdAssociation = associationService.createUserAssocation(association);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<Object> insertUserToAssociation(@RequestBody AssociationIdRequest association) {
        try {
            // Assuming the association object contains the necessary information to invite
            // a user
            Association updatedAssociation = associationService.insertUserToAssociation(association);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/referral")
    public ResponseEntity<Object> acceptUserReferral(@RequestBody AssociationReferral association) {
        try {
            // Assuming the association object contains the necessary information to invite
            // a user
            Association newAssociation = associationService.acceptUserReferral(association);
            return ResponseEntity.status(HttpStatus.CREATED).body(newAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/approve")
    public ResponseEntity<Object> approveUserToAssociation(@RequestBody AssociationIdRequest association) {
        try {
            // Assuming the association object contains the necessary information to invite
            // a user
            Association updatedAssociation = associationService.approveUserToAssociation(association);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/reject")
    public ResponseEntity<?> rejectUserFromAssociation(@RequestBody AssociationIdRequest association) {
        try {
            associationService.rejectUserFromAssociation(association);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @PostMapping("/approve/{notificationId}")
    public ResponseEntity<?> approveInvitation(
            @PathVariable String notificationId) {
        try {
            // Call the service to approve the invitation
            Association updatedAssociation = associationService.approveInvitation(notificationId);
            return ResponseEntity.ok(updatedAssociation);
        } catch (IllegalArgumentException e) {
            // Return a bad request response with a specific error message
            return ResponseEntity.badRequest().body("Invalid notification ID: " + e.getMessage());
        } catch (IllegalStateException e) {
            // Return a bad request response with a specific error message
            return ResponseEntity.badRequest().body("Invalid association state: " + e.getMessage());
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Server error: " + e.getMessage());
        }
    }

    @PatchMapping("/promote")
    public ResponseEntity<Object> promoteAssociation(@RequestBody AssociationIdRequest association) {
        try {
            Association updateAssociation = associationService.promoteAssociation(association);
            return ResponseEntity.ok(updateAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Association not found: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @PatchMapping("/demote")
    public ResponseEntity<Object> demoteAssociation(@RequestBody AssociationIdRequest association) {
        try {
            Association updateAssociation = associationService.demoteAssociation(association);
            return ResponseEntity.ok(updateAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}/{schoolId}")
    public ResponseEntity<?> deleteAssociation(@PathVariable String userId, @PathVariable String schoolId) {
        try {
            associationService.deleteAssociation(schoolId, userId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssociationById(@PathVariable String id) {
        try {
            associationService.deleteAssociationById(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{notificationId}")
    public ResponseEntity<String> deleteAssociation(@PathVariable String notificationId) {
        try {
            // Call the service method to delete the association
            associationService.deleteAssociation(notificationId);

            // Return a success response
            return ResponseEntity.ok("Association related to notification ID " + notificationId + " has been deleted.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Return a bad request response if there's an error with the request
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<Object> applyToSchool(@RequestBody AssociationIdRequest associationRequest) {
        try {
            Association createdAssociation = associationService.applyToSchool(associationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAssociation);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    /*
     * @PostMapping("/invite")
     * public ResponseEntity<Association> inviteUserToAssociation(@RequestBody
     * AssociationIdRequest association) {
     * // Assuming the association object contains the necessary information to
     * invite
     * // a user
     * Association updatedAssociation =
     * associationService.inviteUserToAssociation(association);
     * return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
     * }
     */

    // working invite
    /*
     * @PostMapping("/invite")
     * public ResponseEntity<?> inviteUserToAssociation(@RequestBody
     * AssociationIdRequest association) {
     * try {
     * Association updatedAssociation =
     * associationService.inviteUserToAssociation(association);
     * return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
     * } catch (Exception e) {
     * return ResponseEntity.status(HttpStatus.BAD_REQUEST).
     * body("Error processing invitation: " + e.getMessage());
     * }
     * }
     */

    @PostMapping("/invite")
    public ResponseEntity<?> inviteUserToAssociation(@RequestBody AssociationEmailRequest associationRequest) {

        try {
            // Call the service to invite the user
            Association newAssociation = associationService.inviteUserToAssociation(associationRequest);
            // Return the created association with status 201 (Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(newAssociation);
        } catch (IllegalArgumentException e) {
            // Return a bad request if validation fails
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalStateException e) {
            // Return a conflict if the user is already invited or associated
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // Return an internal server error for unexpected cases
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inviting user to association.");
        }
    }

    /*
     * @PostMapping("/accept")
     * public ResponseEntity<?> acceptUserToSchool(@RequestBody AssociationIdRequest
     * associationRequest) {
     * try {
     * // Call the service to accept the user
     * Association acceptedAssociation =
     * associationService.acceptUserToSchool(associationRequest);
     * 
     * // Return the accepted association with status 200 (OK)
     * return ResponseEntity.ok(acceptedAssociation);
     * } catch (IllegalStateException e) {
     * // Return a conflict if no application is found or the user is already
     * approved/invited
     * return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
     * } catch (Exception e) {
     * // Return an internal server error for unexpected cases
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
     * body("Error accepting user to school.");
     * }
     * }
     */

    @GetMapping("/applications/{schoolId}")
    public ResponseEntity<Object> getApplicationsForSchool(@PathVariable String schoolId) {
        try {
            // Fetch applications using the updated service method
            List<UserAssociation> applications = associationService.getApplicationsForSchool(schoolId);

            if (applications.isEmpty()) {
                return ResponseEntity.noContent().build(); // No applications
            }

            return ResponseEntity.ok(applications);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/applications/user/{userId}")
    public ResponseEntity<Object> getApplicationsForUser(@PathVariable String userId) {
        try {
            // Fetch applications using the updated service method
            List<AssociationSchoolInfo> applications = associationService.getAppliedSchools(userId);

            if (applications.isEmpty()) {
                return ResponseEntity.noContent().build(); // No applications
            }

            return ResponseEntity.ok(applications);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getAssociationsByUserId(@PathVariable String userId) {
        try {
            List<Association> associations = associationService.getAssociationsByUserId(userId);
            if (associations.isEmpty()) {
                return ResponseEntity.noContent().build(); // No associations found for this user
            }
            return ResponseEntity.ok(associations);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }

}
