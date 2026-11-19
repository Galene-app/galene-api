package br.com.galene.api.domain.monitoring.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "gln_intensity_level")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class IntensityLevelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_level")
    private Long id;

    private String levelName;

    @Column(precision = 5, scale = 2)
    private BigDecimal dbMin;

    @Column(precision = 5, scale = 2)
    private BigDecimal dbMax;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(length = 7)
    private String colorHex;

    private String galeneAction;

    public boolean contains(BigDecimal dbValue) {
        return dbMin.compareTo(dbValue) <= 0 && dbMax.compareTo(dbValue) >= 0;
    }

    public boolean startsAtOrAbove(BigDecimal threshold) {
        return dbMin.compareTo(threshold) >= 0;
    }
}
