package com.priyansh.notification_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.priyansh.notification_system.entity.Notification;
import com.priyansh.notification_system.entity.NotificationStatus;
import com.priyansh.notification_system.entity.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<User> findByUserId(String userId);

    List<Notification> findByStatusAndRetryCountLessThan(
            NotificationStatus status,
            Integer maxRetries);

    Optional<Notification> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.status = :newStatus
            WHERE n.id = :id
            AND n.status IN :allowedStatuses
            """)
    int updateStatusIfAllowed(
            @Param("id") Long id,
            @Param("newStatus") NotificationStatus newStatus,
            @Param("allowedStatuses") List<NotificationStatus> allowedStatuses);

}
