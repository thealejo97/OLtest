package com.OL.OLtest.controller;

import com.OL.OLtest.model.Establishment;
import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.repository.EstablishmentRepository;
import com.OL.OLtest.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/establishments")
public class EstablishmentController {

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private MerchantRepository merchantRepository;

    
    @GetMapping
    public ResponseEntity<Page<Establishment>> getAllEstablishments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Establishment> establishments = establishmentRepository.findAll(pageable);
        return ResponseEntity.ok(establishments);
    }

    
    @PostMapping
    public ResponseEntity<?> createEstablishment(@RequestBody Establishment establishment) {
        if (establishment.getMerchant() == null || establishment.getMerchant().getId() == null) {
            return ResponseEntity.badRequest().body("Merchant is required and must include a valid ID");
        }

        Optional<Merchant> merchantOptional = merchantRepository.findById(establishment.getMerchant().getId());
        if (merchantOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Merchant not found");
        }

        establishment.setMerchant(merchantOptional.get());
        Establishment savedEstablishment = establishmentRepository.save(establishment);
        return ResponseEntity.ok(savedEstablishment);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<?> getEstablishmentById(@PathVariable Long id) {
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(id);
        if (establishmentOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Establishment not found");
        }
        return ResponseEntity.ok(establishmentOptional.get());
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEstablishment(@PathVariable Long id, @RequestBody Establishment updatedEstablishment) {
        Optional<Establishment> establishmentOptional = establishmentRepository.findById(id);
        if (establishmentOptional.isEmpty()) {
            return ResponseEntity.status(404).body("Establishment not found");
        }

        Establishment establishment = establishmentOptional.get();
        establishment.setName(updatedEstablishment.getName());
        establishment.setAddress(updatedEstablishment.getAddress());

        if (updatedEstablishment.getMerchant() != null && updatedEstablishment.getMerchant().getId() != null) {
            Optional<Merchant> merchantOptional = merchantRepository.findById(updatedEstablishment.getMerchant().getId());
            if (merchantOptional.isEmpty()) {
                return ResponseEntity.status(404).body("Merchant not found");
            }
            establishment.setMerchant(merchantOptional.get());
        }

        Establishment savedEstablishment = establishmentRepository.save(establishment);
        return ResponseEntity.ok(savedEstablishment);
    }

    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEstablishment(@PathVariable Long id) {
        if (!establishmentRepository.existsById(id)) {
            return ResponseEntity.status(404).body("Establishment not found");
        }

        establishmentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
