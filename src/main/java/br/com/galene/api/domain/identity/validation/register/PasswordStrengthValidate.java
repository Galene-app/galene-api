package br.com.galene.api.domain.identity.validation.register;

import br.com.galene.api.domain.identity.constants.UserMessages;
import br.com.galene.api.domain.identity.dto.user.RegisterRequestDto;
import br.com.galene.api.domain.identity.exception.WeakPasswordException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Order(2)
@Component
public class PasswordStrengthValidate implements RegisterValidate {

    public static final int MIN_LENGTH = 8;
    public static final int MAX_LENGTH = 64;

    private static final char FIRST_VISIBLE_ASCII = '!';
    private static final char LAST_VISIBLE_ASCII = '~';

    public void validate(RegisterRequestDto request) {
        if (!isStrong(request.password()))
            throw new WeakPasswordException(UserMessages.WEAK_PASSWORD);
    }

    public static boolean isStrong(String password) {
        if (password == null || password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) return false;

        boolean upper = false, lower = false, digit = false, symbol = false;
        for (char character : password.toCharArray()) {
            if (character < FIRST_VISIBLE_ASCII || character > LAST_VISIBLE_ASCII) return false;

            if (Character.isUpperCase(character)) upper = true;
            else if (Character.isLowerCase(character)) lower = true;
            else if (Character.isDigit(character)) digit = true;
            else symbol = true;
        }
        return upper && lower && digit && symbol;
    }
}
