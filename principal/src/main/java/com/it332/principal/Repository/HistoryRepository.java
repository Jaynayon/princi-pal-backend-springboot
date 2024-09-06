package com.it332.principal.Repository;

import com.it332.principal.Models.History;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.transaction.annotation.Transactional;

public interface HistoryRepository extends MongoRepository<History, String> {
    // Find all UpdateHistory records by lrId
    List<History> findAllByLrId(String lrId, Sort sort);

    // Find all UpdateHistory records by documentsId
    List<History> findAllByDocumentsId(String documentsId, Sort sort);

    // Custom method to delete all records by lrId
    @Transactional
    void deleteAllByLrId(String lrId);
}
