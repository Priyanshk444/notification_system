package com.priyansh.notification_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.priyansh.notification_system.entity.UserNotificationPreference;

import java.util.Optional;

public interface UserNotificationPreferenceRepository
        extends JpaRepository<UserNotificationPreference, Long> {

    Optional<UserNotificationPreference> findByUserId(Long userId);
}
