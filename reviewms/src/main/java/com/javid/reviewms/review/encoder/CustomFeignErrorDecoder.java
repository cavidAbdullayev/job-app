package com.javid.reviewms.review.encoder;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.javid.reviewms.review.exception.CompanyNotFoundException;
import com.javid.reviewms.review.exception.InvalidInputException;
import com.javid.reviewms.review.exception.RemoteServiceException;
import com.javid.reviewms.review.exception.response.RemoteErrorResponse;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public Exception decode(String methodKey, Response response) {
        RemoteErrorResponse errorResponse = null;

        try (InputStream bodyIs = response.body().asInputStream()) {
            errorResponse = objectMapper.readValue(bodyIs, RemoteErrorResponse.class);
        } catch (IOException e) {
            log.error("Failed to parse error response from remote service", e);
        }

        String errorMessage = (errorResponse != null && errorResponse.message() != null)
                ? errorResponse.message()
                : "Error occurred while calling remote service";

        log.error("Error from remote service [{}]: Status {} - {}", methodKey, response.status(), errorMessage);

        return switch (response.status()) {
            case 404 -> new CompanyNotFoundException(errorMessage);
            case 400 -> new InvalidInputException(errorMessage);
            default -> new RemoteServiceException(response.status(), errorMessage);
        };
    }
}
