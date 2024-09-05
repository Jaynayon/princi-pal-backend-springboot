package com.it332.principal.Controllers;

import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.DTO.HistoryRequest;
import com.it332.principal.DTO.HistoryResponse;
import com.it332.principal.Models.History;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    ErrorMessage err = new ErrorMessage("");

    // Endpoint to create a new LR document
    @PostMapping("/create")
    public ResponseEntity<Object> saveHistory(@RequestBody @Valid HistoryRequest req) {
        try {
            // Debugging: Print LRRequest details for inspection
            System.out.println("Received Request: " + req);

            History savedHistory = historyService.createHistory(req);
            return new ResponseEntity<>(savedHistory, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate document is detected
            err.setMessage("Failed to create History: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(err);
        } catch (NotFoundException e) {
            // This exception is thrown when a no school is detected
            err.setMessage("Failed to get History: " + e.getMessage());
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
        List<History> historyList = historyService.getAllHistories();
        return new ResponseEntity<>(historyList, HttpStatus.OK);
    }

    @GetMapping("/documents/{documentsId}")
    public ResponseEntity<Object> getAllHistoryByDocumentsId(@PathVariable String documentsId) {
        try {
            List<HistoryResponse> historyList = historyService.getHistoryByDocumentsId(documentsId);
            return new ResponseEntity<>(historyList, HttpStatus.OK);
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

    @GetMapping("/lr/{lrId}")
    public ResponseEntity<Object> getAllHistoryByLrId(@PathVariable String lrId) {
        try {
            List<HistoryResponse> historyList = historyService.getHistoryByLrId(lrId);
            return new ResponseEntity<>(historyList, HttpStatus.OK);
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

    // Endpoint to delete an LR document by ID
    @DeleteMapping("/lr/{lrId}")
    public ResponseEntity<Object> deleteHistoryByLRId(@PathVariable String lrId) {
        try {
            historyService.deleteHistoryByLrId(lrId);
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
