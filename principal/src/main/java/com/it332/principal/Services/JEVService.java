package com.it332.principal.Services;

import com.it332.principal.DTO.JEVRequest;
import com.it332.principal.DTO.JEVResponse;
import com.it332.principal.Models.Documents;
import com.it332.principal.Models.JEV;
import com.it332.principal.Models.LR;
import com.it332.principal.Models.Uacs;
import com.it332.principal.Repository.DocumentsRepository;
import com.it332.principal.Repository.JEVRepository;
import com.it332.principal.Repository.LRRepository;
import com.it332.principal.Security.NotFoundException;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JEVService {

    @Autowired
    private JEVRepository jevRepository;

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private LRRepository lrRepository;

    @Autowired
    private UacsService uacsService;

    Documents existingDocument;

    public JEV saveJEV(JEVRequest jev) {
        // Validate LR fields if needed before saving (e.g., check for required fields)
        existingDocument = getDocumentById(jev.getDocumentsId());
        Uacs existingUacs = uacsService.getUacsByCode(jev.getObjectCode());

        // Check if JEV exists
        JEV exist = jevRepository.findByDocumentsIdAndUacs_Code(jev.getDocumentsId(), jev.getObjectCode());

        if (exist != null) {
            throw new IllegalArgumentException("JEV with code " + jev.getObjectCode() + " already exists in Document: "
                    + jev.getDocumentsId());
        }

        // Save the JEV first
        JEV newJev = jevRepository.save(new JEV(jev, existingUacs));

        // Update the associated JEV's budgetExceeded status based on JEV's budget
        // updateJEVAmount(newJev.getDocumentsId());

        return newJev;
    }

    public void initializeJEV(String documentId) {
        List<Uacs> allUacs = uacsService.getAllUacs();

        // Iterate over each Uacs object in the list using a for-each loop
        for (Uacs uacs : allUacs) {
            saveJEV(new JEVRequest(uacs.getCode(), documentId));
        }
    }

    public Documents getDocumentById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        return documentsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found with ID: " + id));
    }

    public JEV findExistingJEV(String documentsId, String uacsCode) {
        Documents existingDoc = getDocumentById(documentsId);
        JEV exist = jevRepository.findByDocumentsIdAndUacs_Code(existingDoc.getId(), uacsCode);

        if (exist == null) {
            throw new NotFoundException("JEV not found with ID: " + documentsId + "and code" + uacsCode);
        }

        return exist;
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
        return jevRepository.findAll();
    }

    // Method to retrieve all LR documents with the same documentsId
    public List<JEVResponse> getAllJEVsByDocumentsId(String documentsId) {
        // Get if documents exists
        existingDocument = getDocumentById(documentsId);

        // Fetch LR and JEV lists by documentsId
        List<LR> lrList = lrRepository.findByDocumentsIdOrderByDateAsc(documentsId);
        List<JEV> jevList = jevRepository.findByDocumentsId(documentsId);

        // Iterate over each JEV to update its amount
        for (JEV jev : jevList) {
            String code = jev.getUacs().getCode(); // Get the UACS code of the JEV

            // Initialize the sum for this JEV's code
            float sum = 0.0f;

            // Sum all LR amounts that have the same objectCode as the JEV's UACS code
            for (LR lr : lrList) {
                if (lr.getObjectCode().equals(code)) { // Use .equals() for string comparison
                    sum += lr.getAmount(); // Accumulate the amount from matching LR entries
                }
            }

            // Update the JEV's amount with the computed sum
            jev.setAmount(sum); // Set the new sum to the JEV's amount
        }

        // Filter list to be returned
        return jevList.stream()
                .map(JEVResponse::new) // Map each LR to LRResponse using constructor
                .collect(Collectors.toList());
    }

    public void updateJEVAmount(String documentsId) {
        existingDocument = getDocumentById(documentsId);

        // Fetch LR and JEV lists by documentsId
        List<LR> lrList = lrRepository.findByDocumentsIdOrderByDateAsc(documentsId);
        List<JEV> jevList = jevRepository.findByDocumentsId(documentsId);

        // Iterate over each JEV to update its amount
        for (JEV jev : jevList) {
            String code = jev.getUacs().getCode(); // Get the UACS code of the JEV

            // Calculate the sum for this JEV's code
            float sum = (float) lrList.stream()
                    .filter(lr -> code.equals(lr.getObjectCode()))
                    .mapToDouble(LR::getAmount)
                    .sum();

            // Update the JEV's amount with the computed sum
            jev.setAmount(sum); // Set the new sum to the JEV's amount
            jev.setBudgetExceeded(sum > jev.getBudget()); // Set budget exceeded status
        }

        jevRepository.saveAll(jevList);
    }

    public JEV updateJEV(String id, JEVRequest updatedJEV) {
        JEV jev = getJEVById(id);

        // Update LR fields based on the provided updatedLR object
        if (updatedJEV.getAmount() != null) {
            jev.setAmount(updatedJEV.getAmount());
        }
        if (updatedJEV.getAmountType() != null) {
            jev.setAmountType(updatedJEV.getAmountType());
        }
        if (updatedJEV.getBudget() != null) {
            jev.setBudget(updatedJEV.getBudget());
        }

        JEV newJEV = jevRepository.save(jev);

        // Update the associated JEV's budgetExceeded status based on JEV's budget
        updateJEVAmount(newJEV.getDocumentsId());

        return newJEV;
    }

    public void deleteJEVById(String id) {
        JEV lr = getJEVById(id);

        jevRepository.delete(lr);

        // Update the associated Document's budget based on the saved LR's amount
        // updateDocumentAmount(lr.getDocumentsId());
    }

    @Transactional
    public void deleteByDocumentsId(String documentsId) {
        List<JEV> lrList = jevRepository.findByDocumentsId(documentsId);
        jevRepository.deleteAll(lrList);
    }

}
