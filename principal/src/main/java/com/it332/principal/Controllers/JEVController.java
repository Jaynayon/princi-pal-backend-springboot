package com.it332.principal.Controllers;

import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.DTO.JEVRequest;
import com.it332.principal.DTO.JEVResponse;
import com.it332.principal.Models.JEV;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.JEVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/jev")
public class JEVController {

    @Autowired
    private JEVService jevService;

    ErrorMessage err = new ErrorMessage("");

    // Endpoint to create a new LR document
    @PostMapping("/create")
    public ResponseEntity<Object> createJEV(@RequestBody @Valid JEVRequest jev) {
        try {

            JEV savedLR = jevService.saveJEV(jev);
            return new ResponseEntity<>(savedLR, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to create JEV: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get JEV: " + e.getMessage());
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

    // Endpoint to retrieve all LR documents
    @GetMapping("/all")
    public ResponseEntity<Object> getAllLRs() {
        List<JEV> jevList = jevService.getAllJEVs();
        return new ResponseEntity<>(jevList, HttpStatus.OK);
    }

    @GetMapping("/documents/{documentsId}")
    public ResponseEntity<Object> getAllLRsByDocumentsId(@PathVariable String documentsId) {
        try {
            List<JEVResponse> jevList = jevService.getAllJEVsByDocumentsId(documentsId);
            return new ResponseEntity<>(jevList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to get LR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get LR: " + e.getMessage());
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

    // Endpoint to retrieve an LR document by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getLRById(@PathVariable String id) {
        try {
            JEV lr = jevService.getJEVById(id);
            return new ResponseEntity<>(lr, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to get LR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get LR: " + e.getMessage());
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

    // Endpoint to update an existing LR document
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateJEV(@PathVariable String id, @RequestBody @Valid JEVRequest updatedLR) {
        try {
            JEV updatedEntity = jevService.updateJEV(id, updatedLR);
            return ResponseEntity.ok(updatedEntity);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to patch LR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to patch LR: " + e.getMessage());
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

    // Endpoint to delete an LR document by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteJEV(@PathVariable String id) {
        try {
            jevService.deleteJEVById(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to delete LR: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to delete LR: " + e.getMessage());
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
}
