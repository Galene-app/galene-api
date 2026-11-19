package br.com.galene.api.domain.reporting.entity;

import br.com.galene.api.domain.reporting.enums.ReportType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "gln_report")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_report")
    private Long id;

    @Column(name = "id_patient")
    private Long patientId;

    @Column(name = "id_user")
    private Long userId;

    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime generatedAt;
    private String observation;

    public static ReportEntity daily(Long patientId, Long userId, LocalDate date, LocalDateTime generatedAt) {
        return new ReportEntity(null, patientId, userId, ReportType.DAILY, date, date, generatedAt, null);
    }
}
