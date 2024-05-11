package com.it332.principal.Services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.DocumentsPatch;
import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.Models.Documents;
import com.it332.principal.Models.School;
import com.it332.principal.Repository.DocumentsRepository;
import com.it332.principal.Security.NotFoundException;

import java.util.List;

@Service
public class DocumentsService {

    @Autowired
    private DocumentsRepository documentRepository;

    @Autowired
    private SchoolService schoolService;

    public DocumentsResponse saveDocument(Documents document) {
        // Check if school exists
        School existingSchool = schoolService.getSchoolById(document.getSchoolId());

        Documents getDoc = documentRepository.findBySchoolIdAndYearAndMonth(existingSchool.getId(),
                document.getYear(), document.getMonth());

        if (getDoc != null) {
            throw new IllegalArgumentException("Document with School id " + getDoc.getId() +
                    " in " + getDoc.getMonth() + " " + getDoc.getYear() + " already exists");
        }

        // Save the new document
        Documents newDoc = documentRepository.save(document);

        return new DocumentsResponse(existingSchool, newDoc);
    }

    public DocumentsResponse getDocumentBySchoolYearMonth(String schoolId, String year, String month) {
        // Check if school exists
        School existingSchool = schoolService.getSchoolById(schoolId);

        Documents getDoc = documentRepository.findBySchoolIdAndYearAndMonth(existingSchool.getId(), year, month);

        if (getDoc == null) {
            throw new NotFoundException("No Document with School id " + existingSchool.getId() +
                    " in " + month + " " + year);
        }

        return new DocumentsResponse(existingSchool, getDoc);
    }

    public List<Documents> getAllDocuments() {
        return documentRepository.findAll();
    }

    public Documents getDocumentById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found with ID: " + id));
    }

    public Documents updateDocument(String id, DocumentsPatch updatedSchool) {
        // Check if document exists
        Documents document = getDocumentById(id);

        if (updatedSchool.getBudgetLimit() != null) {
            document.setBudgetLimit(updatedSchool.getBudgetLimit());
        }
        if (updatedSchool.getSds() != null) {
            document.setSds(updatedSchool.getSds());
        }
        if (updatedSchool.getClaimant() != null) {
            document.setClaimant(updatedSchool.getClaimant());
        }
        if (updatedSchool.getHeadAccounting() != null) {
            document.setHeadAccounting(updatedSchool.getHeadAccounting());
        }

        return documentRepository.save(document);
    }

    public void deleteDocumentById(String id) {
        Documents document = getDocumentById(id);

        documentRepository.delete(document);
    }

}
