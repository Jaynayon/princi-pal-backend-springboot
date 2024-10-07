package com.it332.principal.Repository;

import com.it332.principal.Models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByUserId(String userId);

    // Find notifications by rejection status
    List<Notification> findByUserIdAndIsRejected(String userId, boolean isRejected);

    // Optional: Find notifications by association ID if needed
    List<Notification> findByAssocId(String assocId);

    List<Notification> findByAssocIdIn(List<String> assocId);

    List<Notification> findBySchoolId(String schoolId);

    default List<Notification> findAllSortedByTimestamp() {
        return findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
    }
    void deleteByUserId(String userId);
}
