package com.it332.principal.Services;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.bson.types.ObjectId;

import com.it332.principal.DTO.HistoryRequest;
import com.it332.principal.DTO.HistoryResponse;
import com.it332.principal.DTO.UserDetails;
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

            if (req.isCreated() || req.isDeleted()) {
                history.setCreated(req.isCreated());
                history.setDeleted(req.isDeleted());
                history.setLrCopy(lrExist); // Add copy if LR is created/deleted
            }
        }

        // If all entities are valid, create and save the history
        return historyRepository.save(history);
    }

    // For create/delete history
    public History createHistory(LR lr, String userId, boolean createdOrDeleted) {
        History history = new History();
        LR lrExist = getLRById(lr.getId());
        Documents docExist = documentsService.getDocumentById(lr.getDocumentsId());
        User userExist = userService.getUserById(userId);

        // Check if any of the fetched objects are null
        if (lrExist != null && docExist != null && userExist != null) {
            history.setLrId(lrExist.getId());
            history.setDocumentsId(docExist.getId());
            history.setUserId(userExist.getId());

            if (createdOrDeleted) {
                history.setCreated(true);
            } else {
                history.setDeleted(true);
            }
            history.setLrCopy(lrExist); // Add copy if LR is created/deleted
        }

        // If all entities are valid, create and save the history
        return historyRepository.save(history);
    }

    // For modify history
    public History createHistory(LR lr, String userId, String fieldName, String oldValue, String newValue) {
        History history = new History();
        LR lrExist = getLRById(lr.getId());
        Documents docExist = documentsService.getDocumentById(lr.getDocumentsId());
        User userExist = userService.getUserById(userId);

        // Check if any of the fetched objects are null
        if (lrExist != null && docExist != null && userExist != null) {
            history.setLrId(lrExist.getId());
            history.setDocumentsId(docExist.getId());
            history.setUserId(userExist.getId());
            history.setFieldName(fieldName);
            history.setOldValue(oldValue);
            history.setNewValue(newValue);
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
        // Fetch all histories sorted by updateDate
        List<History> lrHistory = historyRepository.findAllByLrId(lrId, Sort.by(Sort.Direction.DESC, "updateDate"));

        // Extract unique user IDs from the history records
        Set<String> userIds = lrHistory.stream()
                .map(History::getUserId)
                .collect(Collectors.toSet());

        // Fetch all users in one batch based on user IDs
        Map<String, UserDetails> users = userService.getUsersByIds(userIds).stream()
                .collect(Collectors.toMap(UserDetails::getId, user -> user));

        // Use Stream API to map histories to responses
        return lrHistory.stream()
                .map(history -> {
                    HistoryResponse hr = new HistoryResponse(history);
                    hr.setUser(users.get(history.getUserId())); // Set user details
                    return hr;
                })
                .collect(Collectors.toList());
    }

    // Get the most recent history by lrId
    public HistoryResponse getLastHistoryByLrId(String lrId) {
        // Fetch the most recent history (sorted by updateDate in descending order,
        // limited to 1 result)
        History lrHistory = historyRepository.findFirstByLrIdOrderByUpdateDateDesc(lrId);

        // If history is null, return null or handle accordingly
        if (lrHistory == null) {
            return null;
        }

        // Fetch the user details for the associated userId
        UserDetails user = new UserDetails(userService.getUserById(lrHistory.getUserId()));

        // Create and return the response
        HistoryResponse hr = new HistoryResponse(lrHistory);
        hr.setUser(user); // Set user details
        return hr;
    }

    // Get all History by documentsId
    public List<HistoryResponse> getHistoryByDocumentsId(String documentsId) {
        // Fetch all histories sorted by updateDate
        List<History> lrHistory = historyRepository.findAllByDocumentsId(documentsId,
                Sort.by(Sort.Direction.DESC, "updateDate"));

        // Extract unique user IDs from the history records
        Set<String> userIds = lrHistory.stream()
                .map(History::getUserId)
                .collect(Collectors.toSet());

        // Fetch all users in one batch based on user IDs
        Map<String, UserDetails> users = userService.getUsersByIds(userIds).stream()
                .collect(Collectors.toMap(UserDetails::getId, user -> user));

        // Use Stream API to map histories to responses
        return lrHistory.stream()
                .map(history -> {
                    HistoryResponse hr = new HistoryResponse(history);
                    hr.setUser(users.get(history.getUserId())); // Set user details
                    return hr;
                })
                .collect(Collectors.toList());
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
