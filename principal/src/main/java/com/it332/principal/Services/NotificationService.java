package com.it332.principal.Services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.stream.Stream;

import com.it332.principal.Models.Association;
import com.it332.principal.Models.Notification;
import com.it332.principal.Repository.NotificationRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AssociationService associationService;

    @Autowired
    private UserService userService;

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
    
    public void deleteNotificationsByUser(String userId) {
        try {
            notificationRepository.deleteByUserId(userId);
        } catch (Exception e) {
            // Log the error for debugging purposes
            e.printStackTrace();
            throw new RuntimeException("Failed to delete notifications for user ID: " + userId, e);
        }
    }
    

    public void deleteNotification(String id) {
        // Fetch and validate the notification
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Invalid notificationId: " + id));
        
        // Delete the notification
        notificationRepository.delete(notification);
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

    // New method to get notifications through user's associations
    public List<Notification> getNotificationsByUserAssociations(String userId) {
        // Get all associations for the user
        List<Association> associations = associationService.getAssociationsByUserId(userId);

        // Collect all association IDs
        List<String> assocId = associations.stream()
                                             .map(Association::getId)
                                             .collect(Collectors.toList());

        // Retrieve notifications for the collected association IDs
        return notificationRepository.findByAssocIdIn(assocId);
    }

   public List<Notification> getNotificationsByUserIdThroughAssociations(String userId) {
    // Get notifications for the user directly
    List<Notification> notifications = getNotificationsByUserId(userId);

    // Get notifications for associations
    List<Notification> associationNotifications = getNotificationsByUserAssociations(userId);

    // Use a Map to remove duplicates by notification ID
    Map<String, Notification> notificationMap = new HashMap<>();

    // Add direct user notifications to the map
    for (Notification notification : notifications) {
        notificationMap.put(notification.getId(), notification);
    }

    // Add association notifications to the map, overriding duplicates
    for (Notification notification : associationNotifications) {
        notificationMap.put(notification.getId(), notification);
    }

    // Return a combined list of unique notifications
    return new ArrayList<>(notificationMap.values());
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
