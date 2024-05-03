package com.it332.principal.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Documents> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Documents getDocumentById(String id) {
        return documentRepository.findById(id).orElse(null);
    }

}
