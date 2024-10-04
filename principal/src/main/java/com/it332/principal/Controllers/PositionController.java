package com.it332.principal.Controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Models.Position;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.PositionService;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    @Autowired
    private PositionService positionService;

    ErrorMessage err = new ErrorMessage("");

    @PostMapping("/create")
    public ResponseEntity<Object> createPosition(@Valid @RequestBody Position position) {
        ErrorMessage err = new ErrorMessage("");
        try {
            Position newPosition = positionService.createPosition(position);
            return new ResponseEntity<>(newPosition, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            err.setMessage("Failed to create position: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
        } catch (Exception e) {
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Position>> getAllPositions() {
        List<Position> allPositions = positionService.getAllPositions();
        return new ResponseEntity<>(allPositions, HttpStatus.OK);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Object> getPositionByName(@PathVariable String name) {
        try {
            Position existingPos = positionService.getPositionByName(name);
            return new ResponseEntity<>(existingPos, HttpStatus.OK);
        } catch (IllegalArgumentException | NotFoundException e) {
            err.setMessage("Failed to get position: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        } catch (Exception e) {
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPositionById(@Valid @PathVariable String id) {

        try {
            Position position = positionService.getPositionById(id);
            return new ResponseEntity<>(position, HttpStatus.OK);
        } catch (IllegalArgumentException | NotFoundException e) {
            err.setMessage("Failed to get position: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        } catch (Exception e) {
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updatePosition(@PathVariable String id, @RequestBody Position updatedPosition) {
        ErrorMessage err = new ErrorMessage("");
        try {
            Position updatedEntity = positionService.updatePosition(id, updatedPosition);
            return ResponseEntity.ok(updatedEntity);
        } catch (IllegalArgumentException | NotFoundException e) {
            err.setMessage("Failed to update position: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        } catch (Exception e) {
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePosition(@PathVariable String id) {
        ErrorMessage err = new ErrorMessage("");
        try {
            positionService.deletePositionById(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | NotFoundException e) {
            err.setMessage("Failed to delete position: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        } catch (Exception e) {
            e.printStackTrace();
            err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}
