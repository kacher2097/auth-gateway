package com.authenhub.repository.jpa;

import com.authenhub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    long countByRole(com.authenhub.entity.mongo.User.Role role);
    long countByActive(boolean active);

    List<User> findByCreatedAtBetween(Timestamp start, Timestamp end);

    @Query("SELECT COUNT(u) FROM User u WHERE u.socialProvider IS NOT NULL")
    long countBySocialLogin();
}
