package br.com.galene.api.domain.reporting.service;

import br.com.galene.api.domain.identity.dto.event.AccountDeletedEvent;
import br.com.galene.api.domain.reporting.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ReportListenerService {

    private final ReportRepository reportRepository;

    @EventListener
    public void onAccountDeleted(AccountDeletedEvent event) {
        if (event.patientId() == null) return;

        int deleted = reportRepository.deleteAllOfPatient(event.patientId());
        log.info("{} relatório(s) apagado(s) na exclusão da conta {}", deleted, event.userId());
    }
}
