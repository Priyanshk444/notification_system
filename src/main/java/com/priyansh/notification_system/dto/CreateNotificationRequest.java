package com.priyansh.notification_system.dto;

import com.priyansh.notification_system.entity.NotiicationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateNotificationRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    @NotNull
    private NotiicationType type; // IN_APP, EMAIL
}
