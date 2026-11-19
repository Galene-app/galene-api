package br.com.galene.api.domain.identity.entity;

import br.com.galene.api.common.converter.BooleanToNumberConverter;
import br.com.galene.api.domain.identity.enums.TermType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gln_term")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class TermEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_term")
    private Long id;

    @Enumerated(EnumType.STRING)
    private TermType termType;

    private String termVersion;
    private String title;

    @Lob
    private String content;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean mandatory;

    private LocalDateTime publishedAt;
}
