package com.it332.principal.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.Documents;

public interface DocumentsRepository extends MongoRepository<Documents, String> {
    // Define custom query methods here if needed
    Documents findBySchoolIdAndYearAndMonth(String schoolId, String year, String month);

    List<Documents> findBySchoolIdAndYear(String schoolId, String year);

    Documents findByYearAndMonth(String year, String month);
}
