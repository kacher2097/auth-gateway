package com.authenhub.repository;

import com.authenhub.entity.PasswordManage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PasswordManageRepository extends JpaRepository<PasswordManage, Long>, JpaSpecificationExecutor<PasswordManage> {
    /**
     * Find password entries by site URL (case-insensitive, partial match)
     *
     * @param siteUrl the site URL to search for
     * @return matching password entries
     */
    List<PasswordManage> findBySiteUrlContainingIgnoreCase(String siteUrl);

    /**
     * Find password entries by username (case-insensitive, partial match)
     *
     * @param username the username to search for
     * @return matching password entries
     */
    List<PasswordManage> findByUsernameContainingIgnoreCase(String username);

    /**
     * Find a password entry by site URL and username (exact match)
     *
     * @param siteUrl the site URL
     * @param username the username
     * @return the password entry if found
     */
    Optional<PasswordManage> findBySiteUrlAndUsername(String siteUrl, String username);

    /**
     * Find password entries by site URL or username (case-insensitive, partial match)
     *
     * @param keyword the keyword to search for in site URL or username
     * @return matching password entries
     */
    @Query("SELECT p FROM PasswordManage p WHERE LOWER(p.siteUrl) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PasswordManage> findByKeyword(@Param("keyword") String keyword);
}
