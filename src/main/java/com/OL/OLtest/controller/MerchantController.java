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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    // Obtener comerciantes con filtros y paginación
    @GetMapping
    public ResponseEntity<Page<Merchant>> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String registrationDate
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Merchant> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("businessName")), "%" + name.toLowerCase() + "%")
            );
        }

        if (municipality != null && !municipality.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("city").get("name"), municipality)
            );
        }

        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status)
            );
        }

        if (registrationDate != null && !registrationDate.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("createdAt"), LocalDate.parse(registrationDate))
            );
        }

        Page<Merchant> merchants = merchantRepository.findAll(spec, pageable);
        return ResponseEntity.ok(merchants);
    }

    // Crear un nuevo comerciante
    @PostMapping
    public ResponseEntity<?> createMerchant(@RequestBody Merchant merchant) {
        try {
            // Validar City y Department
            if (merchant.getCity() == null || merchant.getCity().getId() == null ||
                !cityRepository.existsById(merchant.getCity().getId())) {
                return ResponseEntity.badRequest().body("Invalid City ID");
            }

            if (merchant.getDepartment() == null || merchant.getDepartment().getId() == null ||
                !departmentRepository.existsById(merchant.getDepartment().getId())) {
                return ResponseEntity.badRequest().body("Invalid Department ID");
            }

            // Guardar el Merchant
            Merchant savedMerchant = merchantRepository.save(merchant);
            return ResponseEntity.ok(savedMerchant);

        } catch (Exception e) {
            return ResponseEntity.status(400).body("Error creating merchant: " + e.getMessage());
        }
    }
    // Actualizar el estado de un comerciante
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateMerchantStatus(@PathVariable Long id, @RequestParam String status) {
        if (!status.equalsIgnoreCase("Active") && !status.equalsIgnoreCase("Inactive")) {
            return ResponseEntity.badRequest().body("Invalid status. Allowed values are 'Active' or 'Inactive'.");
        }

        Optional<Merchant> merchantOptional = merchantRepository.findById(id);
        if (merchantOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Merchant not found");
        }

        Merchant merchant = merchantOptional.get();
        merchant.setStatus(status);
        Merchant updatedMerchant = merchantRepository.save(merchant);

        return ResponseEntity.ok(updatedMerchant);
    }

    // Actualizar un comerciante completo
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

    // Eliminar un comerciante
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        if (merchantRepository.existsById(id)) {
            merchantRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
