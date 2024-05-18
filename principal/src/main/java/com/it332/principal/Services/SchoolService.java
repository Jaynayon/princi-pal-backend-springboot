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
        School existingName = schoolRepository.findByName(school.getName());
        School existingFullName = schoolRepository.findByFullName(school.getFullName());

        if (existingName != null || existingFullName != null) {
            if (existingName != null) {
                throw new IllegalArgumentException("School with name " + school.getName() + " already exists");
            }
            throw new IllegalArgumentException("School with full name " + school.getFullName() + " already exists");
        }

        return schoolRepository.save(school);
    }

    public School getSchoolByName(String name) {
        return schoolRepository.findByName(name);
    }

    public School getSchoolByFullName(String fullName) {
        return schoolRepository.findByFullName(fullName);
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
        // Check if school exists
        School existingSchool = getSchoolById(id);

        // Check if school name is already taken
        School existingName = getSchoolByName(updatedSchool.getName());
        School existingFullName = getSchoolByFullName(updatedSchool.getFullName());

        if (existingName != null || existingFullName != null) {
            if (existingName != null) {
                throw new IllegalArgumentException("School with name " + updatedSchool.getName() + " already exists");
            } else if (existingFullName != null) {
                throw new IllegalArgumentException(
                        "School with full name " + updatedSchool.getFullName() + " already exists");
            }
        }

        if (updatedSchool.getName() != null) {
            existingSchool.setName(updatedSchool.getName());
        }
        if (updatedSchool.getFullName() != null) {
            existingSchool.setFullName(updatedSchool.getFullName());
        }

        return schoolRepository.save(existingSchool);
    }

    public void deleteSchoolById(String id) {
        School school = getSchoolById(id);

        schoolRepository.delete(school);
    }

}
