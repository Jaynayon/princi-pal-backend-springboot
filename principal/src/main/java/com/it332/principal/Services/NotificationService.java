package com.it332.principal.Services;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.it332.principal.Models.Notification;
import com.it332.principal.Models.Association;
import com.it332.principal.Models.School;
import com.it332.principal.Repository.AssociationRepository;
import com.it332.principal.Repository.NotificationRepository;
import com.it332.principal.Security.NotFoundException;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private SchoolService schoolService;

    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    public List<Notification> getNotificationsByUserAssociation(String userId) {
        // Step 1: Fetch user-specific notifications (no association approval applied)
        List<Notification> userNotifications = getNotificationsByUserId(userId);

        // Step 2: Fetch all associations for the user
        List<Association> associations = associationRepository.findByUserId(userId);

        // Step 3: If the user has associations, fetch the association IDs (only for
        // approved associations)
        List<String> approvedAssocIds = associations.stream()
                .filter(Association::isApproved) // Only include associations where the user is approved
                .map(Association::getId) // Extract approved association IDs
                .collect(Collectors.toList());

        // Step 4: Fetch notifications that are associated with approved assocIds (only
        // if there are approved associations)
        List<Notification> assocNotifications = !approvedAssocIds.isEmpty()
                ? notificationRepository.findByAssocIdIn(approvedAssocIds)
                : Collections.emptyList();

        // Step 5: Combine user-specific and approved association-related notifications,
        // avoid duplicates
        Set<Notification> combinedNotifications = new HashSet<>(userNotifications); // Use Set to avoid duplicates
        combinedNotifications.addAll(assocNotifications);

        // Step 6: Fetch school-related notifications where assocId is null, only for
        // approved associations
        for (Association association : associations) {
            if (association.isApproved()) {
                String schoolId = association.getSchoolId();
                List<Notification> schoolNotificationsWithoutAssocId = notificationRepository.findBySchoolId(schoolId)
                        .stream()
                        .filter(notification -> notification.getAssocId() == null
                                || notification.getAssocId().isEmpty()) // Fetch notifications with no assocId
                        .collect(Collectors.toList());

                combinedNotifications.addAll(schoolNotificationsWithoutAssocId); // Avoid duplicates via Set
            }
        }

        // Step 7: Convert the set back to a list and sort by timestamp (if Notification
        // has a getTimestamp method)
        List<Notification> sortedNotifications = new ArrayList<>(combinedNotifications);
        sortedNotifications.sort(Comparator.comparing(Notification::getTimestamp).reversed());

        // Return the sorted list of notifications
        return sortedNotifications;
    }

    public List<Notification> getNotificationsByUserId(String userId) {
        return notificationRepository.findByUserId(userId);
    }

    public List<Notification> getBudgetLimitNotifications() {
        return notificationRepository.findAll().stream()
                .filter(n -> n.getDetails() != null && n.getDetails().toLowerCase().contains("budget limit"))
                .collect(Collectors.toList());
    }

    public List<Notification> getBudgetLimitExceededNotifications() {
        // Retrieve all notifications where the details indicate a budget limit has been
        // exceeded
        return notificationRepository.findAll().stream()
                .filter(n -> n.getDetails() != null
                        && n.getDetails().toLowerCase().contains("budget limit")
                        && n.getDetails().toLowerCase().contains("exceeded"))
                .collect(Collectors.toList());
    }

    public List<Notification> getBudgetExceededNotifications(String schoolId) {
        List<Notification> notifications = notificationRepository.findBySchoolId(schoolId);

        // Filter notifications with "has exceeded the cash advance" in their details
        return notifications.stream()
                .filter(n -> n.getDetails() != null &&
                        n.getDetails().toLowerCase().contains("negative"))
                .collect(Collectors.toList());
    }

    public List<Notification> getApprovedNotificationsByUserId(String userId) {
        // Fetch notifications where 'details' contain the keyword 'Congratulations'
        return notificationRepository.findByUserId(userId).stream()
                .filter(notification -> notification.getDetails() != null &&
                        notification.getDetails().toLowerCase().contains("congratulations"))
                .collect(Collectors.toList());
    }

    public List<Notification> getRejectionNotificationsByUserId(String userId) {
        return notificationRepository.findByUserIdAndIsRejected(userId, true);
    }

    public List<Notification> getInvitationNotificationsForUser(String userId) {
        // Fetch notifications for the user containing the word "invited"
        return notificationRepository.findByUserId(userId).stream()
                .filter(notification -> notification.getDetails() != null &&
                        notification.getDetails().toLowerCase().contains("invited"))
                .collect(Collectors.toList());
    }

    public Notification updateAcceptInvitationNotification(String notificationId) {
        // Find the notification by its ID
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id: " + notificationId));

        // Retrieve the school associated with the notification
        String schoolId = notification.getSchoolId();
        School school = schoolService.getSchoolById(schoolId);
        String schoolName = school.getFullName(); // Fetch the school's full name

        // Update the notification details to include the school name and set the button
        // state to false
        notification.setSchoolId(null);
        notification.setDetails("You accepted the invitation to join " + schoolName);
        notification.setHasButtons(false);
        notification.setAccepted(true);
        notification.setRejected(false);

        // Save and return the updated notification
        return notificationRepository.save(notification);
    }

    public Notification updateRejectInvitationNotification(String notificationId) {
        try {
            // Retrieve the notification by its ID
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found"));

            // Retrieve the associated school details for the notification
            String schoolId = notification.getSchoolId();
            School school = schoolService.getSchoolById(schoolId);
            String schoolName = school.getFullName(); // Fetch the school's full name

            // Extract the association ID from the notification and delete the association
            String assocId = notification.getAssocId();
            if (assocId != null && !assocId.isEmpty()) {
                Association association = associationRepository.findById(assocId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Association not found"));

                // Delete the association
                associationRepository.delete(association);
            }

            // Update the notification details to reflect the rejection action
            notification.setSchoolId(null);
            notification.setDetails("You rejected the invitation to join " + schoolName);
            notification.setAccepted(false);
            notification.setRejected(true);
            notification.setHasButtons(false); // Disable buttons as the action is completed

            // Save and return the updated notification
            return notificationRepository.save(notification);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error updating notification", e);
        }
    }

    public void deleteNotificationsByUserId(String userId) {
        List<Notification> notifications = notificationRepository
                .findByUserIdAndHasButtonsIsFalse(userId);
        notificationRepository.deleteAll(notifications);
    }
}
