package br.com.galene.api.domain.identity.entity;

import br.com.galene.api.common.converter.BooleanToNumberConverter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "gln_patient")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PatientEntity {

    public static final BigDecimal DEFAULT_DB_LIMIT = new BigDecimal("70.00");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_patient")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    private String patientCode;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean hasMedicalCare;

    // Condição declarada pelo usuário: só pode ser gravada com o consentimento HEALTH_DATA
    private String diagnosis;

    @Column(precision = 5, scale = 2)
    private BigDecimal dbLimit;

    private LocalDate startDate;

    public PatientEntity(UserEntity user, String patientCode, LocalDate startDate) {
        this.user = user;
        this.patientCode = patientCode;
        this.hasMedicalCare = false;
        this.dbLimit = DEFAULT_DB_LIMIT;
        this.startDate = startDate;
    }
}
