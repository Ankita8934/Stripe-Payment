package com.stripe.integration.repository;

import com.stripe.integration.entity.Numbers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NumberRep extends JpaRepository<Numbers,Long> {
}
