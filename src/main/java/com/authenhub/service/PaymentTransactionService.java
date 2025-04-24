package com.authenhub.service;

import com.authenhub.bean.payment.PaymentTransactionDTO;
import com.authenhub.utils.MongoAggregationUtils;
import com.authenhub.utils.MongoQueryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

import com.authenhub.utils.TimestampUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentTransactionService {

    private static final String COLLECTION_PAYMENT = "payment_transactions";

    private final MongoTemplate mongoTemplate;

    /**
     * Find transactions by various criteria
     */
    public List<PaymentTransactionDTO> findTransactions(
            String userId,
            Long amount,
            String status,
            Timestamp startDate,
            Timestamp endDate) {

        // Build criteria using MongoQueryUtils
        Criteria userCriteria = MongoQueryUtils.hasValue("userId", userId);
        Criteria amountCriteria = MongoQueryUtils.hasValue("amount", amount);
        Criteria statusCriteria = MongoQueryUtils.hasValue("status", status);
        Criteria dateCriteria = MongoQueryUtils.dateRange("createdAt", startDate, endDate);

        // Build query with AND operator
        Query query = MongoQueryUtils.buildAndQuery(
                userCriteria,
                amountCriteria,
                statusCriteria,
                dateCriteria
        );

        // Execute query
        return mongoTemplate.find(query, PaymentTransactionDTO.class, COLLECTION_PAYMENT);
    }

    /**
     * Get transaction statistics by status
     */
    public List<PaymentTransactionDTO> getTransactionStatsByStatus(Timestamp startDate, Timestamp endDate) {
        // Build criteria
        Criteria dateCriteria = MongoQueryUtils.dateRange("createdAt", startDate, endDate);

        // Create group operation
        GroupOperation groupOperation = MongoAggregationUtils.groupAndSum("status", "amount", "totalAmount");

        // Execute aggregation
        AggregationResults<PaymentTransactionDTO> results = MongoAggregationUtils.executeMatchAndGroup(
                mongoTemplate,
                dateCriteria,
                groupOperation,
                COLLECTION_PAYMENT,
                PaymentTransactionDTO.class
        );

        return results.getMappedResults();
    }

    /**
     * Get paginated transaction statistics by user
     */
    public List<PaymentTransactionDTO> getPaginatedTransactionStatsByUser(
            Timestamp startDate,
            Timestamp endDate,
            int page,
            int size) {

        // Build criteria
        Criteria dateCriteria = MongoQueryUtils.dateRange("createdAt", startDate, endDate);

        // Create group operation
        GroupOperation groupOperation = MongoAggregationUtils.groupAndSum("userId", "amount", "totalAmount");

        // Create sort
        Sort sort = Sort.by(Sort.Direction.DESC, "totalAmount");

        // Calculate skip
        int skip = page * size;

        // Execute paginated aggregation
        AggregationResults<PaymentTransactionDTO> results = MongoAggregationUtils.executePaginatedAggregation(
                mongoTemplate,
                dateCriteria,
                groupOperation,
                sort,
                skip,
                size,
                COLLECTION_PAYMENT,
                PaymentTransactionDTO.class
        );

        return results.getMappedResults();
    }
}
