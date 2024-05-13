package com.it332.principal.Controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it332.principal.Models.Association;
import com.it332.principal.Services.AssociationService;

@RestController
@RequestMapping("/associations")
public class AssociationController {

    @Autowired
    private AssociationService associationService;

    @GetMapping("/{id}")
    public ResponseEntity<Association> getAssociationById(@PathVariable String id) {
        Optional<Association> associationOptional = associationService.getAssociationById(id);
        return associationOptional.map(association -> ResponseEntity.ok().body(association))
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Association> createAssociation(@RequestBody Association association) {
        Association createdAssociation = associationService.createAssociation(association);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAssociation);
    }
    
    @PostMapping("/invite")
    public ResponseEntity<Association> inviteUserToAssociation(@RequestBody Association association) {
    // Assuming the association object contains the necessary information to invite a user
    Association updatedAssociation = associationService.inviteUserToAssociation(association);
    return ResponseEntity.status(HttpStatus.CREATED).body(updatedAssociation);
}
    @PutMapping("/update")
    public ResponseEntity<Association> updateAssociation(@PathVariable String id, @RequestBody Association association){
        Association updateAssociation = associationService.updateAssociation(id, association);
        return ResponseEntity.ok(updateAssociation);
    }
    @DeleteMapping("/{userId}/{schoolId}")
    public ResponseEntity<?> deleteAssociation(@PathVariable String userId, @PathVariable String schoolId){
        associationService.deleteAssociation(userId, schoolId);
        return ResponseEntity.noContent().build();
    }
}
