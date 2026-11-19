package br.com.galene.api.domain.questionnaire.service;

import br.com.galene.api.domain.identity.dto.event.AccountDeletedEvent;
import br.com.galene.api.domain.identity.dto.event.ConsentRevokedEvent;
import br.com.galene.api.domain.questionnaire.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class QuestionnaireListenerService {

    private final AnswerRepository answerRepository;

    @EventListener
    public void onConsentRevoked(ConsentRevokedEvent event) {
        if (event.patientId() == null) return;

        int deleted = answerRepository.deleteRequiringConsent(event.patientId(), event.termType());
        if (deleted > 0) log.info("{} resposta(s) apagada(s) após o usuário {} revogar {}", deleted, event.userId(), event.termType());
    }

    @EventListener
    public void onAccountDeleted(AccountDeletedEvent event) {
        if (event.patientId() == null) return;

        int deleted = answerRepository.deleteAllOfPatient(event.patientId());
        log.info("{} resposta(s) apagada(s) na exclusão da conta {}", deleted, event.userId());
    }
}
