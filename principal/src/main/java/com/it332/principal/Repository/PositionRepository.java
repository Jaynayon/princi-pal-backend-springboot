package com.it332.principal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.Position;

public interface PositionRepository extends MongoRepository<Position, String> {
    Position findByName(String name);
}
