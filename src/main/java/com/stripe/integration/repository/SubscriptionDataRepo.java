package com.stripe.integration.repository;

import com.stripe.integration.entity.SubscriptionData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionDataRepo extends JpaRepository<SubscriptionData,Long> {

}
