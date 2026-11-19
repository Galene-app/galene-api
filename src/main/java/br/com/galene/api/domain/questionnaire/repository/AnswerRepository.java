package br.com.galene.api.domain.questionnaire.repository;

import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.questionnaire.entity.AnswerEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    @EntityGraph(attributePaths = {"question", "option"})
    List<AnswerEntity> findByPatientId(Long patientId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM AnswerEntity a WHERE a.patient.id = :patientId")
    int deleteAllOfPatient(@Param("patientId") Long patientId);

    @Modifying(flushAutomatically = true)
    @Query("DELETE FROM AnswerEntity a WHERE a.patient.id = :patientId AND a.question.id IN "
            + "(SELECT q.id FROM QuestionEntity q WHERE q.requiredConsent = :termType)")
    int deleteRequiringConsent(@Param("patientId") Long patientId, @Param("termType") TermType termType);
}
