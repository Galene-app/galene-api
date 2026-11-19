package br.com.galene.api.domain.email.service;

import br.com.galene.api.domain.email.dto.event.EmailRequestedEvent;
import br.com.galene.api.domain.email.exception.EmailFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

/**
 * Envia o e-mail só depois do commit (nenhum e-mail sai se a transação falhar) e fora da thread
 * da requisição (o tempo de resposta não revela se o e-mail existe).
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class EmailListenerService {

    private final MailerooService mailerooService;

    @Async
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void onEmailRequested(EmailRequestedEvent event) {
        try {
            mailerooService.sendEmail(event.email());
        } catch (EmailFailedException ex) {
            log.warn("E-mail {} não enviado; o usuário pode solicitar o reenvio", event.email().type());
        }
    }
}
