package br.com.galene.api.domain.identity.validation.register;

import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;

public interface RegisterValidate {
    void validate(RegisterRequestDto request);
}
