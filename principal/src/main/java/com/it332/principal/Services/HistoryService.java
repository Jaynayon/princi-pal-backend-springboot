package com.it332.principal.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;

import com.it332.principal.DTO.HistoryRequest;
import com.it332.principal.DTO.HistoryResponse;
import com.it332.principal.Models.Documents;
import com.it332.principal.Models.History;
import com.it332.principal.Models.LR;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.HistoryRepository;
import com.it332.principal.Repository.LRRepository;
import com.it332.principal.Security.MissingFieldException;

@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private DocumentsService documentsService;

    @Autowired
    private UserService userService;

    @Autowired
    private LRRepository lrRepository;

    // Create History
    public History createHistory(HistoryRequest req) {
        History history = new History();
        LR lrExist = getLRById(req.getLrId());
        Documents docExist = documentsService.getDocumentById(req.getDocumentsId());
        User userExist = userService.getUserById(req.getUserId());

        if (req.getFieldName() == null || req.getFieldName().isEmpty()) {
            throw new MissingFieldException("Field name is missing");
        }
        if (req.getOldValue() == null || req.getOldValue().isEmpty()) {
            throw new MissingFieldException("Old value is missing");
        }
        if (req.getNewValue() == null || req.getNewValue().isEmpty()) {
            throw new MissingFieldException("New value is missing");
        }

        // Check if any of the fetched objects are null
        if (lrExist != null && docExist != null && userExist != null) {
            history.setLrId(lrExist.getId());
            history.setDocumentsId(docExist.getId());
            history.setUserId(userExist.getId());
            history.setFieldName(req.getFieldName());
            history.setOldValue(req.getOldValue());
            history.setNewValue(req.getNewValue());
            history.setCreated(req.isCreated());
            history.setDeleted(req.isDeleted());
        }

        // If all entities are valid, create and save the history
        return historyRepository.save(history);
    }

    // Get all History records
    public List<History> getAllHistories() {
        return historyRepository.findAll(Sort.by(Sort.Direction.DESC, "updateDate"));
    }

    // Get all History by lrId
    public List<HistoryResponse> getHistoryByLrId(String lrId) {
        return historyRepository.findAllByLrId(lrId, Sort.by(Sort.Direction.DESC, "updateDate"));
    }

    // Get all History by documentsId
    public List<HistoryResponse> getHistoryByDocumentsId(String documentsId) {
        return historyRepository.findAllByDocumentsId(documentsId, Sort.by(Sort.Direction.DESC, "updateDate"));
    }

    // Delete all History entries by lrId
    public void deleteHistoryByLrId(String lrId) {
        historyRepository.deleteAllByLrId(lrId);
    }

    public LR getLRById(String id) {
        // Validate the format of the provided ID
        if (!ObjectId.isValid(id)) {
            throw new IllegalArgumentException("Invalid ID format");
        }

        return lrRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LR not found with ID: " + id));
    }

}
