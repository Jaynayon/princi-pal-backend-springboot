package com.it332.principal.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.it332.principal.Models.Notification;
import com.it332.principal.Services.NotificationService;
import com.it332.principal.Security.NotFoundException;

@RestController
@RequestMapping("/Notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok().body(notifications);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable String userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
            return ResponseEntity.ok().body(notifications);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        try {
            // Check if the notification is an invitation
            boolean isInvitation = notification.getDetails().toLowerCase().contains("invited");

            // Set the button flag based on whether it's an invitation
            notification.setHasButtons(isInvitation);

            Notification createdNotification = notificationService.createNotification(notification);
            return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Notification> approveNotification(@PathVariable String id) {
        try {
            Notification updatedNotification = notificationService.acceptNotification(id);
            return ResponseEntity.ok(updatedNotification);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Notification> rejectNotification(@PathVariable("id") String id) {
        try {
            Notification notification = notificationService.rejectNotification(id);
            return new ResponseEntity<>(notification, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            // Return the error response with status code directly
            return new ResponseEntity<>(null, e.getStatusCode());
        } catch (Exception e) {
            // Return a generic error response for unexpected issues
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/school/{schoolId}")
    public List<Notification> getNotificationsForSchool(@PathVariable String schoolId) {
        return notificationService.getNotificationsBySchool(schoolId);
    }

    @GetMapping("/{userId}/associations")
    public ResponseEntity<List<Notification>> getNotificationsByUserAssociations(@PathVariable String userId) {
        // Fetch notifications through user's associations
        List<Notification> notifications = notificationService.getNotificationsByUserAssociations(userId);

        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<Notification>> getNotificationsByUserIdThroughAssociations(
            @PathVariable String userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserIdThroughAssociations(userId);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<String> deleteNotificationsByUser(@PathVariable String userId) {
        try {
            // Call the service method to delete notifications
            notificationService.deleteNotificationsByUser(userId);

            // Return a success response
            return ResponseEntity.ok("Notifications for user ID " + userId + " have been deleted.");
        } catch (RuntimeException e) {
            // Return a bad request response if there's an error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error clearing notifications: " + e.getMessage());
        }
    }

    // Endpoint to delete a notification based on the notificationId
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable String notificationId) {
        try {
            // Call the service method to delete the notification
            notificationService.deleteNotification(notificationId);

            // Return a success response
            return ResponseEntity.ok("Notification with ID " + notificationId + " has been deleted.");
        } catch (IllegalArgumentException e) {
            // Return a bad request response if there's an error
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
