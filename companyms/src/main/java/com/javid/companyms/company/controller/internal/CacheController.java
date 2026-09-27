package com.javid.companyms.company.controller.internal;

import com.javid.companyms.company.service.impl.CacheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {
    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @DeleteMapping("/company")
    public ResponseEntity<String> clearCompanyCaches() {
        cacheService.clearCompanyCaches();
        return ResponseEntity.ok("Şirkət keşləri uğurla təmizləndi.");
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> clearAllCaches() {
        cacheService.clearAllCaches();
        return ResponseEntity.ok("Bütün keşlər uğurla təmizləndi.");
    }
}
