package br.com.galene.api.domain.identity.repository;

import br.com.galene.api.domain.identity.entity.ConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsentRepository extends JpaRepository<ConsentEntity, Long> {
}
