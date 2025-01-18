package com.OL.OLtest.repository;

import com.OL.OLtest.model.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
    Merchant findByEmail(String email);
}
