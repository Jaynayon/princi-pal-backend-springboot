package com.it332.principal.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it332.principal.Models.School;
import com.it332.principal.Services.SchoolService;

@RestController
@RequestMapping("/schools")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    @PostMapping("/create")
    public ResponseEntity<School> createSchool(@RequestBody School school) {
        School newSchool = schoolService.createSchool(school);
        return new ResponseEntity<>(newSchool, HttpStatus.CREATED);
    }

    @GetMapping("/all")
    public ResponseEntity<List<School>> getAllSchools() {
        List<School> allSchools = schoolService.getAllSchools();
        return new ResponseEntity<>(allSchools, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<School> getSchoolById(@PathVariable String id) {
        School school = schoolService.getSchoolById(id);
        if (school != null) {
            return new ResponseEntity<>(school, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
