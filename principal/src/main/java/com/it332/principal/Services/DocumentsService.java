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

import com.it332.principal.Models.Notification;
import com.it332.principal.Models.Association;
import java.util.Date;

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

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AssociationService associationService;

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

        createBudgetLimitNotification(newDoc);

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
                existingDoc.setCashAdvance(document.getAnnualBudget() / 12);
                existingDoc.setAnnualBudget(document.getAnnualBudget());

                // Store requested month Document
                if (document.getMonth().equals(month)) {
                    documentRequest = existingDoc;
                }

                // Add the new document to year documents
                yearDocuments.add(existingDoc);
            } else {
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

    public List<String> getDocumentIdsBySchoolYear(String schoolId, String year) {
        // Check if school exists
        School existingSchool = schoolService.getSchoolById(schoolId);

        List<Documents> documents = documentRepository.findBySchoolIdAndYear(schoolId, year);

        if (documents == null || documents.isEmpty()) {
            throw new NotFoundException("No Documents found for School id " + existingSchool.getId() + " in " + year);
        }

        List<String> documentIds = new ArrayList<>();
        for (Documents doc : documents) {
            documentIds.add(doc.getId());
        }

        return documentIds;
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

    public void updateDocumentAnnualBudget(DocumentsRequest doc) {
        // Check if the needed fields are provided
        if (doc.getCashAdvance() == null) {
            throw new IllegalArgumentException("Cash Advance is required");
        }
        if (doc.getSchoolId() == null) {
            throw new IllegalArgumentException("School ID is required");
        }
        if (doc.getYear() == null) {
            throw new IllegalArgumentException("Year is required");
        }

        // Find all documents with the specified schoolId and year
        List<Documents> documents = getDocumentsBySchoolYear(doc.getSchoolId(), doc.getYear());
        double newCashAdvance = doc.getAnnualBudget() / 12; // Calculate the new cash advance

        // Update the cash advance for each document
        documents.forEach(document -> {
            document.setAnnualBudget(doc.getAnnualBudget());
            document.setCashAdvance(newCashAdvance);
        });

        // Save all documents in one batch operation
        documentRepository.saveAll(documents);

        // Update the budget exceeded status for all documents
        documents.forEach(document -> updateDocumentBudgetExceeded(document.getId()));
    }

    public void updateDocumentAnnualExpense(String schoolId, String year) {
        // Find all documents with the specified schoolId and year
        List<Documents> documents = getDocumentsBySchoolYear(schoolId, year);
        double totalAnnualExpense = documents.stream()
                .mapToDouble(Documents::getBudget)
                .sum();

        // Update the cash advance for each document
        documents.forEach(document -> {
            document.setAnnualExpense(totalAnnualExpense);
        });

        // Save all documents in one batch operation
        documentRepository.saveAll(documents);
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

        createBudgetLimitNotification(newDoc);

        if (updatedSchool.getBudgetLimit() != null && newDoc.isBudgetLimitExceeded() && newDoc.getBudgetLimit() != 0
                && newDoc.getBudget() > newDoc.getBudgetLimit()) {
            createBudgetLimitExceededNotification(newDoc);
        }

        return newDoc;
    }

    public void deleteDocumentById(String id) {
        Documents document = getDocumentById(id);

        // Delete initialized jev's (obsolete logic)
        // jevService.deleteByDocumentsId(document.getId());

        documentRepository.delete(document);
    }

    public void createBudgetLimitNotification(Documents document) {
        // Check if the budget limit is greater than 0
        if (document.getBudgetLimit() > 0) {
            // Fetch the school details to get the full name
            School school = schoolService.getSchoolById(document.getSchoolId());
            String schoolFullName = school.getFullName(); // Assuming getFullName() exists

            // Prepare the notification message including the school's full name
            String details = String.format(
                    "The budget limit for %s %s at %s has been set to ₱%.2f",
                    document.getMonth(),
                    document.getYear(),
                    schoolFullName, // Insert the school's full name
                    document.getBudgetLimit());

            // Fetch all users associated with the school through the AssociationService
            List<Association> associations = associationService.getAssociationsBySchoolId(document.getSchoolId());
            for (Association assoc : associations) {
                // Create a new Notification object for each user associated with the school
                Notification notification = new Notification(
                        assoc.getUserId(), // Set the user ID from the association
                        assoc.getId(), // AssocId if needed
                        document.getSchoolId(), // School ID for associating the notification
                        details,
                        new Date());

                // Save the notification using NotificationService
                notificationService.createNotification(notification);
            }
        }
    }

    public void createBudgetLimitExceededNotification(Documents document) {
        // Fetch the school details to get the full name
        School school = schoolService.getSchoolById(document.getSchoolId());
        String schoolFullName = school.getFullName(); // Assuming getFullName() method exists

        // Prepare the notification message including the school's full name
        String details = String.format(
                "Attention! The budget limit for %s %s at %s has been exceeded.",
                document.getMonth(),
                document.getYear(),
                schoolFullName // Insert the school's full name
        );

        // Fetch all users associated with the school through the AssociationService
        List<Association> associations = associationService.getAssociationsBySchoolId(document.getSchoolId());
        for (Association assoc : associations) {
            // Create a new Notification object for each user associated with the school
            Notification notification = new Notification(
                    assoc.getUserId(), // Set the user ID from the association
                    assoc.getId(), // AssocId if needed
                    document.getSchoolId(), // School ID for associating the notification
                    details,
                    new Date());

            // Save the notification using NotificationService
            notificationService.createNotification(notification);
        }
    }

}
