package com.javid.userservice.util;


import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSourceUtils {
    private final MessageSource messageSource;

    public String getLocalizedMessage(String messageKey, Object... args) {
        return messageSource.getMessage(
                messageKey,
                args,
                LocaleContextHolder.getLocale()
        );
    }
}
