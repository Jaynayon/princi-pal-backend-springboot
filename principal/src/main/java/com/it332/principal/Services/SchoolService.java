package com.it332.principal.Services;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.School;
import com.it332.principal.Repository.SchoolRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    public School createSchool(School school) {
        School existingSchool = schoolRepository.findByName(school.getName());

        if (existingSchool != null) {
            throw new IllegalArgumentException("School with name " + school.getName() + " already exists");
        }
        return schoolRepository.save(school);
    }

    public School getSchoolByName(String name) {
        return schoolRepository.findByName(name);
    }

    public List<School> getAllSchools() {
        return schoolRepository.findAll();
    }

    public School getSchoolById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }
        // Retrieve the school entity by ID from the repository
        return schoolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("School not found with ID: " + id));
    }

    public School updateSchool(String id, School updatedSchool) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        School existingSchool = getSchoolById(id);

        existingSchool.setName(updatedSchool.getName());

        return schoolRepository.save(existingSchool);
    }

    public void deleteSchoolById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        School school = getSchoolById(id);

        schoolRepository.delete(school);
    }

}
