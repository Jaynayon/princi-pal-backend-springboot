package com.it332.principal.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.it332.principal.Models.Notification;
import com.it332.principal.Services.AssociationService;
import com.it332.principal.Services.NotificationService;
import com.it332.principal.Security.NotFoundException;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AssociationService associationService;

    @GetMapping("/user/{userId}/associations")
    public ResponseEntity<List<Notification>> getNotificationsByUserAssociation(@PathVariable String userId) {
        try {
            // Call the new service method to get notifications based on user associations
            List<Notification> notifications = notificationService.getNotificationsByUserAssociation(userId);

            if (notifications.isEmpty()) {
                return ResponseEntity.noContent().build(); // Return 204 No Content if no notifications found
            }

            return ResponseEntity.ok(notifications); // Return 200 OK with the notifications
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR); // Return 500 Internal Server Error in
                                                                                 // case of exceptions
        }
    }

    @GetMapping("/budget")
    public ResponseEntity<List<Notification>> getBudgetNotifications() {
        try {
            // Use the service method to retrieve all notifications related to budget limits
            List<Notification> notifications = notificationService.getBudgetLimitNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/budget-exceeded")
    public ResponseEntity<List<Notification>> getBudgetLimitExceededNotifications() {
        try {
            // Use the service method to retrieve all notifications related to budget limits
            // being exceeded
            List<Notification> notifications = notificationService.getBudgetLimitExceededNotifications();

            if (notifications.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/negative/{schoolId}")
    public ResponseEntity<List<Notification>> getBudgetExceededNotifications(@PathVariable String schoolId) {
        List<Notification> notifications = notificationService.getBudgetExceededNotifications(schoolId);
        if (notifications.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(notifications);
        }
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{userId}/approved")
    public ResponseEntity<List<Notification>> getApprovedNotifications(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getApprovedNotificationsByUserId(userId);
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build(); // No approval notifications found for this user
        }
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{userId}/rejected")
    public ResponseEntity<List<Notification>> getRejectionNotificationsByUserId(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getRejectionNotificationsByUserId(userId);
        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build(); // No rejection notifications found for this user
        }
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{userId}/invitation")
    public ResponseEntity<List<Notification>> getInvitationNotificationsForUser(@PathVariable String userId) {
        List<Notification> notifications = notificationService.getInvitationNotificationsForUser(userId);

        if (notifications.isEmpty()) {
            return ResponseEntity.noContent().build(); // Return 204 if no notifications found
        }

        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/accept/{notificationId}")
    public ResponseEntity<Notification> updateAcceptInvitationNotification(
            @PathVariable String notificationId) {
        try {
            if (notificationId == null || notificationId.isEmpty()) {
                throw new IllegalArgumentException("Invalid request: notificationId is required.");
            }

            // Call the association approval logic first (similar to the axios POST request)
            associationService.approveInvitation(notificationId);

            // Update the notification details
            Notification notification = notificationService.updateAcceptInvitationNotification(notificationId);

            return ResponseEntity.ok(notification);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/reject/{notificationId}")
    public ResponseEntity<Notification> rejectInvitation(@PathVariable String notificationId) {
        try {
            // Call the service method to update the notification and delete the related
            // association
            Notification updatedNotification = notificationService.updateRejectInvitationNotification(notificationId);
            return ResponseEntity.ok(updatedNotification);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteNotificationsByUserId(@PathVariable String userId) {
        try {
            notificationService.deleteNotificationsByUserId(userId);
            return ResponseEntity.ok("All notifications deleted for user ID: " + userId);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete notifications: " + e.getMessage());
        }
    }

}
