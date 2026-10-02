package com.javid.userservice.event;

import lombok.Builder;

import java.io.Serializable;

public record UserRegisteredEvent(
        String eventId,
        String email,
        String firstName,
        String confirmationUrl
) implements Serializable {

}
