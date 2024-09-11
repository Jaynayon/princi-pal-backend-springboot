package com.it332.principal.Controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
import com.it332.principal.DTO.UserAssociation;
import com.it332.principal.Models.Association;
import com.it332.principal.Services.AssociationService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/associations")
public class AssociationController {

    @Autowired
    private AssociationService associationService;

    @GetMapping("/all")
    public ResponseEntity<List<Association>> getAllAssociation() {
        List<Association> associationOptional = associationService.getAllAssociations();

        return ResponseEntity.ok().body(associationOptional);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Association> getAssociationById(@PathVariable String id) {
        Association associationOptional = associationService.getAssociationById(id);

        return ResponseEntity.ok().body(associationOptional);

        // return associationOptional.map(association ->
        // ResponseEntity.ok().body(association))
        // .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Association> createAssociation(@RequestBody Association association) {
        Association createdAssociation = associationService.createAssociation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssociation);
    }

    @PostMapping("/user")
    public ResponseEntity<UserAssociation> getUserAssociation(@RequestBody AssociationIdRequest association) {
        UserAssociation createdAssociation = associationService.getUserAssocation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssociation);
    }

    @PostMapping("/insert")
    public ResponseEntity<Association> insertUserToAssociation(@RequestBody AssociationIdRequest association) {
        // Assuming the association object contains the necessary information to invite
        // a user
        Association updatedAssociation = associationService.insertUserToAssociation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
    }

    @PostMapping("/approve")
    public ResponseEntity<Association> approveUserToAssociation(@RequestBody AssociationIdRequest association) {
        // Assuming the association object contains the necessary information to invite
        // a user
        Association updatedAssociation = associationService.approveUserToAssociation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
    }

    @PatchMapping("/promote")
    public ResponseEntity<Association> promoteAssociation(@RequestBody AssociationIdRequest association) {
        Association updateAssociation = associationService.promoteAssociation(association);
        return ResponseEntity.ok(updateAssociation);
    }

    @PatchMapping("/demote")
    public ResponseEntity<Association> demoteAssociation(@RequestBody AssociationIdRequest association) {
        Association updateAssociation = associationService.demoteAssociation(association);
        return ResponseEntity.ok(updateAssociation);
    }

    @DeleteMapping("/{userId}/{schoolId}")
    public ResponseEntity<?> deleteAssociation(@PathVariable String userId, @PathVariable String schoolId) {
        associationService.deleteAssociation(schoolId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssociationById(@PathVariable String id) {
        associationService.deleteAssociationById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/apply")
    public ResponseEntity<Association> applyToSchool(@RequestBody AssociationIdRequest associationRequest) {
        Association createdAssociation = associationService.applyToSchool(associationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssociation);
    }

    /*@PostMapping("/invite")
    public ResponseEntity<Association> inviteUserToAssociation(@RequestBody AssociationIdRequest association) {
        // Assuming the association object contains the necessary information to invite
        // a user
        Association updatedAssociation = associationService.inviteUserToAssociation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
    }*/


    // working invite
    /* 
    @PostMapping("/invite")
    public ResponseEntity<?> inviteUserToAssociation(@RequestBody AssociationIdRequest association) {
    try {
        Association updatedAssociation = associationService.inviteUserToAssociation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error processing invitation: " + e.getMessage());
    }
}*/

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

    /*@PostMapping("/accept")
    public ResponseEntity<?> acceptUserToSchool(@RequestBody AssociationIdRequest associationRequest) {
        try {
            // Call the service to accept the user
            Association acceptedAssociation = associationService.acceptUserToSchool(associationRequest);

            // Return the accepted association with status 200 (OK)
            return ResponseEntity.ok(acceptedAssociation);
        } catch (IllegalStateException e) {
            // Return a conflict if no application is found or the user is already approved/invited
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            // Return an internal server error for unexpected cases
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error accepting user to school.");
        }
    }*/

    @GetMapping("/applications/{schoolId}")
    public ResponseEntity<List<Association>> getApplicationsForSchool(@PathVariable String schoolId) {
        // Only return applications where 'invitation' is false
        List<Association> applications = associationService.getApplicationsForSchool(schoolId)
            .stream()
            .filter(association -> !association.isInvitation()) // Ensure it's an application, not an invite
            .collect(Collectors.toList());

        if (applications.isEmpty()) {
            return ResponseEntity.noContent().build(); // No applications
        }

        return ResponseEntity.ok(applications);
    }

}
