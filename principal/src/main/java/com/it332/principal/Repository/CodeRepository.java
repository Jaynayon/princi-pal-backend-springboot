package com.it332.principal.Repository;

import com.it332.principal.Models.Code;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CodeRepository extends MongoRepository<Code, String> {
    Code findByCode(String code);

    Code findBySchoolId(String schoolId);
}
