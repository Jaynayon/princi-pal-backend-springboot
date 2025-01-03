package com.it332.principal.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.UserAssociation;
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

        // Convert properties to uppercases
        school.setName(school.getName().toUpperCase());
        school.setFullName(school.getFullName().toUpperCase());

        return schoolRepository.save(school);
    }

    public UserAssociation isPrincipalPresent(String schoolId) {
        // Get user associations for school
        List<UserAssociation> users = getUsersBySchoolId(schoolId);

        for (UserAssociation user : users) {
            // User user = userService.getUserById(association.getUserId());
            if ("Principal".equals(user.getPosition())) {
                return user;
            }
        }

        return null;
    }

    public void deleteIsPrincipalPresent(String schoolId) {
        // Find the first user with the position "Principal" directly
        getUsersBySchoolId(schoolId).stream()
                .filter(user -> "Principal".equals(user.getPosition()))
                .findFirst()
                .ifPresent(user -> associationRepository.delete(
                        associationRepository.findBySchoolIdAndUserId(schoolId, user.getId())));
    }

    public List<UserAssociation> getUsersBySchoolId(String schoolId) {
        List<Association> association = associationRepository.findBySchoolIdAndApprovedTrue(schoolId);

        // Extract userIds from associations
        List<String> userIds = association.stream()
                .map(Association::getUserId)
                .distinct() // Ensure no duplicate user IDs
                .collect(Collectors.toList());

        // Get all users in school
        List<User> schoolUsers = userRepository.findAllById(userIds);

        // Map each user to their respective association to create
        // UserAssociation DTOs
        List<UserAssociation> userAssociations = schoolUsers.stream()
                .map(user -> {
                    // Find the corresponding association for the user
                    Association assoc = association.stream()
                            .filter(a -> a.getUserId().equals(user.getId()))
                            .findFirst()
                            .orElseThrow(
                                    () -> new RuntimeException("Association not found for user ID: " + user.getId()));

                    // Create and return UserAssociation DTO
                    return new UserAssociation(user, assoc);
                })
                .collect(Collectors.toList());

        return userAssociations;
    }

    public School getSchoolByName(String name) {
        School byName = schoolRepository.findByName(name.toUpperCase());
        if (byName == null) {
            throw new NotFoundException("School not found with name: " + name);
        }
        return byName;
    }

    public School getSchoolByFullName(String fullName) {
        School byFullName = schoolRepository.findByFullName(fullName.toUpperCase());
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
            existingSchool.setName(updatedSchool.getName().toUpperCase());
        }
        if (updatedSchool.getFullName() != null) {
            existingSchool.setFullName(updatedSchool.getFullName().toUpperCase());
        }

        return schoolRepository.save(existingSchool);
    }

    public void deleteSchoolById(String id) {
        School school = getSchoolById(id);

        schoolRepository.delete(school);
    }

}
