package br.com.galene.api.domain.identity.entity;

import br.com.galene.api.common.converter.BooleanToNumberConverter;
import br.com.galene.api.domain.identity.enums.TermType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gln_consent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ConsentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consent")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_patient")
    private PatientEntity patient;

    @Enumerated(EnumType.STRING)
    private TermType termType;

    private String termVersion;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean accepted;

    private LocalDateTime answeredAt;

    public ConsentEntity(UserEntity user, PatientEntity patient, TermEntity term, boolean accepted, LocalDateTime answeredAt) {
        this.user = user;
        this.patient = patient;
        this.termType = term.getTermType();
        this.termVersion = term.getTermVersion();
        this.accepted = accepted;
        this.answeredAt = answeredAt;
    }

    public static ConsentEntity revocationOf(ConsentEntity consent, LocalDateTime answeredAt) {
        return new ConsentEntity(null, consent.getUser(), consent.getPatient(), consent.getTermType(), consent.getTermVersion(), false, answeredAt);
    }
}
