package com.it332.principal.Controllers;

import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.DTO.LRRequest;
import com.it332.principal.DTO.LRResponse;
import com.it332.principal.DTO.StackedBarDTO;
import com.it332.principal.Models.LR;
import com.it332.principal.Models.LRJEV;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.LRService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/lr")
public class LRController {

    @Autowired
    private LRService lrService;

    ErrorMessage err = new ErrorMessage("");

    // Endpoint to create a new LR document
    @PostMapping("/create")
    public ResponseEntity<Object> saveRecord(@RequestBody @Valid LRRequest lr) {
        try {
            // Debugging: Print LRRequest details for inspection
            System.out.println("Received LRRequest: " + lr.getDate());

            LR savedLR = lrService.saveLR(lr);
            return new ResponseEntity<>(savedLR, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to create LR: " + e.getMessage());
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

    // Endpoint to retrieve all LR documents
    @GetMapping("/all")
    public ResponseEntity<Object> getAllLRs() {
        List<LR> lrList = lrService.getAllLRs();
        return new ResponseEntity<>(lrList, HttpStatus.OK);
    }

    // Endpoint to retrieve all LR documents
    @GetMapping("/keyword/{keyword}")
    public ResponseEntity<Object> getRecords(@PathVariable String details) {
        List<LR> lrList = lrService.getLRByKeyword(details);
        return new ResponseEntity<>(lrList, HttpStatus.OK);
    }

    @GetMapping("/documents/{documentsId}/approved")
    public ResponseEntity<Object> getAllApprovedLRsByDocumentsId(@PathVariable String documentsId) {
        try {
            List<LRResponse> lrList = lrService.getAllApprovedLRsByDocumentsId(documentsId);
            return new ResponseEntity<>(lrList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to get LR JEV: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get LR JEV: " + e.getMessage());
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

    @GetMapping("/documents/{documentsId}/unapproved")
    public ResponseEntity<Object> getAllNotApprovedLRsByDocumentsId(@PathVariable String documentsId) {
        try {
            List<LRResponse> lrList = lrService.getAllNotApprovedLRsByDocumentsId(documentsId);
            return new ResponseEntity<>(lrList, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to get LR JEV: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get LR JEV: " + e.getMessage());
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

    @GetMapping("/jev/documents/{documentsId}")
    public ResponseEntity<Object> getAllJEVsByDocumentsId(@PathVariable String documentsId) {
        try {
            List<LRJEV> lrList = lrService.getJEVByDocumentsId(documentsId);
            return new ResponseEntity<>(lrList, HttpStatus.OK);
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

    @GetMapping("/jev/school/{schoolId}/year/{year}/stackedbar")
    public ResponseEntity<Object> getAnnualStackedBarReport(@PathVariable String schoolId,
            @PathVariable String year) {
        try {
            List<StackedBarDTO> stackedBarList = lrService.getAnnualStackedBarReport(schoolId, year);
            return new ResponseEntity<>(stackedBarList, HttpStatus.OK);
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

    @GetMapping("/documents/{documentsId}/highest")
    public ResponseEntity<Object> getHighestAmountLRTranspoExpenses(@PathVariable String documentsId) {
        try {
            LRResponse reponse = lrService.getHighestAmountLRByDocumentsIdAndObjectCode(documentsId);
            return new ResponseEntity<>(reponse, HttpStatus.OK);
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
            LR lr = lrService.getLRById(id);
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
    public ResponseEntity<Object> updateLR(@PathVariable String id, @RequestBody @Valid LRRequest updatedLR) {
        try {
            LR updatedEntity = lrService.updateLR(id, updatedLR);
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
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<Object> deleteLR(@PathVariable String id, @PathVariable String userId) {
        try {
            lrService.deleteLRById(id, userId);
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
