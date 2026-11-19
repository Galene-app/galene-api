package br.com.galene.api.domain.identity.validation.register;

import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;
import br.com.galene.api.domain.identity.exception.UnderageUserException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;

@Order(1)
@Component
@RequiredArgsConstructor
public class UserAgeValidate implements RegisterValidate {

    public static final int ADULT_AGE = 18;

    private final Clock clock;

    public void validate(RegisterRequestDto request) {
        var age = Period.between(request.birthDate(), LocalDate.now(clock)).getYears();
        if (age < ADULT_AGE)
            throw new UnderageUserException(UserMessages.UNDERAGE);
    }
}
