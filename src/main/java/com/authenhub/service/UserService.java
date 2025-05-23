package com.authenhub.service;

import com.authenhub.config.DatabaseSwitcherConfig;
import com.authenhub.entity.User;
import com.authenhub.repository.jpa.UserJpaRepository;
import com.authenhub.service.interfaces.IUserService;
import java.sql.Timestamp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final MongoTemplate mongoTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final UserJpaRepository userRepository;
    private final DatabaseSwitcherConfig databaseConfig;

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
     * Count total users
     *
     * @return the count of users
     */
    @Override
    public long countTotalUsers() {
        return userRepository.count();
    }
}
