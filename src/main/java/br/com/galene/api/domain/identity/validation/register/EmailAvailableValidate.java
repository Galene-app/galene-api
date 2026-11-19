package br.com.galene.api.domain.identity.validation.register;

import br.com.galene.api.common.helpers.EmailHelper;
import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;
import br.com.galene.api.domain.identity.exception.EmailAlreadyRegisteredException;
import br.com.galene.api.domain.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component
@RequiredArgsConstructor
public class EmailAvailableValidate implements RegisterValidate {

    private final UserRepository userRepository;

    public void validate(RegisterRequestDto request) {
        if (userRepository.existsByEmail(EmailHelper.normalize(request.email())))
            throw new EmailAlreadyRegisteredException(UserMessages.EMAIL_ALREADY_REGISTERED);
    }
}
