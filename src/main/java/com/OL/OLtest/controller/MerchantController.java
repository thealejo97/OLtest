package com.OL.OLtest.controller;

import com.OL.OLtest.model.Merchant;
import com.OL.OLtest.repository.MerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
public class MerchantController {

    @Autowired
    private MerchantRepository merchantRepository;

    @GetMapping
    public ResponseEntity<Page<Merchant>> getAllMerchants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<Merchant> merchants = merchantRepository.findAll(PageRequest.of(page, size));
        return ResponseEntity.ok(merchants);
    }

    @PostMapping
    public Merchant createMerchant(@RequestBody Merchant merchant) {
        return merchantRepository.save(merchant);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Merchant> getMerchantById(@PathVariable Long id) {
        return merchantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchant(@PathVariable Long id) {
        if (merchantRepository.existsById(id)) {
            merchantRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
