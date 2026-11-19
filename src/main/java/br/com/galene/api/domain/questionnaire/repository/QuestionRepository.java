package br.com.galene.api.domain.questionnaire.repository;

import br.com.galene.api.domain.questionnaire.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    @Query("SELECT DISTINCT q FROM QuestionEntity q LEFT JOIN FETCH q.options ORDER BY q.displayOrder")
    List<QuestionEntity> findAllWithOptions();
}
