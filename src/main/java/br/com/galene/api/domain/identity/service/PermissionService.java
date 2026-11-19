package br.com.galene.api.domain.identity.service;

import br.com.galene.api.domain.identity.constants.ConsentMessages;
import br.com.galene.api.domain.identity.dto.consent.ConsentRequestDto;
import br.com.galene.api.domain.identity.dto.consent.PermissionsDto;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.identity.exception.MicrophoneRequiredException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final ConsentService consentService;

    @Transactional
    public PermissionsDto updatePermissions(Long userId, PermissionsDto request) {
        if (!request.microphone())
            throw new MicrophoneRequiredException(ConsentMessages.MICROPHONE_REQUIRED);

        var permissions = new LinkedHashMap<TermType, Boolean>();
        permissions.put(TermType.MICROPHONE, request.microphone());
        permissions.put(TermType.BLUETOOTH, request.bluetooth());
        permissions.put(TermType.LOCATION, request.location());

        var granted = permissions.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
        consentService.acceptConsents(userId, new ConsentRequestDto(granted));
        permissions.forEach((termType, allowed) -> {
            if (!allowed) consentService.revokeConsent(userId, termType);
        });

        return request;
    }
}
