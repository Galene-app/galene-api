package br.com.galene.api.domain.identity.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.config.shared.SpringDocConfig;
import br.com.galene.api.domain.identity.constants.AuthMessages;
import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.auth.ForgotPasswordRequestDto;
import br.com.galene.api.domain.identity.dto.auth.LoginRequestDto;
import br.com.galene.api.domain.identity.dto.auth.LoginResponseDto;
import br.com.galene.api.domain.identity.dto.auth.RefreshTokenRequestDto;
import br.com.galene.api.domain.identity.dto.auth.TwoFactorVerifyRequestDto;
import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;
import br.com.galene.api.domain.identity.dto.user.UserResponseDto;
import br.com.galene.api.domain.identity.security.UserSecurity;
import br.com.galene.api.domain.identity.service.AuthService;
import br.com.galene.api.domain.identity.service.PasswordService;
import br.com.galene.api.domain.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Tag(name = "Autenticação")
@RestController
@RequestMapping(ApiPaths.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final PasswordService passwordService;

    @Operation(summary = "Cadastra um novo usuário")
    @PostMapping(ApiPaths.AUTH_REGISTER)
    public ResponseEntity<ApiResponse<UserResponseDto>> registerNewUser(@RequestBody @Valid RegisterRequestDto request) {
        var newUserDto = userService.registerUser(request);
        var location = ServletUriComponentsBuilder.fromCurrentContextPath().path(ApiPaths.USERS_ME).build().toUri();

        return ResponseEntity.created(location).body(new SuccessResult<>(HttpStatus.CREATED, UserMessages.USER_CREATED, newUserDto));
    }

    @Operation(summary = "Autentica com e-mail e senha",
            description = "Com 2FA ativo envia o código por e-mail (mfaRequired=true); com 2FA desligado devolve os tokens na hora")
    @PostMapping(ApiPaths.AUTH_LOGIN)
    public ResponseEntity<ApiResponse<LoginResponseDto>> authenticate(@RequestBody @Valid LoginRequestDto request) {
        var response = authService.authenticate(request);
        var message = Boolean.TRUE.equals(response.mfaRequired()) ? AuthMessages.CODE_SENT : AuthMessages.AUTHENTICATED;
        return ResponseEntity.ok(new SuccessResult<>(message, response));
    }

    @Operation(summary = "Valida o código de verificação e emite os tokens")
    @PostMapping(ApiPaths.AUTH_TWO_FACTOR_VERIFY)
    public ResponseEntity<ApiResponse<LoginResponseDto>> verifyTwoFactorCode(@RequestBody @Valid TwoFactorVerifyRequestDto request) {
        var response = authService.verifyTwoFactorCode(request);
        return ResponseEntity.ok(new SuccessResult<>(AuthMessages.AUTHENTICATED, response));
    }

    @Operation(summary = "Renova a sessão (o refresh token anterior deixa de valer)")
    @PostMapping(ApiPaths.AUTH_REFRESH)
    public ResponseEntity<ApiResponse<LoginResponseDto>> refresh(@RequestBody @Valid RefreshTokenRequestDto request) {
        var response = authService.refresh(request);
        return ResponseEntity.ok(new SuccessResult<>(AuthMessages.SESSION_REFRESHED, response));
    }

    @Operation(summary = "Envia uma senha temporária por e-mail",
            description = "A senha atual deixa de valer. A resposta é a mesma exista ou não o e-mail")
    @PostMapping(ApiPaths.AUTH_PASSWORD_FORGOT)
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@RequestBody @Valid ForgotPasswordRequestDto request) {
        passwordService.sendTemporaryPassword(request);
        return ResponseEntity.ok(new SuccessResult<>(AuthMessages.TEMPORARY_PASSWORD_REQUESTED, null));
    }

    @Operation(summary = "Encerra a sessão do token usado")
    @SecurityRequirement(name = SpringDocConfig.BEARER_KEY)
    @PostMapping(ApiPaths.AUTH_LOGOUT_PATH)
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserSecurity user) {
        authService.logout(user);
        return ResponseEntity.ok(new SuccessResult<>(AuthMessages.LOGGED_OUT, null));
    }
}
