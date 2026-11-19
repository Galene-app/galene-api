package br.com.galene.api.domain.questionnaire.entity;

import br.com.galene.api.domain.questionnaire.enums.OptionCode;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "gln_question_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class QuestionOptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_option")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_question", nullable = false)
    private QuestionEntity question;

    private String optionText;

    @Enumerated(EnumType.STRING)
    private OptionCode optionCode;

    private Integer displayOrder;
}
