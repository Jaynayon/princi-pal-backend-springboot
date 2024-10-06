package com.it332.principal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationRequest {
    private String userId;
    private String schoolId;
    private String details;
}
