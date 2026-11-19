package br.com.galene.api.domain.identity.controller;

import br.com.galene.api.common.constants.ApiPaths;
import br.com.galene.api.common.model.ApiResponse;
import br.com.galene.api.common.model.SuccessResult;
import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;
import br.com.galene.api.domain.identity.dto.user.UserResponseDto;
import br.com.galene.api.domain.identity.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Operation(summary = "Cadastra um novo usuário")
    @PostMapping(ApiPaths.AUTH_REGISTER)
    public ResponseEntity<ApiResponse<UserResponseDto>> registerNewUser(@RequestBody @Valid RegisterRequestDto request) {
        var newUserDto = userService.registerUser(request);
        var location = ServletUriComponentsBuilder.fromCurrentContextPath().path(ApiPaths.USERS_ME).build().toUri();

        return ResponseEntity.created(location).body(new SuccessResult<>(HttpStatus.CREATED, UserMessages.USER_CREATED, newUserDto));
    }
}
