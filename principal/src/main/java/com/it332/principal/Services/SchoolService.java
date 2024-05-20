package com.it332.principal.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.Association;
import com.it332.principal.Models.School;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.AssociationRepository;
import com.it332.principal.Repository.SchoolRepository;
import com.it332.principal.Repository.UserRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class SchoolService {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private UserRepository userRepository;

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

    public User isPrincipalPresent(String schoolId) {
        // Get user associations for school
        List<User> users = getUsersBySchoolId(schoolId);

        for (User user : users) {
            // User user = userService.getUserById(association.getUserId());
            if ("Principal".equals(user.getPosition())) {
                return user;
            }
        }

        return null;
    }

    public List<User> getUsersBySchoolId(String schoolId) {
        List<Association> association = associationRepository.findBySchoolId(schoolId);

        // Extract userIds from associations
        List<String> userIds = association.stream()
                .map(Association::getUserId)
                .distinct() // Ensure no duplicate user IDs
                .collect(Collectors.toList());

        return userRepository.findAllById(userIds);
    }

    public School getSchoolByName(String name) {
        School byName = schoolRepository.findByName(name);
        if (byName == null) {
            throw new NotFoundException("School not found with name: " + name);
        }
        return byName;
    }

    public School getSchoolByFullName(String fullName) {
        School byFullName = schoolRepository.findByFullName(fullName);
        if (byFullName == null) {
            throw new NotFoundException("School not found with name: " + fullName);
        }
        return byFullName;
    }

    public School getSchoolByNameOrFullName(String name) {
        try {
            // First, try to get the school by name
            return getSchoolByName(name);
        } catch (NotFoundException e) {
            // If not found by name, try to get the school by full name
            try {
                return getSchoolByFullName(name);
            } catch (NotFoundException ex) {
                // If neither found, rethrow the original NotFoundException
                throw new NotFoundException("School not found with name or full name: " + name);
            }
        }
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
