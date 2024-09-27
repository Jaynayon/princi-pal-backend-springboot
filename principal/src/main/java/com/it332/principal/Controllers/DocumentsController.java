package com.it332.principal.Controllers;

import com.it332.principal.DTO.DocumentsPatch;
import com.it332.principal.DTO.DocumentsRequest;
import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Models.Documents;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "https://localhost:3000")
@RequestMapping("/documents")
public class DocumentsController {

    @Autowired
    private DocumentsService documentsService;

    // Endpoint to create a new document
    @PostMapping("/create")
    public ResponseEntity<Object> createDocument(@RequestBody @Valid DocumentsRequest document) {
        ErrorMessage err = new ErrorMessage("");
        try {
            DocumentsResponse savedDocument = documentsService.saveDocument(document);
            return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to create Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
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

    // Endpoint to create a new document
    @PostMapping("/initialize")
    public ResponseEntity<Object> initializeDocuments(@RequestBody @Valid DocumentsRequest document) {
        ErrorMessage err = new ErrorMessage("");
        try {
            DocumentsResponse savedDocument = documentsService.initializeDocuments(document);
            return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to create Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
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

    @GetMapping("/school/{school}/{year}/{month}")
    public ResponseEntity<Object> getDocumentBySchoolYearMonth(@PathVariable String school,
            @PathVariable String year,
            @PathVariable String month) throws Exception {
        ErrorMessage err = new ErrorMessage("");
        try {
            DocumentsResponse document = documentsService.getDocumentBySchoolYearMonth(school, year, month);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
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

    @GetMapping("/school/{school}/{year}")
    public ResponseEntity<Object> getDocumentBySchoolYearMonth(@PathVariable String school,
            @PathVariable String year) throws Exception {
        ErrorMessage err = new ErrorMessage("");
        try {
            List<Documents> document = documentsService.getDocumentsBySchoolYear(school, year);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
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

    // Endpoint to retrieve all documents
    @GetMapping("/all")
    public ResponseEntity<List<Documents>> getAllDocuments() {
        List<Documents> documentsList = documentsService.getAllDocuments();
        return new ResponseEntity<>(documentsList, HttpStatus.OK);
    }

    // Endpoint to retrieve a document by ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> getDocumentById(@PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            Documents document = documentsService.getDocumentById(id);
            if (document != null) {
                return new ResponseEntity<>(document, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate school name is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get Document: " + e.getMessage());
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
    public ResponseEntity<Object> updateDocument(@PathVariable String id,
            @RequestBody @Valid DocumentsPatch updatedSchool) {
        ErrorMessage err = new ErrorMessage("");
        try {
            Documents updatedEntity = documentsService.updateDocument(id, updatedSchool);
            return ResponseEntity.ok(updatedEntity);
        } catch (IllegalArgumentException e) {
            err.setMessage("Failed to patch Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            err.setMessage("Document not found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception e) {
            err.setMessage(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteDocument(@PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            documentsService.deleteDocumentById(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } catch (IllegalArgumentException e) {
            err.setMessage("Failed to delete Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to delete Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(err);
        } catch (Exception e) {
            err.setMessage("Failed to delete Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(err);
        }
    }

}
