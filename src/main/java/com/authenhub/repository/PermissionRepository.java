package com.authenhub.repository;

import com.authenhub.entity.mongo.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends MongoRepository<Permission, String> {
    Optional<Permission> findByName(String name);
    List<Permission> findByCategory(String category);
    List<Permission> findAllById(String id);
    boolean existsByName(String name);
}
