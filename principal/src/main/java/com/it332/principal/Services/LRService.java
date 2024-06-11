package com.it332.principal.Services;

import com.it332.principal.DTO.LRRequest;
import com.it332.principal.DTO.LRResponse;
import com.it332.principal.Models.Documents;
import com.it332.principal.Models.LR;
import com.it332.principal.Models.Uacs;
import com.it332.principal.Repository.DocumentsRepository;
import com.it332.principal.Repository.LRRepository;
import com.it332.principal.Security.NotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LRService {

    @Autowired
    private LRRepository lrRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private UacsService uacsService;

    @Autowired
    private JEVService jevService;

    Documents existingDocument;

    public LR saveLR(LRRequest lr) {
        // Validate LR fields if needed before saving (e.g., check for required fields)
        existingDocument = documentsService.getDocumentById(lr.getDocumentsId());
        Uacs existingUacs = uacsService.getUacsByCode(lr.getObjectCode());

        // Save the LR first
        LR newLr = lrRepository.save(new LR(lr, existingUacs.getCode()));

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentAmount(lr.getDocumentsId());

        // Update the selected UACS code
        jevService.updateJEVAmount(lr.getDocumentsId(), existingUacs.getCode(),
                Float.parseFloat(lr.getAmount() + ""));

        return newLr;
    }

    public List<LR> getLRByKeyword(String keyword) {
        // Placeholder list to store filtered LR objects
        List<LR> filteredLRList = new ArrayList<>();

        // Assuming LR objects are retrieved from a data source (e.g., database)
        List<LR> allLRs = getAllLRs(); // Example method to get all LR objects

        // Iterate through all LR objects to filter by keyword
        for (LR lr : allLRs) {
            // Check if the LR object's details contain the keyword (case-insensitive)
            if (lrMatchesKeyword(lr, keyword)) {
                filteredLRList.add(lr); // Add matching LR object to the result list
            }
        }

        return filteredLRList;
    }

    // Helper method to check if an LR object matches the keyword
    private boolean lrMatchesKeyword(LR lr, String keyword) {
        // Assuming LR details such as payee, particulars, etc., are checked for keyword
        // match
        String payee = lr.getPayee();
        String particulars = lr.getParticulars();

        // Convert details and keyword to lowercase for case-insensitive comparison
        payee = payee.toLowerCase();
        particulars = particulars.toLowerCase();
        keyword = keyword.toLowerCase();

        // Check if any detail contains the keyword
        return payee.contains(keyword) || particulars.contains(keyword);
    }

    public void updateDocumentAmount(String id) {
        // Find all LR objects with the specified documentId
        existingDocument = documentsService.getDocumentById(id);
        List<LRResponse> lrList = getAllLRsByDocumentsId(id);

        // Calculate the sum of amounts from the LR list
        double totalAmount = lrList.stream()
                .mapToDouble(LRResponse::getAmount)
                .sum();
        // Update the Documents amount property with the calculated total amount
        existingDocument.setBudget(totalAmount);

        // Save new sum
        documentsRepository.save(existingDocument);
    }

    public void updateJEVAmount(String id) {
        // Find all LR objects with the specified documentId
        existingDocument = documentsService.getDocumentById(id);
        List<LRResponse> lrList = getAllLRsByDocumentsId(id);

        // Calculate the sum of amounts from the LR list
        double totalAmount = lrList.stream()
                .mapToDouble(LRResponse::getAmount)
                .sum();
        // Update the Documents amount property with the calculated total amount
        existingDocument.setBudget(totalAmount);

        // Save new sum
        documentsRepository.save(existingDocument);
    }

    public LR getLRById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        return lrRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LR not found with ID: " + id));
    }

    public List<LR> getAllLRs() {
        // List<LR> lrList = lrRepository.findAll();
        return lrRepository.findAll();
    }

    // Method to retrieve all LR documents with the same documentsId
    public List<LRResponse> getAllLRsByDocumentsId(String documentsId) {
        existingDocument = documentsService.getDocumentById(documentsId);
        List<LR> lrList = lrRepository.findByDocumentsIdOrderByDateAsc(documentsId);

        if (existingDocument == null) {
            throw new NotFoundException("LR not found with ID: " + documentsId);
        }

        return lrList.stream()
                .map(LRResponse::new) // Map each LR to LRResponse using constructor
                .collect(Collectors.toList());
    }

    public LR updateLR(String id, LRRequest updatedLR) {
        LR lr = getLRById(id);

        // Update LR fields based on the provided updatedLR object
        if (updatedLR.getDate() != null) {
            lr.setDate(updatedLR.getDate());
        }
        if (updatedLR.getOrsBursNo() != null) {
            lr.setOrsBursNo(updatedLR.getOrsBursNo());
        }
        if (updatedLR.getParticulars() != null) {
            lr.setParticulars(updatedLR.getParticulars());
        }
        if (updatedLR.getAmount() != 0) {
            lr.setAmount(updatedLR.getAmount());
        }
        if (updatedLR.getObjectCode() != null) {
            Uacs existingUacs = uacsService.getUacsByCode(updatedLR.getObjectCode());
            lr.setObjectCode(existingUacs.getCode());
        }
        if (updatedLR.getPayee() != null) {
            lr.setPayee(updatedLR.getPayee());
        }
        if (updatedLR.getNatureOfPayment() != null) {
            lr.setNatureOfPayment(updatedLR.getNatureOfPayment());
        }

        LR newLR = lrRepository.save(lr);

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentAmount(lr.getDocumentsId());

        return newLR;
    }

    public void deleteLRById(String id) {
        LR lr = getLRById(id);

        lrRepository.delete(lr);

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentAmount(lr.getDocumentsId());
    }
}
