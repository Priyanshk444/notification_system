package com.priyansh.notification_system.serviceImpl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.priyansh.notification_system.entity.Notification;
import com.priyansh.notification_system.services.sender.NotificationSender;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EmailNotificationSender implements NotificationSender {

    private final JavaMailSender mailSender;

    @Override
    public void send(Notification notification) {
        // Implementation for sending email notification
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("priyanshkumar444@gmail.com");
        message.setSubject(
                notification.getTitle() != null
                        ? notification.getTitle()
                        : "New Notification"
        );
        message.setText(notification.getContent());
        mailSender.send(message);
    }

}
