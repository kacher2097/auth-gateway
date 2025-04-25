package com.authenhub.service.interfaces;

import com.authenhub.entity.User;

import java.sql.Timestamp;
import java.util.List;

/**
 * Interface for user service operations
 */
public interface IUserService {
    
    /**
     * Count users created between two dates
     *
     * @param start the start date
     * @param end the end date
     * @return the count of users
     */
    long countUsersByCreatedAtBetween(Timestamp start, Timestamp end);
    
    /**
     * Find users created between two dates
     *
     * @param start the start date
     * @param end the end date
     * @return the list of users
     */
    List<User> findUsersByCreatedAtBetween(Timestamp start, Timestamp end);
    
    /**
     * Count users by active status
     *
     * @param active the active status
     * @return the count of users
     */
    long countUsersByActive(boolean active);
    
    /**
     * Count users by role
     *
     * @param role the role
     * @return the count of users
     */
    long countUsersByRole(String role);
    
    /**
     * Count total users
     *
     * @return the count of users
     */
    long countTotalUsers();
    
    /**
     * Count users with social login
     *
     * @return the count of users
     */
    long countUsersBySocialLogin();
}
