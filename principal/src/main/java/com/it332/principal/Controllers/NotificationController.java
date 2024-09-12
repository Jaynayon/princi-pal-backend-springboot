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
import com.it332.principal.Repository.NotificationRepository;
import com.it332.principal.Services.NotificationService;
import com.it332.principal.Security.NotFoundException;

@RestController
@RequestMapping("/Notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    private NotificationRepository notificationRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok().body(notifications);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Notification>> getNotificationsByUserId(@PathVariable String userId) {
        try {
            List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
            return ResponseEntity.ok().body(notifications);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/create") // Correct path to match the class level @RequestMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        try {
            Notification createdNotification = notificationService.createNotification(notification);
            return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

     // Endpoint to delete notifications for a school
     @DeleteMapping("/school/{schoolId}")
     public void deleteNotificationsForSchool(@PathVariable String schoolId) {
         notificationService.deleteNotificationsBySchool(schoolId);
     }
    

    @PutMapping("/accept/{id}")
    public ResponseEntity<Notification> acceptNotification(@PathVariable String id) {
        try {
            Notification acceptedNotification = notificationService.acceptNotification(id);
            return ResponseEntity.ok().body(acceptedNotification);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/reject/{id}")
    public ResponseEntity<Notification> rejectNotification(@PathVariable String id) {
        try {
            Notification rejectedNotification = notificationService.rejectNotification(id);
            return ResponseEntity.ok().body(rejectedNotification);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/school/{schoolId}")
    public List<Notification> getNotificationsForSchool(@PathVariable String schoolId) {
        return notificationService.getNotificationsBySchool(schoolId);
    }
}
