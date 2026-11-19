package br.com.galene.api.domain.questionnaire.entity;

import br.com.galene.api.common.converter.BooleanToNumberConverter;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.questionnaire.enums.QuestionCode;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gln_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_question")
    private Long id;

    private String questionText;
    private Integer displayOrder;

    @Convert(converter = BooleanToNumberConverter.class)
    private Boolean active;

    @Enumerated(EnumType.STRING)
    private QuestionCode questionCode;

    private Integer minChoices;
    private Integer maxChoices;

    @Enumerated(EnumType.STRING)
    private TermType requiredConsent;

    @OneToMany(mappedBy = "question")
    @OrderBy("displayOrder")
    private List<QuestionOptionEntity> options = new ArrayList<>();
}
