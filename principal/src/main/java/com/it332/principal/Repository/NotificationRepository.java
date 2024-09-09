package com.it332.principal.Repository;

import com.it332.principal.Models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    List<Notification> findByUserId(String userId);

    // Find notifications by read status
    List<Notification> findByUserIdAndIsRead(String userId, boolean isRead);

    // Find notifications by read status and acceptance status
    List<Notification> findByUserIdAndIsReadAndIsAccepted(String userId, boolean isRead, boolean isAccepted);

    // Find notifications by rejection status
    List<Notification> findByUserIdAndIsRejected(String userId, boolean isRejected);

    // Optional: Find notifications by association ID if needed
    List<Notification> findByAssocId(String assocId);

    List<Notification> findBySchoolId(String schoolId);

    void deleteBySchoolId(String schoolId);

    void deleteByUserId(String userId);
}
