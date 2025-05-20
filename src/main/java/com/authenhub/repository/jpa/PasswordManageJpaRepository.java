package com.authenhub.repository.jpa;

import com.authenhub.entity.PasswordManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordManageJpaRepository extends JpaRepository<PasswordManage, Long> {
    List<PasswordManage> findBySiteUrlContainingIgnoreCase(String siteUrl);
    List<PasswordManage> findByUsernameContainingIgnoreCase(String username);
    Optional<PasswordManage> findBySiteUrlAndUsername(String siteUrl, String username);
    List<PasswordManage> findBySiteUrlContainingIgnoreCaseOrUsernameContainingIgnoreCase(String siteUrl, String username);
}
