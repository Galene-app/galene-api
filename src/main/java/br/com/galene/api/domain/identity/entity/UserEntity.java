package br.com.galene.api.domain.identity.entity;

import br.com.galene.api.common.converter.BooleanToNumberConverter;
import br.com.galene.api.common.helpers.EmailHelper;
import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;
import br.com.galene.api.domain.identity.enums.AccountStatus;
import br.com.galene.api.domain.identity.enums.ColorblindMode;
import br.com.galene.api.domain.identity.enums.ProfileType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gln_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    private String fullName;
    private String email;
    private String passwordHash;
    private String phone;
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private ProfileType profileType;

    private String photoUrl;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean darkMode;

    @Enumerated(EnumType.STRING)
    private ColorblindMode colorblindMode;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean ttsEnabled;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String mfaCodeHash;
    private LocalDateTime mfaExpiresAt;

    public UserEntity(RegisterRequestDto dto, String passwordHash, LocalDateTime createdAt) {
        this.fullName = dto.fullName().trim();
        this.email = EmailHelper.normalize(dto.email());
        this.passwordHash = passwordHash;
        this.birthDate = dto.birthDate();
        this.profileType = ProfileType.PATIENT;
        this.accountStatus = AccountStatus.ACTIVE;
        this.darkMode = false;
        this.colorblindMode = ColorblindMode.NONE;
        this.ttsEnabled = false;
        this.createdAt = createdAt;
    }
}
