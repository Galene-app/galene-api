package br.com.galene.api.domain.privacy.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.privacy.constants.PrivacyMessages;
import br.com.galene.api.domain.privacy.dto.UserDataExportDto;
import br.com.galene.api.domain.privacy.service.DataExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Perfil")
@SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
@RestController
@RequiredArgsConstructor
public class DataExportController {

    private final DataExportService dataExportService;

    @Operation(summary = "Exporta todos os dados do usuário em JSON (LGPD)",
            description = "Perfil, consentimentos, respostas do questionário, perfil sonoro, leituras de ruído e notificações")
    @GetMapping(ApiPaths.USERS_ME_EXPORT)
    public ResponseEntity<ApiResponse<UserDataExportDto>> exportUserData(@AuthenticationPrincipal UserSecurity user) {
        var export = dataExportService.exportUserData(user.userId());
        return ResponseEntity.ok(new SuccessResult<>(PrivacyMessages.DATA_EXPORTED, export));
    }
}
