package br.com.galene.api.domain.monitoring.entity;

import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.monitoring.enums.LocationType;
import br.com.galene.api.domain.monitoring.enums.MeasurementSource;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gln_measurement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class MeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_measurement")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_patient", nullable = false)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_level", nullable = false)
    private IntensityLevelEntity level;

    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    @Column(precision = 5, scale = 2)
    private BigDecimal dbValue;

    private LocalDateTime measuredAt;

    @Enumerated(EnumType.STRING)
    private MeasurementSource source;
}
