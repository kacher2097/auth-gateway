package com.authenhub.utils;

import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class MongoAggregationUtils {

    /**
     * Executes an aggregation pipeline with match and group operations
     *
     * @param mongoTemplate  the MongoTemplate
     * @param matchCriteria  the match criteria
     * @param collectionName the collection name
     * @param outputClass    the output class
     * @param <T>            the type of the output class
     * @return the aggregation results
     */
    public static <T> AggregationResults<T> executeMatchAndGroup(
            MongoTemplate mongoTemplate,
            Criteria matchCriteria,
            GroupOperation groupOperation,
            String collectionName,
            Class<T> outputClass) {

        MatchOperation matchOperation = Aggregation.match(matchCriteria);
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation);

        return mongoTemplate.aggregate(aggregation, collectionName, outputClass);
    }

    /**
     * Executes an aggregation pipeline with match, group, and sort operations
     *
     * @param mongoTemplate  the MongoTemplate
     * @param matchCriteria  the match criteria
     * @param collectionName the collection name
     * @param outputClass    the output class
     * @param <T>            the type of the output class
     * @return the aggregation results
     */
    public static <T> AggregationResults<T> executeMatchGroupAndSort(
            MongoTemplate mongoTemplate,
            Criteria matchCriteria,
            GroupOperation groupOperation,
            Sort sort,
            String collectionName,
            Class<T> outputClass) {

        MatchOperation matchOperation = Aggregation.match(matchCriteria);
        SortOperation sortOperation = Aggregation.sort(sort);

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                groupOperation,
                sortOperation
        );

        return mongoTemplate.aggregate(aggregation, collectionName, outputClass);
    }

    /**
     * Executes an aggregation pipeline with match, group, sort, skip, and limit operations
     *
     * @param mongoTemplate  the MongoTemplate
     * @param matchCriteria  the match criteria
     * @param skip           the number of documents to skip
     * @param limit          the maximum number of documents to return
     * @param collectionName the collection name
     * @param outputClass    the output class
     * @param <T>            the type of the output class
     * @return the aggregation results
     */
    public static <T> AggregationResults<T> executePaginatedAggregation(
            MongoTemplate mongoTemplate,
            Criteria matchCriteria,
            GroupOperation groupOperation,
            Sort sort,
            int skip,
            int limit,
            String collectionName,
            Class<T> outputClass) {

        List<AggregationOperation> operations = new ArrayList<>();

        if (matchCriteria != null) {
            operations.add(Aggregation.match(matchCriteria));
        }

        if (groupOperation != null) {
            operations.add(groupOperation);
        }

        if (sort != null) {
            operations.add(Aggregation.sort(sort));
        }

        if (skip > 0) {
            operations.add(Aggregation.skip((long) skip));
        }

        if (limit > 0) {
            operations.add(Aggregation.limit(limit));
        }

        Aggregation aggregation = Aggregation.newAggregation(operations);

        return mongoTemplate.aggregate(aggregation, collectionName, outputClass);
    }

    /**
     * Creates a group operation for counting documents
     *
     * @param groupByField the field to group by
     * @return the group operation
     */
    public static GroupOperation groupAndCount(String groupByField) {
        return Aggregation.group(groupByField).count().as("count");
    }

    /**
     * Creates a group operation for summing a field
     *
     * @param groupByField the field to group by
     * @param sumField     the field to sum
     * @param resultField  the name of the result field
     * @return the group operation
     */
    public static GroupOperation groupAndSum(String groupByField, String sumField, String resultField) {
        return Aggregation.group(groupByField).sum(sumField).as(resultField);
    }

    /**
     * Creates a group operation for calculating average of a field
     *
     * @param groupByField the field to group by
     * @param avgField     the field to average
     * @param resultField  the name of the result field
     * @return the group operation
     */
    public static GroupOperation groupAndAvg(String groupByField, String avgField, String resultField) {
        return Aggregation.group(groupByField).avg(avgField).as(resultField);
    }

    /**
     * Creates a group operation for finding minimum value of a field
     *
     * @param groupByField the field to group by
     * @param minField     the field to find minimum
     * @param resultField  the name of the result field
     * @return the group operation
     */
    public static GroupOperation groupAndMin(String groupByField, String minField, String resultField) {
        return Aggregation.group(groupByField).min(minField).as(resultField);
    }

    /**
     * Creates a group operation for finding maximum value of a field
     *
     * @param groupByField the field to group by
     * @param maxField     the field to find maximum
     * @param resultField  the name of the result field
     * @return the group operation
     */
    public static GroupOperation groupAndMax(String groupByField, String maxField, String resultField) {
        return Aggregation.group(groupByField).max(maxField).as(resultField);
    }
}
