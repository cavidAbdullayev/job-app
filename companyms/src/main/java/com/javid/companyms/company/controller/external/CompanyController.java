package com.javid.companyms.company.controller.external;

import com.javid.companyms.company.dto.CreateCompanyRequest;
import com.javid.companyms.company.dto.GetCompanyResponse;
import com.javid.companyms.company.dto.UpdateCompanyRequest;
import com.javid.companyms.company.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/external/companies")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<GetCompanyResponse>> getAllCompanies() {
        return new ResponseEntity<>(companyService.getAllCompanies(), HttpStatus.OK);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            value = "/{id}"
    )
    public ResponseEntity<GetCompanyResponse> updateCompany(@RequestBody @Valid UpdateCompanyRequest updatedCompany,
                                                            @PathVariable("id") Long id) {
        return ResponseEntity.ok(companyService.updateCompany(updatedCompany, id));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<GetCompanyResponse> createCompany(@RequestBody @Valid CreateCompanyRequest companyRequest) {
        return ResponseEntity.ok(companyService.createCompany(companyRequest));
    }

    @RequestMapping(
            method = RequestMethod.DELETE,
            value = "/{id}"
    )
    public ResponseEntity<Void> deleteCompanyById(@PathVariable("id") Long id) {
        companyService.deleteCompanyById(id);
        return ResponseEntity.noContent().build();
    }

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/{id}"
    )
    public ResponseEntity<GetCompanyResponse> getCompanyById(@PathVariable("id") Long id) {
        GetCompanyResponse companyResponse = companyService.getCompanyById(id);
        if (companyResponse != null)
            return new ResponseEntity<>(companyResponse, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}