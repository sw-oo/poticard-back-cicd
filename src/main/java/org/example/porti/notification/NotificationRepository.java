package org.example.porti.notification;

import org.example.porti.notification.model.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    Optional<NotificationEntity> findByEndpoint(String endpoint); // 엔드포인트 중복 방지용
}
