package com.authenhub.service.interfaces;

import java.sql.Timestamp;

public interface IUserService {

    /**
     * Count users created between two dates
     *
     * @param start the start date
     * @param end   the end date
     * @return the count of users
     */
    long countUsersByCreatedAtBetween(Timestamp start, Timestamp end);

    /**
     * Count users by active status
     *
     * @param active the active status
     * @return the count of users
     */
    long countUsersByActive(boolean active);

    /**
     * Count total users
     *
     * @return the count of users
     */
    long countTotalUsers();

}
