package com.it332.principal.Services;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.Position;
import com.it332.principal.Repository.PositionRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class PositionService {

    @Autowired
    private PositionRepository positionRepository;

    public Position createPosition(Position position) {
        Position existingPosition = positionRepository.findByName(position.getName());

        if (existingPosition != null) {
            throw new IllegalArgumentException("Position with name " + position.getName() + " already exists");
        }
        return positionRepository.save(position);
    }

    public Position getPositionByName(String name) {
        Position existPos = positionRepository.findByName(name);

        if (existPos == null) {
            throw new NotFoundException("Position not found with name: " + name);
        }
        return existPos;
    }

    public List<Position> getAllPositions() {
        return positionRepository.findAll();
    }

    public Position getPositionById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }
        // Retrieve the position entity by ID from the repository
        return positionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Position not found with ID: " + id));
    }

    public Position updatePosition(String id, Position updatedPosition) {
        // Check if position exists
        Position existingPosition = getPositionById(id);

        // Check if position name is already taken
        Position nameAlreadyExists = positionRepository.findByName(updatedPosition.getName());

        if (nameAlreadyExists != null && !nameAlreadyExists.getId().equals(id)) {
            throw new IllegalArgumentException("Position with name " + updatedPosition.getName() + " already exists");
        }

        existingPosition.setName(updatedPosition.getName());

        return positionRepository.save(existingPosition);
    }

    public void deletePositionById(String id) {
        Position position = getPositionById(id);

        positionRepository.delete(position);
    }
}
