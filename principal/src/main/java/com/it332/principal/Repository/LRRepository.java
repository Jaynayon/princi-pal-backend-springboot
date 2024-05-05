package com.it332.principal.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.DTO.LRResponse;
import com.it332.principal.Models.LR;

public interface LRRepository extends MongoRepository<LR, String> {
    // Define custom query methods here if needed
    List<LRResponse> findByDocumentsId(String documentsId);
}