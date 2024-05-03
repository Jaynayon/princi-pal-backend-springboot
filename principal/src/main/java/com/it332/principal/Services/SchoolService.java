package com.it332.principal.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.School;
import com.it332.principal.Repository.SchoolRepository;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    public School createSchool(School school) {
        return schoolRepository.save(school);
    }

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public School getSchoolById(String id) {
        return schoolRepository.findById(id).orElse(null);
    }

}
