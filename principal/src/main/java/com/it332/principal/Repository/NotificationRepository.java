package com.it332.principal.Repository;

import com.it332.principal.Models.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByUserId(String userId);

    List<Notification> findByAssocId(String assocId);

    List<Notification> findByAssocIdIn(List<String> assocIds);

    // Find notifications by rejection status
    List<Notification> findByUserIdAndIsRejected(String userId, boolean isRejected);

    List<Notification> findBySchoolId(String schoolId);

    List<Notification> findByUserIdAndHasButtonsIsFalse(String userId);

    void deleteByUserId(String userId);
}
