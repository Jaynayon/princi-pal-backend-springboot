package com.it332.principal.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.it332.principal.DTO.LRResponse;
import com.it332.principal.Models.LR;

public interface LRRepository extends MongoRepository<LR, String> {
    // Define custom query methods here if needed
    List<LR> findByApprovedTrueAndDocumentsIdOrderByDateAsc(String documentsId);

    List<LR> findByApprovedFalseAndDocumentsIdOrderByDateAsc(String documentsId);

    List<LRResponse> findByDocumentsIdOrderByDateDesc(String documentsId);

    // Custom query method to find LR entities by documentsId
    @Query(value = "{'documentsId': ?0}")
    List<LRResponse> findLRsByDocumentsId(String documentsId);
}