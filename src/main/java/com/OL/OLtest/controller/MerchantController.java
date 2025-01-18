package com.OL.OLtest.controller;

import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantRepository merchantRepository;

    // Obtener todos los comerciantes
    @GetMapping
    public List<Merchant> getAllMerchants() {
        return merchantRepository.findAll();
    }

    // Crear un nuevo comerciante
    @PostMapping
    public Merchant createMerchant(@RequestBody Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    // Obtener un comerciante por ID
    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Actualizar un comerciante por ID
    @PutMapping("/{id}")
    public ResponseEntity<Merchant> updateMerchant(@PathVariable Long id, @RequestBody Merchant updatedMerchant) {
        return merchantRepository.findById(id).map(merchant -> {
            merchant.setBusinessName(updatedMerchant.getBusinessName());
            merchant.setCity(updatedMerchant.getCity());
            merchant.setPhone(updatedMerchant.getPhone());
            merchant.setEmail(updatedMerchant.getEmail());
            merchant.setStatus(updatedMerchant.getStatus());
            return ResponseEntity.ok(merchantRepository.save(merchant));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Eliminar un comerciante por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        if (merchantRepository.existsById(id)) {
            merchantRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
