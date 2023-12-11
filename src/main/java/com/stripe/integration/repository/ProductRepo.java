package com.stripe.integration.repository;
import com.stripe.integration.entity.ProductData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepo extends JpaRepository<ProductData, Long> {

}
