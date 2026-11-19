package br.com.galene.api.domain.identity.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.constants.ConsentMessages;
import br.com.galene.api.domain.identity.dto.consent.PermissionsDto;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.identity.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Consentimentos")
@SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
@RestController
@RequestMapping(ApiPaths.USERS_ME_PERMISSIONS)
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "Registra as permissões concedidas no aparelho",
            description = "Microfone é obrigatório; Bluetooth e localização são opcionais. Cada permissão vira o consentimento correspondente")
    @PutMapping
    public ResponseEntity<ApiResponse<PermissionsDto>> updatePermissions(@AuthenticationPrincipal UserSecurity user,
                                                                         @RequestBody @Valid PermissionsDto request) {
        var permissions = permissionService.updatePermissions(user.userId(), request);
        return ResponseEntity.ok(new SuccessResult<>(ConsentMessages.PERMISSIONS_UPDATED, permissions));
    }
}
