package br.com.galene.api.domain.sound.entity;

import br.com.galene.api.common.converter.BooleanToNumberConverter;
import br.com.galene.api.domain.sound.enums.SoundType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gln_sound")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SoundEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sound")
    private Long id;

    private String soundName;

    @Enumerated(EnumType.STRING)
    private SoundType soundType;

    private Integer durationSec;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean premiumOnly;

    private String mediaUrl;
}
