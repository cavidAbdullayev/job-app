package com.javid.companyms.company.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("REVIEW-SERVICE")
public interface ReviewClient {
}