package br.com.galene.api.domain.identity.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gln_user_session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_session")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    private String refreshTokenHash;
    private LocalDateTime createdAt;
    private LocalDateTime refreshedAt;
    private LocalDateTime revokedAt;

    public UserSessionEntity(UserEntity user, String refreshTokenHash, LocalDateTime now) {
        this.user = user;
        this.refreshTokenHash = refreshTokenHash;
        this.createdAt = now;
        this.refreshedAt = now;
    }
}
