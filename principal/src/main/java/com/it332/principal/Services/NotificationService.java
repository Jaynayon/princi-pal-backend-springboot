package com.it332.principal.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.Notification;
import com.it332.principal.Repository.NotificationRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Notification getNotificationById(String id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + id));
    }

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

     // Method to delete notifications for a specific school
     public void deleteNotificationsBySchool(String schoolId) {
        notificationRepository.deleteBySchoolId(schoolId);
    }

    public Notification acceptNotification(String id) {
        Notification notification = getNotificationById(id);
        notification.setAccepted(true);
        notification.setRejected(false); // Ensure rejection flag is reset
        return notificationRepository.save(notification);
    }

    public Notification rejectNotification(String id) {
        Notification notification = getNotificationById(id);
        notification.setAccepted(false); // Ensure acceptance flag is reset
        notification.setRejected(true);
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getNotificationsByUserIdAndReadStatus(String userId, boolean isRead) {
        return notificationRepository.findByUserIdAndIsRead(userId, isRead);
    }

    // Method to get notifications for a specific school
    public List<Notification> getNotificationsBySchool(String schoolId) {
        return notificationRepository.findBySchoolId(schoolId);
    }

    // Method to create a notification if balance exceeds the budget
    public void checkAndNotify(double balance, double budget, String userId) {
        if (balance > budget) {
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setAssocId(null); // Set if needed
            notification.setDetails("Your balance of " + balance + " exceeds the budget of " + budget + ".");
            notification.setRead(false);
            notification.setAccepted(false);
            notification.setRejected(false);
            createNotification(notification);
        }
    }
}
