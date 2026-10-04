package com.javid.emailservice.controller;


import com.javid.emailservice.service.FailedEventRetryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/email")
@RestController
@RequiredArgsConstructor
public class FailedEventRetry {

    private final FailedEventRetryService failedEventRetryService;

    @PutMapping("/retry/{failedEventId}")
    public void retryFailedEvent(@PathVariable UUID failedEventId){
        failedEventRetryService.retryFailedEvent(failedEventId);
    }
}
