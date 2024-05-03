package com.it332.principal.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.it332.principal.Models.Documents;
import com.it332.principal.Repository.DocumentsRepository;

import java.util.List;

@Service
public class DocumentsService {

    @Autowired
    private DocumentsRepository documentRepository;

    public Documents saveDocument(Documents document) {
        // Implement any additional logic here before saving
        return documentRepository.save(document);
    }

    public Documents getDocumentBySchoolYearMonth(String schoolId, String year, String month) {
        return documentRepository.findBySchoolIdAndYearAndMonth(schoolId, year, month);
    }

    public List<Documents> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Documents getDocumentById(String id) {
        return documentRepository.findById(id).orElse(null);
    }

}
