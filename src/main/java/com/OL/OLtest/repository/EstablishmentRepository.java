package com.OL.OLtest.repository;

import com.OL.OLtest.model.Establishment;
import com.OL.OLtest.model.Merchant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {
    int countByMerchant(Merchant merchant);
    int countByMerchantAndStatus(Merchant merchant, String status);

    @Query("SELECT SUM(e.employeeCount) FROM Establishment e WHERE e.merchant = :merchant")
    int sumEmployeesByMerchant(@Param("merchant") Merchant merchant);
}
