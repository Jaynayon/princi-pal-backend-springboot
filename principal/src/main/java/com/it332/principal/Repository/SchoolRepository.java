package com.it332.principal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.School;

public interface SchoolRepository extends MongoRepository<School, String> {
    School findByName(String name);

    School findByFullName(String fullName);
}
