package br.com.galene.api.domain.notification.repository;

import br.com.galene.api.domain.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM NotificationEntity n WHERE n.userId = :userId")
    int deleteAllOfUser(@Param("userId") Long userId);
}
