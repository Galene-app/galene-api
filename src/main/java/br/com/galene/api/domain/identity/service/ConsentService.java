package br.com.galene.api.domain.identity.service;

import br.com.galene.api.domain.identity.constants.ConsentMessages;
import br.com.galene.api.domain.identity.dto.consent.ConsentRequestDto;
import br.com.galene.api.domain.identity.dto.consent.ConsentResponseDto;
import br.com.galene.api.domain.identity.dto.consent.TermResponseDto;
import br.com.galene.api.domain.identity.dto.event.ConsentRevokedEvent;
import br.com.galene.api.domain.identity.entity.ConsentEntity;
import br.com.galene.api.domain.identity.entity.PatientEntity;
import br.com.galene.api.domain.identity.entity.TermEntity;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.identity.exception.ConsentNotRevocableException;
import br.com.galene.api.domain.identity.exception.ConsentRequiredException;
import br.com.galene.api.domain.identity.exception.TermNotFoundException;
import br.com.galene.api.domain.identity.repository.ConsentRepository;
import br.com.galene.api.domain.identity.repository.PatientRepository;
import br.com.galene.api.domain.identity.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;
    private final TermRepository termRepository;
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional(readOnly = true)
    public List<TermResponseDto> getCurrentTerms() {
        return termRepository.findCurrentTerms().stream().map(TermResponseDto::new).toList();
    }

    @Transactional(readOnly = true)
    public List<ConsentResponseDto> getUserConsents(Long userId) {
        userService.getUserById(userId);
        return toResponse(latestAnswers(userId));
    }

    @Transactional
    public List<ConsentResponseDto> acceptConsents(Long userId, ConsentRequestDto request) {
        var user = userService.getUserById(userId);
        var patient = findPatient(userId);
        var latest = latestAnswers(userId);
        var now = LocalDateTime.now(clock);

        for (var termType : request.termTypes().stream().distinct().toList()) {
            var term = getCurrentTerm(termType);
            if (isAcceptedAtVersion(latest.get(termType), term)) continue;

            var consent = consentRepository.save(new ConsentEntity(user, patientFor(termType, patient), term, true, now));
            latest.put(termType, consent);
            log.info("Usuário {} aceitou o termo {} v{}", userId, termType, term.getTermVersion());
        }
        return toResponse(latest);
    }

    @Transactional
    public void revokeConsent(Long userId, TermType termType) {
        if (termType == TermType.ACCOUNT)
            throw new ConsentNotRevocableException(ConsentMessages.CONSENT_NOT_REVOCABLE);

        userService.getUserById(userId);
        var lastAnswer = consentRepository.findFirstByUserIdAndTermTypeOrderByAnsweredAtDescIdDesc(userId, termType);
        if (lastAnswer.isEmpty() || !lastAnswer.get().getAccepted()) return;

        var revocation = consentRepository.save(ConsentEntity.revocationOf(lastAnswer.get(), LocalDateTime.now(clock)));
        if (termType == TermType.HEALTH_DATA) findPatient(userId).setDiagnosis(null);

        var patientId = revocation.getPatient() == null ? null : revocation.getPatient().getId();
        eventPublisher.publishEvent(new ConsentRevokedEvent(userId, patientId, termType));
        log.info("Usuário {} revogou o termo {}", userId, termType);
    }

    @Transactional(readOnly = true)
    public boolean hasConsent(Long userId, TermType termType) {
        return consentRepository.findFirstByUserIdAndTermTypeOrderByAnsweredAtDescIdDesc(userId, termType)
                .map(ConsentEntity::getAccepted)
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public void requireConsent(Long userId, TermType termType) {
        if (!hasConsent(userId, termType))
            throw new ConsentRequiredException(ConsentMessages.CONSENT_REQUIRED.formatted(termType));
    }

    private Map<TermType, ConsentEntity> latestAnswers(Long userId) {
        var latest = new EnumMap<TermType, ConsentEntity>(TermType.class);
        consentRepository.findByUserIdOrderByAnsweredAtDescIdDesc(userId)
                .forEach(consent -> latest.putIfAbsent(consent.getTermType(), consent));
        return latest;
    }

    private static List<ConsentResponseDto> toResponse(Map<TermType, ConsentEntity> latest) {
        return latest.values().stream().map(ConsentResponseDto::new).toList();
    }

    private static boolean isAcceptedAtVersion(ConsentEntity lastAnswer, TermEntity term) {
        return lastAnswer != null && lastAnswer.getAccepted() && lastAnswer.getTermVersion().equals(term.getTermVersion());
    }

    private static PatientEntity patientFor(TermType termType, PatientEntity patient) {
        return termType == TermType.ACCOUNT ? null : patient;
    }

    private TermEntity getCurrentTerm(TermType termType) {
        return termRepository.findFirstByTermTypeOrderByPublishedAtDesc(termType)
                .orElseThrow(() -> new TermNotFoundException(ConsentMessages.TERM_NOT_FOUND + termType));
    }

    private PatientEntity findPatient(Long userId) {
        return patientRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException(ConsentMessages.PATIENT_NOT_FOUND));
    }
}
