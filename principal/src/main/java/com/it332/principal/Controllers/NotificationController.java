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

import com.it332.principal.Models.Notification;
import com.it332.principal.Services.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotification() {
        List<Notification> notificationOptional = notificationService.getAllNotifications();

        return ResponseEntity.ok().body(notificationOptional);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable String id) {
        Notification notificationOptional = notificationService.getNotificationById(id);

        return ResponseEntity.ok().body(notificationOptional);
    }

    @PostMapping("/create")
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        Notification createdNotification = notificationService.createNotification(notification);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> clearNotificationById(@PathVariable String id) {
        notificationService.clearAllNotificationsByUserId(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Notification> acceptNotification(@PathVariable String id) {
        Notification acceptedNotification = notificationService.acceptNotification(id);
        return ResponseEntity.ok().body(acceptedNotification);
    }

    // Endpoint for rejecting a notification
    @PutMapping("/reject/{id}")
    public ResponseEntity<Notification> rejectNotification(@PathVariable String id) {
        Notification rejectedNotification = notificationService.rejectNotification(id);
        return ResponseEntity.ok().body(rejectedNotification);
    }
}
