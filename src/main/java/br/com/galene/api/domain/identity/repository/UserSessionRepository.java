package br.com.galene.api.domain.identity.repository;

import br.com.galene.api.domain.identity.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserSessionRepository extends JpaRepository<UserSessionEntity, Long> {

    @EntityGraph(attributePaths = "user")
    Optional<UserSessionEntity> findByRefreshTokenHash(String refreshTokenHash);

    Optional<UserSessionEntity> findByIdAndUserId(Long id, Long userId);

    @Modifying
    @Query("UPDATE UserSessionEntity s SET s.revokedAt = :now WHERE s.user.id = :userId AND s.revokedAt IS NULL")
    int revokeAllActiveSessions(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
