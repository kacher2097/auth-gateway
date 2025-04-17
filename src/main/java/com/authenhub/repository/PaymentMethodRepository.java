package com.authenhub.repository;

import com.authenhub.entity.PaymentMethod;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentMethodRepository extends MongoRepository<PaymentMethod, String> {
    List<PaymentMethod> findByIsActiveTrue();
    Optional<PaymentMethod> findByName(String name);
    List<PaymentMethod> findByProviderType(String providerType);
    boolean existsByName(String name);
}
