package br.com.galene.api.domain.monitoring.service;

import br.com.galene.api.domain.identity.dto.event.AccountDeletedEvent;
import br.com.galene.api.domain.identity.dto.event.ConsentRevokedEvent;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.monitoring.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class MonitoringListenerService {

    public static final int ACCOUNT_DELETION_ORDER = 2;

    private final MeasurementRepository measurementRepository;

    @EventListener
    public void onConsentRevoked(ConsentRevokedEvent event) {
        if (event.termType() != TermType.LOCATION || event.patientId() == null) return;

        int cleared = measurementRepository.clearLocations(event.patientId());
        log.info("Local apagado de {} leitura(s) após o usuário {} revogar LOCATION", cleared, event.userId());
    }

    @Order(ACCOUNT_DELETION_ORDER)
    @EventListener
    public void onAccountDeleted(AccountDeletedEvent event) {
        if (event.patientId() == null) return;

        int deleted = measurementRepository.deleteAllOfPatient(event.patientId());
        log.info("{} leitura(s) e alertas apagados na exclusão da conta {}", deleted, event.userId());
    }
}
