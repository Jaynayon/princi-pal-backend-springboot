package com.it332.principal.Services;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.DocumentsPatch;
import com.it332.principal.DTO.DocumentsRequest;
import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.Models.Documents;
import com.it332.principal.Models.School;
import com.it332.principal.Repository.DocumentsRepository;
import com.it332.principal.Security.NotFoundException;

import java.util.List;
import java.util.ArrayList;

@Service
public class DocumentsService {

    @Autowired
    private DocumentsRepository documentRepository;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    public JEVService jevService;

    public DocumentsResponse saveDocument(DocumentsRequest document) {
        // Check if school exists
        School existingSchool = schoolService.getSchoolById(document.getSchoolId());

        Documents getDoc = documentRepository.findBySchoolIdAndYearAndMonth(existingSchool.getId(),
                document.getYear(), document.getMonth());

        if (getDoc != null) {
            throw new IllegalArgumentException("Document with School id " + getDoc.getId() +
                    " in " + getDoc.getMonth() + " " + getDoc.getYear() + " already exists");
        }

        // Save the new document
        Documents newDoc = documentRepository.save(new Documents(document));

        // Initialize JEV's in new document
        jevService.initializeJEV(newDoc.getId());

        return new DocumentsResponse(existingSchool, newDoc);
    }

    public DocumentsResponse initializeDocuments(DocumentsRequest document) {
        // Check if the school exists
        School existingSchool = schoolService.getSchoolById(document.getSchoolId());
        List<Documents> yearDocuments = new ArrayList<>();
        Documents documentRequest = new Documents();

        // Array of month names
        String[] months = { "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December" };

        // Loop through all months from January to December
        for (String month : months) {
            // Check if a document for this month and year already exists
            Documents existingDoc = documentRepository.findBySchoolIdAndYearAndMonth(
                    existingSchool.getId(), document.getYear(), month);

            if (existingDoc != null) {
                continue; // Skip if document already exist
            }

            // Create a new document for each month
            Documents newDoc = new Documents(document);
            newDoc.setMonth(month); // Set the month
            newDoc.setCashAdvance(document.getAnnualBudget() / 12);

            // Store requested month Document
            if (document.getMonth().equals(month)) {
                documentRequest = newDoc;
            }

            // Add the new document to year documents
            yearDocuments.add(newDoc);
        }

        documentRepository.saveAll(yearDocuments);

        return new DocumentsResponse(existingSchool, documentRequest);
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

    public List<Documents> getDocumentsBySchoolYear(String schoolId, String year) {
        // Check if school exists
        School existingSchool = schoolService.getSchoolById(schoolId);

        List<Documents> getDocs = documentRepository.findBySchoolIdAndYear(schoolId, year);

        if (getDocs == null) {
            throw new NotFoundException("No Document with School id " + existingSchool.getId() +
                    " in " + year);
        }

        return getDocs;
    }

    public Documents getDocumentById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found with ID: " + id));
    }

    public void updateDocumentBudgetExceeded(String id) {
        // Find all JEV objects with the specified documentId
        Documents existingDocument = getDocumentById(id);

        // Safely get the cash advance, or use 0.0 if it's null
        Double budget = existingDocument.getBudget();
        double budgetValue = (budget != null) ? budget : 0.0;

        Double cashAdvance = existingDocument.getCashAdvance();
        double cashAdvanceValue = (cashAdvance != null) ? cashAdvance : 0.0;

        // Update the Documents budgetExceeded and budgetLimitExceeded status
        existingDocument.setBudgetExceeded(budgetValue > cashAdvanceValue);
        existingDocument.setBudgetLimitExceeded(budgetValue > existingDocument.getBudgetLimit());

        // Save new sum
        documentRepository.save(existingDocument);
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
        if (updatedSchool.getCashAdvance() != null) {
            document.setCashAdvance(updatedSchool.getCashAdvance());
            jevService.updateJEVCashAdvance(document.getId(), "1990101000",
                    updatedSchool.getCashAdvance().floatValue());
        }

        Documents newDoc = documentRepository.save(document);

        updateDocumentBudgetExceeded(newDoc.getId());

        // if (updatedSchool.getCashAdvance() != null) {
        // System.out.println(newDoc.getCashAdvance().floatValue());
        // // Set JEV Advances to Operating Expenses
        // jevService.updateJEVCashAdvance(newDoc.getId(), "1990101000",
        // newDoc.getCashAdvance().floatValue());
        // }

        return newDoc;
    }

    public void deleteDocumentById(String id) {
        Documents document = getDocumentById(id);

        // Delete initialized jev's (obsolete logic)
        // jevService.deleteByDocumentsId(document.getId());

        documentRepository.delete(document);
    }

}
