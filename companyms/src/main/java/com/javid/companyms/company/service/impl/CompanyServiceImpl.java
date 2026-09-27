package com.javid.companyms.company.service.impl;

import com.javid.companyms.company.dto.CreateCompanyRequest;
import com.javid.companyms.company.dto.GetCompanyResponse;
import com.javid.companyms.company.dto.GetCompanyResponseForReview;
import com.javid.companyms.company.dto.UpdateCompanyRequest;
import com.javid.companyms.company.dto.messaging.ReviewMessage;
import com.javid.companyms.company.entity.Company;
import com.javid.companyms.company.exception.CompanyNotFoundException;
import com.javid.companyms.company.exception.InvalidInputException;
import com.javid.companyms.company.mapper.CompanyMapper;
import com.javid.companyms.company.repository.CompanyRepository;
import com.javid.companyms.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CacheManager cacheManager;
    private final CompanyMapper companyMapper;

    @Override
    @Cacheable(value = "allCompanies")
    @Transactional(readOnly = true)
    public List<GetCompanyResponse> getAllCompanies() {
        log.info("Fetching all companies");
        List<GetCompanyResponse> responseList = companyRepository.findAll()
                .stream()
                .map(companyMapper::mapToResponse)
                .toList();
        log.info("Successfully fetched {} companies", responseList.size());
        return responseList;
    }

    @Override
    @Transactional
    public GetCompanyResponse updateCompany(UpdateCompanyRequest updatedCompany, Long id) {
        if (id == null) {
            log.error("Failed to update company: ID is required");
            throw new InvalidInputException("ID is required!");
        }

        log.info("Updating company with ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Company not found with ID: {}", id);
                    return new CompanyNotFoundException("Company given by ID not found: " + id);
                });

        companyMapper.mapForUpdate(company, updatedCompany);

        evictCacheAfterCommit(id);

        log.info("Successfully updated company with ID: {}", id);
        return companyMapper.mapToResponse(company);
    }

    @Override
    @Transactional
    public GetCompanyResponse createCompany(CreateCompanyRequest companyRequest) {
        if (companyRequest == null) {
            log.error("Failed to create company: Request payload is null");
            throw new InvalidInputException("Company request cannot be null!");
        }

        log.info("Creating a new company");
        Company company = companyMapper.mapCreateRequestToEntity(companyRequest);
        Company savedCompany = companyRepository.save(company);

        evictCacheAfterCommit(null);

        log.info("Successfully created company with ID: {}", savedCompany.getId());
        return companyMapper.mapToResponse(savedCompany);
    }

    @Override
    @Transactional
    public void deleteCompanyById(Long id) {
        if (id == null) {
            log.error("Failed to delete company: ID is required");
            throw new InvalidInputException("ID is required!");
        }

        log.info("Deleting company with ID: {}", id);
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Company not found for deletion with ID: {}", id);
                    return new CompanyNotFoundException("Company given by ID not found: " + id);
                });

        companyRepository.delete(company);
        evictCacheAfterCommit(id);
        log.info("Successfully deleted company with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "companies", key = "#id", unless = "#result == null")
    public GetCompanyResponse getCompanyById(Long id) {
        if (id == null) {
            log.error("Failed to fetch company: ID is required");
            throw new InvalidInputException("ID is required!");
        }

        log.info("Fetching company with ID: {}", id);

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Company not found with ID: {}", id);
                    return new CompanyNotFoundException("Company given by ID not found: " + id);
                });

        log.info("Successfully fetched company with ID: {}", Objects.requireNonNull(company).getId());
        return companyMapper.mapToResponse(company);
    }

    @Override
    @Transactional
    public void updateCompanyRating(ReviewMessage reviewMessage) {
        if (reviewMessage == null) {
            log.error("Failed to update rating: Review message is required");
            throw new InvalidInputException("Review message is required!");
        }

        log.info("Updating rating for company ID: {} with new rating: {}", reviewMessage.companyId(), reviewMessage.averageRating());

        Company company = companyRepository.findById(reviewMessage.companyId())
                .orElseThrow(() -> {
                    log.error("Company not found for rating update with ID: {}", reviewMessage.companyId());
                    return new CompanyNotFoundException("Company not found: " + reviewMessage.companyId());
                });

        company.setRating(reviewMessage.averageRating());

        evictCacheAfterCommit(reviewMessage.companyId());
        log.info("Successfully updated rating for company ID: {}", reviewMessage.companyId());
    }

    @Override
    @Transactional(readOnly = true)
    public GetCompanyResponseForReview getCompanyResponseForReview(Long companyId) {
        if (companyId == null) {
            log.warn("Requested review company response with null ID");
            return null;
        }

        log.info("Fetching company response for review with company ID: {}", companyId);
        return companyRepository.getCompanyResponseForReview(companyId)
                .orElseThrow(() -> {
                    log.error("Company not found for review with ID: {}", companyId);
                    return new CompanyNotFoundException("Company not found: " + companyId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsCompanyForReview(Long companyId) {
        if (companyId == null) {
            log.warn("Checked company existence for review with null ID");
            return false;
        }

        log.debug("Checking existence of company ID: {} for review", companyId);
        return companyRepository.existsById(companyId);
    }

    private void evictCacheAfterCommit(Long companyId) {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            log.info("Transaction committed successfully. Clearing caches for company ID: {}", companyId);
                            clearCaches(companyId);
                        }
                    }
            );
        } else {
            log.info("No active transaction found. Direct cache clearing for company ID: {}", companyId);
            clearCaches(companyId);
        }
    }

    private void clearCaches(Long companyId) {
        if (companyId != null) {
            Cache companiesCache = cacheManager.getCache("companies");
            if (companiesCache != null) {
                companiesCache.evict(companyId);
                log.debug("Evicted 'companies' cache entry for ID: {}", companyId);
            }

            Cache allCompaniesCache = cacheManager.getCache("allCompanies");
            if (allCompaniesCache != null) {
                allCompaniesCache.clear();
                log.debug("Cleared 'allCompanies' cache");
            }
        }
    }
}