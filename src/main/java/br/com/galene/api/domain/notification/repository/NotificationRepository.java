package br.com.galene.api.domain.notification.repository;

import br.com.galene.api.domain.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    Page<NotificationEntity> findByUserId(Long userId, Pageable pageable);

    Page<NotificationEntity> findByUserIdAndIsRead(Long userId, Boolean isRead, Pageable pageable);

    Optional<NotificationEntity> findByIdAndUserId(Long id, Long userId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE NotificationEntity n SET n.isRead = :read WHERE n.userId = :userId AND n.isRead <> :read")
    int updateReadOfUser(@Param("userId") Long userId, @Param("read") Boolean read);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM NotificationEntity n WHERE n.userId = :userId")
    int deleteAllOfUser(@Param("userId") Long userId);
}
