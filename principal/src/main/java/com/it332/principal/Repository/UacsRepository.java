package com.it332.principal.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.it332.principal.Models.Uacs;

public interface UacsRepository extends MongoRepository<Uacs, String> {
    Uacs findByName(String name);

    Uacs findByCode(String code);
}
