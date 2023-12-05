package com.stripe.integration.repository;

import com.stripe.integration.entity.CustomerData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends JpaRepository<CustomerData,Long> {

CustomerData findByName(String name);
}
