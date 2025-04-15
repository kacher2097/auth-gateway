package com.authenhub.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Utility class for MongoDB queries using Criteria
 */
public class MongoQueryUtils {

    private MongoQueryUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Creates a Criteria for a Long value if it's not null
     *
     * @param fieldName the field name
     * @param value     the value
     * @return the criteria
     */
    public static Criteria hasValue(String fieldName, Long value) {
        return Optional.ofNullable(value)
                .map(v -> where(fieldName).is(v))
                .orElse(where(StringUtils.EMPTY));
    }

    /**
     * Creates a Criteria for a Boolean value if it's not null
     *
     * @param fieldName the field name
     * @param value     the value
     * @return the criteria
     */
    public static Criteria hasValue(String fieldName, Boolean value) {
        return Optional.ofNullable(value)
                .map(v -> where(fieldName).is(v))
                .orElse(where(StringUtils.EMPTY));
    }

    /**
     * Creates a Criteria for an Integer value if it's not null and not zero
     *
     * @param fieldName the field name
     * @param value     the value
     * @return the criteria
     */
    public static Criteria hasValue(String fieldName, Integer value) {
        return value == null || value == 0 ? where(StringUtils.EMPTY) : where(fieldName).is(value);
    }

    /**
     * Creates a Criteria for a String value if it's not null and not empty
     *
     * @param fieldName the field name
     * @param value     the value
     * @return the criteria
     */
    public static Criteria hasValue(String fieldName, String value) {
        return value == null || value.isEmpty() ? where(StringUtils.EMPTY) : where(fieldName).is(value);
    }

    /**
     * Creates a Criteria for a Date range
     *
     * @param fieldName the field name
     * @param startDate the start date
     * @param endDate   the end date
     * @return the criteria
     */
    public static Criteria dateRange(String fieldName, Date startDate, Date endDate) {
        Criteria criteria = where(StringUtils.EMPTY);
        
        if (startDate != null && endDate != null) {
            criteria = where(fieldName).gte(startDate).lte(endDate);
        } else if (startDate != null) {
            criteria = where(fieldName).gte(startDate);
        } else if (endDate != null) {
            criteria = where(fieldName).lte(endDate);
        }
        
        return criteria;
    }

    /**
     * Creates a Criteria for a case-insensitive text search
     *
     * @param fieldName the field name
     * @param value     the search text
     * @return the criteria
     */
    public static Criteria textSearch(String fieldName, String value) {
        return value == null || value.isEmpty() 
                ? where(StringUtils.EMPTY) 
                : where(fieldName).regex(Pattern.compile(value, Pattern.CASE_INSENSITIVE));
    }

    /**
     * Creates a Criteria for a collection of values (IN operator)
     *
     * @param fieldName the field name
     * @param values    the collection of values
     * @return the criteria
     */
    public static Criteria in(String fieldName, Collection<?> values) {
        return values == null || values.isEmpty() 
                ? where(StringUtils.EMPTY) 
                : where(fieldName).in(values);
    }

    /**
     * Creates a Query from multiple Criteria with AND operator
     *
     * @param criteriaList the list of criteria
     * @return the query
     */
    public static Query buildAndQuery(Criteria... criteriaList) {
        Criteria combinedCriteria = new Criteria();
        
        if (criteriaList != null && criteriaList.length > 0) {
            combinedCriteria = combinedCriteria.andOperator(criteriaList);
        }
        
        return new Query(combinedCriteria);
    }

    /**
     * Creates a Query from multiple Criteria with OR operator
     *
     * @param criteriaList the list of criteria
     * @return the query
     */
    public static Query buildOrQuery(Criteria... criteriaList) {
        Criteria combinedCriteria = new Criteria();
        
        if (criteriaList != null && criteriaList.length > 0) {
            combinedCriteria = combinedCriteria.orOperator(criteriaList);
        }
        
        return new Query(combinedCriteria);
    }
}
