package br.com.galene.api.domain.identity.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.user.ChangePasswordRequestDto;
import br.com.galene.api.domain.identity.dto.user.UpdateUserDto;
import br.com.galene.api.domain.identity.dto.user.UserResponseDto;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.identity.service.PasswordService;
import br.com.galene.api.domain.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Perfil")
@SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
@RestController
@RequestMapping(ApiPaths.USERS_ME)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordService passwordService;

    @Operation(summary = "Consulta o perfil do usuário autenticado")
    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserDetails(@AuthenticationPrincipal UserSecurity user) {
        var userDto = userService.getUserDetails(user.userId());
        return ResponseEntity.ok().body(new SuccessResult<>(UserMessages.USER_FOUND, userDto));
    }

    @Operation(summary = "Atualiza o perfil do usuário autenticado")
    @PutMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@AuthenticationPrincipal UserSecurity user,
                                                                   @RequestBody @Valid UpdateUserDto request) {
        var response = userService.updateUser(user.userId(), request);
        return ResponseEntity.ok().body(new SuccessResult<>(UserMessages.USER_UPDATED, response));
    }

    @Operation(summary = "Altera a senha do usuário autenticado",
            description = "Recebe a senha atual e a nova. As outras sessões do usuário são encerradas")
    @PutMapping(ApiPaths.USERS_ME_PASSWORD)
    public ResponseEntity<ApiResponse<Void>> changePassword(@AuthenticationPrincipal UserSecurity user,
                                                            @RequestBody @Valid ChangePasswordRequestDto request) {
        passwordService.changePassword(user, request);
        return ResponseEntity.ok().body(new SuccessResult<>(UserMessages.PASSWORD_CHANGED, null));
    }

    @Operation(summary = "Exclui a conta do usuário autenticado")
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserSecurity user) {
        userService.deleteUser(user.userId());
        return ResponseEntity.noContent().build();
    }
}
