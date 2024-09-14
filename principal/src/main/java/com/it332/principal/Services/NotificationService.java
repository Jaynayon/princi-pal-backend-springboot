package com.it332.principal.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public String getAssociationId(String id) throws NotFoundException {
        // Retrieve the notification by its ID from the repository
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Notification not found"));

        // Assuming `getAssociationId` is a method in Notification or has a field `associationId`
        return notification.getAssocId(); // This method or field should exist in Notification
    }

    public Notification acceptNotification(String id) {
        try {
            Notification notification = getNotificationById(id);
            if (notification == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
            }
            notification.setAccepted(true);
            notification.setRejected(false); // Ensure rejection flag is reset
            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating notification", e);
        }
    }

    public Notification rejectNotification(String id) {
        try {
            Notification notification = getNotificationById(id);
            if (notification == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found");
            }
            notification.setAccepted(false); // Ensure acceptance flag is reset
            notification.setRejected(true);
            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating notification", e);
        }
    }
    

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
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
            notification.setAccepted(false);
            notification.setRejected(false);
            createNotification(notification);
        }
    }
}
