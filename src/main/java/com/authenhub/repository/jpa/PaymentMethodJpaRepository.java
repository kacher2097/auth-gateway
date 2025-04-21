package com.authenhub.repository.jpa;

import com.authenhub.entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodJpaRepository extends JpaRepository<PaymentMethod, Long> {
    List<PaymentMethod> findByIsActiveTrue();
    Optional<PaymentMethod> findByName(String name);
    List<PaymentMethod> findByProviderType(String providerType);
    boolean existsByName(String name);
}
