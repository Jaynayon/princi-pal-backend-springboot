package com.it332.principal.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.Notification;
import com.it332.principal.Models.Association;
import com.it332.principal.Models.User;
import com.it332.principal.Repository.AssociationRepository;
import com.it332.principal.Repository.NotificationRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AssociationService associationService;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(String userId) {
        return notificationRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Notification not found" + userId));
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public void clearAllNotificationsByUserId(String userId) {
        List<Notification> notifications = notificationRepository.findByUserId(userId);
        notificationRepository.deleteAll(notifications);
    }
    
    public Notification acceptNotification(String id) {
        Notification notification = getNotificationById(id);
        notification.setAccepted(true);
        notification.setRejected(false); // Ensure rejection flag is reset
        return notificationRepository.save(notification);
    }

    // Method to reject a notification
    public Notification rejectNotification(String id) {
        Notification notification = getNotificationById(id);
        notification.setAccepted(false); // Ensure acceptance flag is reset
        notification.setRejected(true);
        return notificationRepository.save(notification);
    }
}
