package com.authenhub.service;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.User;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final MongoTemplate mongoTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final UserJpaRepository userRepository;
    private final DatabaseSwitcherConfig databaseConfig;

    /**
     * Count users created between two dates
     *
     * @param start the start date
     * @param end the end date
     * @return the count of users
     */
    @Override
    public long countUsersByCreatedAtBetween(Timestamp start, Timestamp end) {
        if (databaseConfig.isMongoActive()) {
            Query query = new Query();

            if (start != null && end != null) {
                query.addCriteria(Criteria.where("createdAt").gte(start).lte(end));
            } else if (start != null) {
                query.addCriteria(Criteria.where("createdAt").gte(start));
            } else if (end != null) {
                query.addCriteria(Criteria.where("createdAt").lte(end));
            }

            return mongoTemplate.count(query, User.class);
        } else {
            StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE 1=1");

            if (start != null && end != null) {
                sql.append(" AND created_at >= ? AND created_at <= ?");
                return jdbcTemplate.queryForObject(sql.toString(), Long.class, start, end);
            } else if (start != null) {
                sql.append(" AND created_at >= ?");
                return jdbcTemplate.queryForObject(sql.toString(), Long.class, start);
            } else if (end != null) {
                sql.append(" AND created_at <= ?");
                return jdbcTemplate.queryForObject(sql.toString(), Long.class, end);
            } else {
                return jdbcTemplate.queryForObject(sql.toString(), Long.class);
            }
        }
    }

    /**
     * Find users created between two dates
     *
     * @param start the start date
     * @param end the end date
     * @return the list of users
     */
    @Override
    public List<User> findUsersByCreatedAtBetween(Timestamp start, Timestamp end) {
        return userRepository.findByCreatedAtBetween(start, end);
    }

    /**
     * Count users by active status
     *
     * @param active the active status
     * @return the count of users
     */
    @Override
    public long countUsersByActive(boolean active) {
        if (databaseConfig.isMongoActive()) {
            return userRepository.countByActive(active);
        } else {
            String sql = "SELECT COUNT(*) FROM users WHERE active = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, active);
        }
    }

    /**
     * Count users by role
     *
     * @param role the role
     * @return the count of users
     */
    @Override
    public long countUsersByRole(String role) {
        if (databaseConfig.isMongoActive()) {
            return userRepository.countByRole(role);
        } else {
            String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
            return jdbcTemplate.queryForObject(sql, Long.class, role);
        }
    }

    /**
     * Count total users
     *
     * @return the count of users
     */
    @Override
    public long countTotalUsers() {
        return userRepository.count();
    }

    /**
     * Count users with social login
     *
     * @return the count of users
     */
    @Override
    public long countUsersBySocialLogin() {
        if (databaseConfig.isMongoActive()) {
            return userRepository.countBySocialLogin();
        } else {
            String sql = "SELECT COUNT(*) FROM users WHERE social_provider IS NOT NULL";
            return jdbcTemplate.queryForObject(sql, Long.class);
        }
    }
}
