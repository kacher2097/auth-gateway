package com.authenhub.service;

import com.authenhub.entity.User;
import com.authenhub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Count users created between two dates
     *
     * @param start the start date
     * @param end the end date
     * @return the count of users
     */
    public long countUsersByCreatedAtBetween(Timestamp start, Timestamp end) {
        Query query = new Query();
        
        if (start != null && end != null) {
            query.addCriteria(Criteria.where("createdAt").gte(start).lte(end));
        } else if (start != null) {
            query.addCriteria(Criteria.where("createdAt").gte(start));
        } else if (end != null) {
            query.addCriteria(Criteria.where("createdAt").lte(end));
        }
        
        return mongoTemplate.count(query, User.class);
    }
    
    /**
     * Find users created between two dates
     *
     * @param start the start date
     * @param end the end date
     * @return the list of users
     */
    public List<User> findUsersByCreatedAtBetween(Timestamp start, Timestamp end) {
        return userRepository.findByCreatedAtBetween(start, end);
    }
    
    /**
     * Count users by active status
     *
     * @param active the active status
     * @return the count of users
     */
    public long countUsersByActive(boolean active) {
        return userRepository.countByActive(active);
    }
    
    /**
     * Count users by role
     *
     * @param role the role
     * @return the count of users
     */
    public long countUsersByRole(User.Role role) {
        return userRepository.countByRole(role);
    }
    
    /**
     * Count total users
     *
     * @return the count of users
     */
    public long countTotalUsers() {
        return userRepository.count();
    }
    
    /**
     * Count users with social login
     *
     * @return the count of users
     */
    public long countUsersBySocialLogin() {
        return userRepository.countBySocialLogin();
    }
}
