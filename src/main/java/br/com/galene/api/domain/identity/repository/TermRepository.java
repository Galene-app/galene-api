package br.com.galene.api.domain.identity.repository;

import br.com.galene.api.domain.identity.entity.TermEntity;
import br.com.galene.api.domain.identity.enums.TermType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermRepository extends JpaRepository<TermEntity, Long> {
    Optional<TermEntity> findFirstByTermTypeOrderByPublishedAtDesc(TermType termType);
}
