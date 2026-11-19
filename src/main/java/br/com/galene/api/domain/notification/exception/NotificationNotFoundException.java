package br.com.galene.api.domain.notification.exception;

import br.com.galene.api.common.constants.ErrorCodes;
import br.com.galene.api.common.exception.custom.ApiException;
import org.springframework.http.HttpStatus;

public class NotificationNotFoundException extends ApiException {
    public NotificationNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, ErrorCodes.NOTIFICATION_NOT_FOUND, message);
    }
}
