package com.OL.OLtest.controller;

import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.model.City;
import com.OL.OLtest.model.Department;
import com.OL.OLtest.repository.MerchantRepository;
import com.OL.OLtest.repository.CityRepository;
import com.OL.OLtest.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    
    @GetMapping
    public ResponseEntity<Page<Merchant>> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Merchant> merchants = merchantRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(merchants);
    }

    
    @PostMapping
    public ResponseEntity<?> createMerchant(@RequestBody Merchant merchant) {
        try {
            
            Optional<City> cityOptional = cityRepository.findById(merchant.getCity().getId());
            if (cityOptional.isEmpty()) {
                return ResponseEntity.status(404).body("City not found");
            }
    
            Optional<Department> departmentOptional = departmentRepository.findById(merchant.getDepartment().getId());
            if (departmentOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Department not found");
            }
    
            
            merchant.setCity(cityOptional.get());
            merchant.setDepartment(departmentOptional.get());
    
            
            Merchant savedMerchant = merchantRepository.save(merchant);
            return ResponseEntity.ok(savedMerchant);
    
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating merchant: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMerchant(@PathVariable Long id, @RequestBody Merchant updatedMerchant) {
        try {
            return merchantRepository.findById(id).map(merchant -> {
                if (updatedMerchant.getCity() != null && updatedMerchant.getCity().getId() != null) {
                    Optional<City> cityOptional = cityRepository.findById(updatedMerchant.getCity().getId());
                    if (cityOptional.isEmpty()) {
                        return ResponseEntity.status(404).body("City not found");
                    }
                    merchant.setCity(cityOptional.get());
                }

                if (updatedMerchant.getDepartment() != null && updatedMerchant.getDepartment().getId() != null) {
                    Optional<Department> departmentOptional = departmentRepository.findById(updatedMerchant.getDepartment().getId());
                    if (departmentOptional.isEmpty()) {
                        return ResponseEntity.status(404).body("Department not found");
                    }
                    merchant.setDepartment(departmentOptional.get());
                }

                merchant.setBusinessName(updatedMerchant.getBusinessName());
                merchant.setPhone(updatedMerchant.getPhone());
                merchant.setEmail(updatedMerchant.getEmail());
                merchant.setStatus(updatedMerchant.getStatus());
                Merchant savedMerchant = merchantRepository.save(merchant);
                return ResponseEntity.ok(savedMerchant);
            }).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error updating merchant: " + e.getMessage());
        }
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        if (merchantRepository.existsById(id)) {
            merchantRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
