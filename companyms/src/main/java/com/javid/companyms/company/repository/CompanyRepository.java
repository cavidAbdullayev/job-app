package com.javid.companyms.company.repository;

import com.javid.companyms.company.dto.GetCompanyResponseForReview;
import com.javid.companyms.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("select new com.javid.companyms.company.dto.GetCompanyResponseForReview(c.name) " +
            "from Company c where c.id = :companyId")
    Optional<GetCompanyResponseForReview> getCompanyResponseForReview(@Param("companyId") Long companyId);

}
