package com.it332.principal.Services;

import com.it332.principal.DTO.LRRequest;
import com.it332.principal.DTO.LRResponse;
import com.it332.principal.Models.Documents;
import com.it332.principal.Models.LR;
import com.it332.principal.Models.LRJEV;
import com.it332.principal.Models.Uacs;
import com.it332.principal.Repository.DocumentsRepository;
import com.it332.principal.Repository.LRRepository;
import com.it332.principal.Security.NotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @Autowired
    private HistoryService historyService;

    Documents existingDocument;

    public LR saveLR(LRRequest lr) {
        // Validate LR fields if needed before saving (e.g., check for required fields)
        existingDocument = documentsService.getDocumentById(lr.getDocumentsId());
        Uacs existingUacs = uacsService.getUacsByCode(lr.getObjectCode());

        // Save the LR first
        LR newLr = lrRepository.save(new LR(lr, existingUacs.getCode()));

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentBudget(newLr.getDocumentsId());

        // Create history
        historyService.createHistory(newLr, lr.getUserId(), true);

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

    public void updateDocumentBudget(String id) {
        // Find all LR objects with the specified documentId
        existingDocument = documentsService.getDocumentById(id);
        List<LRResponse> lrList = getAllApprovedLRsByDocumentsId(id);

        // Calculate the sum of amounts from the LR list
        double totalAmount = lrList.stream()
                .mapToDouble(LRResponse::getAmount)
                .sum();

        // Safely get the cash advance, or use 0.0 if it's null
        Double cashAdvance = existingDocument.getCashAdvance();
        double cashAdvanceValue = (cashAdvance != null) ? cashAdvance : 0.0;

        // Update the document's budget and budgetExceeded status
        existingDocument.setBudget(totalAmount);
        existingDocument.setBudgetExceeded(totalAmount > cashAdvanceValue);
        existingDocument.setBudgetLimitExceeded(totalAmount > existingDocument.getBudgetLimit());

        // Save new sum
        documentsRepository.save(existingDocument);
    }

    public List<LRJEV> getJEVByDocumentsId(String documentsId) {
        existingDocument = documentsService.getDocumentById(documentsId);
        List<LRResponse> docLr = getAllApprovedLRsByDocumentsId(documentsId);
        List<LRJEV> jevs = new ArrayList<>();

        // Use a Set to collect unique objectCodes, avoiding duplicates
        Set<String> uniqueObjectCodes = new HashSet<>();
        for (LRResponse lr : docLr) {
            uniqueObjectCodes.add(lr.getObjectCode());
        }

        // Create a map to quickly find the LRJEV object by UACS code
        Map<String, LRJEV> jevMap = new HashMap<>();

        // Create LRJEV object per unique object code and store in the map
        for (String code : uniqueObjectCodes) {
            Uacs existingUacs = uacsService.getUacsByCode(code);
            LRJEV jev = new LRJEV(existingUacs);
            jevs.add(jev);
            jevMap.put(code, jev); // Add to the map for quick lookup
        }

        // Add the special case for the cash advance
        LRJEV cashAdvanceJev = new LRJEV(uacsService.getUacsByCode("1990101000"));
        jevs.add(cashAdvanceJev);
        jevMap.put("1990101000", cashAdvanceJev);

        // Update amounts efficiently by directly accessing the corresponding LRJEV from
        // the map
        for (LRResponse lr : docLr) {
            LRJEV jev = jevMap.get(lr.getObjectCode());
            if (jev != null) {
                jev.setAmount(jev.getAmount() + lr.getAmount());
                jev.setBudgetExceeded(jev.getAmount() > jev.getBudget());
            }
        }

        // Set the cash advance amount explicitly after the loop
        cashAdvanceJev.setAmount(existingDocument.getCashAdvance());

        return jevs;
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
    public List<LRResponse> getAllApprovedLRsByDocumentsId(String documentsId) {
        existingDocument = documentsService.getDocumentById(documentsId);
        List<LR> lrList = lrRepository.findByApprovedTrueAndDocumentsIdOrderByDateAsc(documentsId);

        if (existingDocument == null) {
            throw new NotFoundException("LR not found with ID: " + documentsId);
        }

        return lrList.stream()
                .map(LRResponse::new) // Map each LR to LRResponse using constructor
                .collect(Collectors.toList());
    }

    // Method to retrieve all LR documents with the same documentsId
    public List<LRResponse> getAllNotApprovedLRsByDocumentsId(String documentsId) {
        existingDocument = documentsService.getDocumentById(documentsId);
        List<LR> lrList = lrRepository.findByApprovedFalseAndDocumentsIdOrderByDateAsc(documentsId);

        if (existingDocument == null) {
            throw new NotFoundException("LR not found with ID: " + documentsId);
        }

        return lrList.stream()
                .map(LRResponse::new) // Map each LR to LRResponse using constructor
                .collect(Collectors.toList());
    }

    public LR updateLR(String id, LRRequest updatedLR) {
        LR lr = getLRById(id);
        existingDocument = documentsService.getDocumentById(lr.getDocumentsId());
        String fieldName = "";
        String oldValue = "";
        String newValue = "";

        // Update LR fields based on the provided updatedLR object
        if (updatedLR.getDate() != null) {
            fieldName = "date";
            oldValue = lr.getDate();
            newValue = updatedLR.getDate().toString();
            lr.setDate(updatedLR.getDate());
        }
        if (updatedLR.getOrsBursNo() != null) {
            fieldName = "orsBursNo";
            oldValue = lr.getOrsBursNo();
            newValue = updatedLR.getOrsBursNo();
            lr.setOrsBursNo(updatedLR.getOrsBursNo());
        }
        if (updatedLR.getParticulars() != null) {
            fieldName = "particulars";
            oldValue = lr.getParticulars();
            newValue = updatedLR.getParticulars();
            lr.setParticulars(updatedLR.getParticulars());
        }
        if (updatedLR.getObjectCode() != null) {
            Uacs existingUacs = uacsService.getUacsByCode(updatedLR.getObjectCode());
            fieldName = "objectCode";
            oldValue = lr.getObjectCode();
            newValue = existingUacs.getCode();
            lr.setObjectCode(existingUacs.getCode());
        }
        if (updatedLR.getPayee() != null) {
            fieldName = "payee";
            oldValue = lr.getPayee();
            newValue = updatedLR.getPayee();
            lr.setPayee(updatedLR.getPayee());
        }
        if (updatedLR.getNatureOfPayment() != null) {
            fieldName = "natureOfPayment";
            oldValue = lr.getNatureOfPayment();
            newValue = updatedLR.getNatureOfPayment();
            lr.setNatureOfPayment(updatedLR.getNatureOfPayment());
        }
        if (updatedLR.getAmount() != 0) {
            fieldName = "amount";
            oldValue = lr.getAmount() + "";
            newValue = updatedLR.getAmount() + "";
            // LR displayed will always be approved ones
            if (lr.isApproved()) {
                // lr amount + total lrs amount > monthly budget
                if ((updatedLR.getAmount() + (existingDocument.getBudget() - lr.getAmount())) > existingDocument
                        .getCashAdvance()) {
                    lr.setApproved(false);
                } else {
                    lr.setApproved(true);
                }
            }
            // Cancel / Reject
            else {
                lr.setApproved(true);
            }
            lr.setAmount(updatedLR.getAmount());
        }
        // Checks if approved payload is true, skips it otherwise
        if (updatedLR.isApproved()) {
            lr.setApproved(updatedLR.isApproved());
        }

        LR newLR = lrRepository.save(lr);

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentBudget(newLR.getDocumentsId());

        // Create history
        historyService.createHistory(newLR, updatedLR.getUserId(), fieldName, oldValue, newValue);

        return newLR;
    }

    public void deleteLRById(String id, String userId) {
        LR lr = getLRById(id);

        // Update the selected UACS code
        jevService.updateJEVAmount(lr.getDocumentsId());

        // Create History
        historyService.createHistory(lr, userId, false);

        lrRepository.delete(lr);

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentBudget(lr.getDocumentsId());
    }
}
