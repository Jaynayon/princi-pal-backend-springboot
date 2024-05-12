package com.it332.principal.Services;

import com.it332.principal.DTO.JEVRequest;
import com.it332.principal.Models.Documents;
import com.it332.principal.Models.JEV;
import com.it332.principal.Models.Uacs;
import com.it332.principal.Repository.DocumentsRepository;
import com.it332.principal.Repository.JEVRepository;
import com.it332.principal.Security.NotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JEVService {

    @Autowired
    private JEVRepository jevRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private UacsService uacsService;

    Documents existingDocument;

    public JEV saveJEV(JEVRequest jev) {
        // Validate LR fields if needed before saving (e.g., check for required fields)
        existingDocument = documentsService.getDocumentById(jev.getDocumentsId());
        Uacs existingUacs = uacsService.getUacsByCode(jev.getObjectCode());

        // Save the LR first
        JEV newJev = jevRepository.save(new JEV(jev, existingUacs));

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentAmount(jev.getDocumentsId());

        return newJev;
    }

    public void updateDocumentAmount(String id) {
        // Find all LR objects with the specified documentId
        existingDocument = documentsService.getDocumentById(id);
        List<JEV> lrList = getAllJEVsByDocumentsId(id);

        // Calculate the sum of amounts from the LR list
        double totalAmount = lrList.stream()
                .mapToDouble(JEV::getAmount)
                .sum();
        // Update the Documents amount property with the calculated total amount
        existingDocument.setCashAdvance(totalAmount);

        // Save new sum
        documentsRepository.save(existingDocument);
    }

    public JEV getJEVById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        return jevRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JEV not found with ID: " + id));
    }

    public List<JEV> getAllJEVs() {
        // List<LR> lrList = lrRepository.findAll();
        return jevRepository.findAll();
    }

    // Method to retrieve all LR documents with the same documentsId
    public List<JEV> getAllJEVsByDocumentsId(String documentsId) {
        existingDocument = documentsService.getDocumentById(documentsId);
        List<JEV> lrList = jevRepository.findByDocumentsId(documentsId);

        if (existingDocument == null) {
            throw new NotFoundException("LR not found with ID: " + documentsId);
        }

        // return lrList.stream()
        // .map(LRResponse::new) // Map each LR to LRResponse using constructor
        // .collect(Collectors.toList());
        return lrList;
    }

    public JEV updateJEV(String id, JEVRequest updatedJEV) {
        JEV jev = getJEVById(id);

        // Update LR fields based on the provided updatedLR object
        if (updatedJEV.getAmount() != null) {
            jev.setAmount(updatedJEV.getAmount());
        }
        if (updatedJEV.getCredit() != null) {
            jev.setCredit(updatedJEV.getCredit());
        }

        JEV newJEV = jevRepository.save(jev);

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentAmount(jev.getDocumentsId());

        return newJEV;
    }

    public void deleteJEVById(String id) {
        JEV lr = getJEVById(id);

        jevRepository.delete(lr);

        // Update the associated Document's budget based on the saved LR's amount
        updateDocumentAmount(lr.getDocumentsId());
    }
}
