package com.it332.principal.Controllers;

import java.util.List;

import javax.validation.Valid;

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

import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Models.School;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.SchoolService;

@RestController
@RequestMapping("/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @PostMapping("/create")
    public ResponseEntity<Object> createSchool(@Valid @RequestBody School school) {
        ErrorMessage err = new ErrorMessage("");
        try {
            School newSchool = schoolService.createSchool(school);
            return new ResponseEntity<>(newSchool, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to get school: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (Exception e) {
            // Catching any other unexpected exceptions
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<School>> getAllSchools() {
        List<School> allSchools = schoolService.getAllSchools();
        return new ResponseEntity<>(allSchools, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSchoolById(@Valid @PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            School school = schoolService.getSchoolById(id);
            if (school != null) {
                return new ResponseEntity<>(school, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to get school: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get school: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception e) {
            // Catching any other unexpected exceptions
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateSchool(@PathVariable String id, @RequestBody School updatedSchool) {
        ErrorMessage err = new ErrorMessage("");
        try {
            School updatedEntity = schoolService.updateSchool(id, updatedSchool);
            return ResponseEntity.ok(updatedEntity);
        } catch (IllegalArgumentException e) {
            err.setMessage("Failed to patch School: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            err.setMessage("School not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception e) {
            err.setMessage("Failed to delete school: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSchool(@PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            schoolService.deleteSchoolById(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (IllegalArgumentException e) {
            err.setMessage("Failed to delete school: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to delete school: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception e) {
            err.setMessage("Failed to delete school: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }
}
