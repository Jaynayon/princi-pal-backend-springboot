package com.it332.principal.Controllers;

import com.it332.principal.Models.Documents;
import com.it332.principal.Services.DocumentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentsController {

    @Autowired
    private DocumentsService documentsService;

    // Endpoint to create a new document
    @PostMapping("/create")
    public ResponseEntity<Documents> createDocument(@RequestBody Documents document) {
        Documents savedDocument = documentsService.saveDocument(document);
        return new ResponseEntity<>(savedDocument, HttpStatus.CREATED);
    }

    @GetMapping("/{school}/lrs/{year}/{month}")
    public ResponseEntity<Documents> getDocumentBySchoolYearMonth(@PathVariable String school,
            @PathVariable String year,
            @PathVariable String month) throws Exception {
        Documents document = documentsService.getDocumentBySchoolYearMonth(school, year, month);
        return new ResponseEntity<>(document, HttpStatus.OK);
    }

    // Endpoint to retrieve all documents
    @GetMapping("/all")
    public ResponseEntity<List<Documents>> getAllDocuments() {
        List<Documents> documentsList = documentsService.getAllDocuments();
        return new ResponseEntity<>(documentsList, HttpStatus.OK);
    }

    // Endpoint to retrieve a document by ID
    @GetMapping("/{id}")
    public ResponseEntity<Documents> getDocumentById(@PathVariable String id) {
        Documents document = documentsService.getDocumentById(id);
        if (document != null) {
            return new ResponseEntity<>(document, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Add more endpoints as needed (e.g., update and delete)

}
