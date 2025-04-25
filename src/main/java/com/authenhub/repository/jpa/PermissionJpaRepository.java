package com.authenhub.repository.jpa;

import com.authenhub.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionJpaRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    List<Permission> findByCategory(String category);
    List<Permission> findAllByIdIn(List<Long> ids);
    boolean existsByName(String name);
}
