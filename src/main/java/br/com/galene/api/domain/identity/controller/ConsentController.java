package br.com.galene.api.domain.identity.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.constants.ConsentMessages;
import br.com.galene.api.domain.identity.dto.consent.ConsentRequestDto;
import br.com.galene.api.domain.identity.dto.consent.ConsentResponseDto;
import br.com.galene.api.domain.identity.enums.TermType;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.identity.service.ConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Consentimentos")
@SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
@RestController
@RequestMapping(ApiPaths.USERS_ME_CONSENTS)
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;

    @Operation(summary = "Lista a resposta mais recente do usuário para cada termo")
    @GetMapping
    public ResponseEntity<ApiResponse<List<ConsentResponseDto>>> getUserConsents(@AuthenticationPrincipal UserSecurity user) {
        var consents = consentService.getUserConsents(user.userId());
        return ResponseEntity.ok(new SuccessResult<>(ConsentMessages.CONSENTS_FOUND, consents));
    }

    @Operation(summary = "Aceita a versão vigente dos termos informados")
    @PostMapping
    public ResponseEntity<ApiResponse<List<ConsentResponseDto>>> acceptConsents(@AuthenticationPrincipal UserSecurity user,
                                                                                @RequestBody @Valid ConsentRequestDto request) {
        var consents = consentService.acceptConsents(user.userId(), request);
        return ResponseEntity.ok(new SuccessResult<>(ConsentMessages.CONSENTS_ACCEPTED, consents));
    }

    @Operation(summary = "Revoga o consentimento de um termo", description = "O histórico do aceite é mantido")
    @DeleteMapping(ApiPaths.CONSENT_TERM_TYPE)
    public ResponseEntity<Void> revokeConsent(@AuthenticationPrincipal UserSecurity user, @PathVariable TermType termType) {
        consentService.revokeConsent(user.userId(), termType);
        return ResponseEntity.noContent().build();
    }
}
