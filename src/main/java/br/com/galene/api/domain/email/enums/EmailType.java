package br.com.galene.api.domain.email.enums;

import lombok.Getter;

@Getter
public enum EmailType {
    TWO_FACTOR("two-factor"),
    PASSWORD_RESET("password-reset");

    private final String tag;

    EmailType(String tag) {
        this.tag = tag;
    }
}
