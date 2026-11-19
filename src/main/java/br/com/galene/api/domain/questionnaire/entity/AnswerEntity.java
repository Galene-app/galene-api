package br.com.galene.api.domain.questionnaire.entity;

import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "gln_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AnswerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_answer")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_patient", nullable = false)
    private PatientEntity patient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_question", nullable = false)
    private QuestionEntity question;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_option", nullable = false)
    private QuestionOptionEntity option;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_user", nullable = false)
    private UserEntity user;

    private LocalDateTime answeredAt;

    public AnswerEntity(PatientEntity patient, QuestionOptionEntity option, UserEntity user, LocalDateTime answeredAt) {
        this.patient = patient;
        this.question = option.getQuestion();
        this.option = option;
        this.user = user;
        this.answeredAt = answeredAt;
    }
}
