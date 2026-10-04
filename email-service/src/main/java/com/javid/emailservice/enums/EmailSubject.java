package com.javid.emailservice.enums;

import lombok.Getter;

@Getter
public enum EmailSubject {
    REGISTRATION_CONFIRMATION("Registration confirmation");

    EmailSubject(String value) {
        this.value = value;
    }

    private final String value;
}
