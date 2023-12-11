package com.stripe.integration.repository;
import com.stripe.integration.entity.PriceData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepo extends JpaRepository<PriceData,Long> {
    PriceData findById(String id);
}
