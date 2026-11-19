package br.com.galene.api.domain.email.repository;

import br.com.galene.api.domain.email.entity.EmailTemplateEntity;
import br.com.galene.api.domain.email.enums.EmailType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplateEntity, Long> {
    Optional<EmailTemplateEntity> findByTemplateKey(EmailType templateKey);
}
