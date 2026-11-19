package br.com.galene.api.domain.identity.repository;

import br.com.galene.api.domain.identity.entity.TermEntity;
import br.com.galene.api.domain.identity.enums.TermType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TermRepository extends JpaRepository<TermEntity, Long> {

    Optional<TermEntity> findFirstByTermTypeOrderByPublishedAtDesc(TermType termType);

    @Query("SELECT t FROM TermEntity t WHERE t.publishedAt = "
            + "(SELECT MAX(t2.publishedAt) FROM TermEntity t2 WHERE t2.termType = t.termType) ORDER BY t.id")
    List<TermEntity> findCurrentTerms();
}
