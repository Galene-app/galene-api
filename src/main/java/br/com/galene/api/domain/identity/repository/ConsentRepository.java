package br.com.galene.api.domain.identity.repository;

import br.com.galene.api.domain.identity.entity.ConsentEntity;
import br.com.galene.api.domain.identity.enums.TermType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository extends JpaRepository<ConsentEntity, Long> {

    List<ConsentEntity> findByUserIdOrderByAnsweredAtDescIdDesc(Long userId);

    Optional<ConsentEntity> findFirstByUserIdAndTermTypeOrderByAnsweredAtDescIdDesc(Long userId, TermType termType);
}
