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
import com.it332.principal.Models.Uacs;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.UacsService;

@RestController
@RequestMapping("/uacs")
public class UacsController {

    @Autowired
    private UacsService uacsService;

    @PostMapping("/create")
    public ResponseEntity<Object> createUacs(@Valid @RequestBody Uacs uacs) {
        ErrorMessage err = new ErrorMessage("");
        try {
            Uacs newUacs = uacsService.createUacs(uacs);
            return new ResponseEntity<>(newUacs, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to create uacs: " + e.getMessage());
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
    public ResponseEntity<List<Uacs>> getAllUacs() {
        List<Uacs> allUacs = uacsService.getAllSchools();
        return new ResponseEntity<>(allUacs, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUacsById(@Valid @PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            Uacs uacs = uacsService.getUacsById(id);
            if (uacs != null) {
                return new ResponseEntity<>(uacs, HttpStatus.OK);
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
    public ResponseEntity<Object> updateUacs(@PathVariable String id, @RequestBody Uacs updatedUacs) {
        ErrorMessage err = new ErrorMessage("");
        try {
            Uacs updatedEntity = uacsService.updateUacs(id, updatedUacs);
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
            uacsService.deleteSchoolById(id);
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
