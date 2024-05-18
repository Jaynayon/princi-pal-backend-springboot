package com.it332.principal.Controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it332.principal.DTO.AssociationIdRequest;
import com.it332.principal.Models.Association;
import com.it332.principal.Services.AssociationService;

@RestController
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

    @PostMapping("/invite")
    public ResponseEntity<Association> inviteUserToAssociation(@RequestBody AssociationIdRequest association) {
        // Assuming the association object contains the necessary information to invite
        // a user
        Association updatedAssociation = associationService.inviteUserToAssociation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
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

    // @PutMapping("/update")
    // public ResponseEntity<Association> updateAssociation(@PathVariable String id,
    // @RequestBody Association association){
    // Association updateAssociation = associationService.updateAssociation(id,
    // association);
    // return ResponseEntity.ok(updateAssociation);
    // }

    @DeleteMapping("/{userId}/{schoolId}")
    public ResponseEntity<?> deleteAssociation(@PathVariable String userId, @PathVariable String schoolId) {
        associationService.deleteAssociation(userId, schoolId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAssociationById(@PathVariable String id) {
        associationService.deleteAssociationById(id);
        return ResponseEntity.noContent().build();
    }
}
