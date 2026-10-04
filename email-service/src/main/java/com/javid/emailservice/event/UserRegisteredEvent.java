package com.javid.emailservice.event;

import java.io.Serializable;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId,
        String email,
        String firstName,
        String confirmationUrl
) implements Serializable {

}