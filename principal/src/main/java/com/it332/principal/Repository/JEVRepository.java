package com.it332.principal.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.JEV;

public interface JEVRepository extends MongoRepository<JEV, String> {
    // Define custom query methods here if needed
    List<JEV> findByDocumentsId(String documentsId);
}