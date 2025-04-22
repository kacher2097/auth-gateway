package com.authenhub.utils;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;

public class SpecificationCustom {

    /**
     * Combine list of specification by and condition.
     *
     * @param specifications list of specification need to combine
     * @param <T>            Type
     * @return a combined {@link Specification}
     */
    @SafeVarargs
    public static <T> Specification<T> and(Specification<T>... specifications) {
        if (null == specifications || specifications.length == 0) return (root, query, cb) -> cb.conjunction();
        return (root, query, cb) -> {
            Predicate[] predicates = Arrays.stream(specifications)
                    .filter(spec -> spec != null)
                    .map(specification -> specification.toPredicate(root, query, cb))
                    .toArray(Predicate[]::new);
            return predicates.length > 0 ? cb.and(predicates) : cb.conjunction();
        };
    }

    /**
     * Combine list of specification by or condition.
     *
     * @param specifications list of specification need to combine
     * @param <T>            Type
     * @return a combined {@link Specification}
     */
    @SafeVarargs
    public static <T> Specification<T> or(Specification<T>... specifications) {
        if (null == specifications || specifications.length == 0) return (root, query, cb) -> cb.conjunction();
        return (root, query, cb) -> {
            Predicate[] predicates = Arrays.stream(specifications)
                    .filter(spec -> spec != null)
                    .map(specification -> specification.toPredicate(root, query, cb))
                    .toArray(Predicate[]::new);
            return predicates.length > 0 ? cb.or(predicates) : cb.conjunction();
        };
    }

    /**
     * Create a specification for date range query.
     *
     * @param key      field name
     * @param fromDate start date
     * @param toDate   end date
     * @param <T>      entity type
     * @return specification for date range
     */
    public static <T> Specification<T> between(String key, Timestamp fromDate, Timestamp toDate) {
        return (root, query, cb) -> (fromDate == null || toDate == null) ? cb.conjunction() : cb.between(root.get(key),
                fromDate, toDate);
    }

    /**
     * Create a specification for boolean value query.
     *
     * @param key   field name
     * @param value boolean value
     * @param <T>   entity type
     * @return specification for boolean value
     */
    public static <T> Specification<T> hasValue(String key, Boolean value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    /**
     * Create a specification for string value query.
     *
     * @param key   field name
     * @param value string value
     * @param <T>   entity type
     * @return specification for string value
     */
    public static <T> Specification<T> hasValue(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    /**
     * Create a specification for integer value query.
     *
     * @param key   field name
     * @param value integer value
     * @param <T>   entity type
     * @return specification for integer value
     */
    public static <T> Specification<T> hasValue(String key, Integer value) {
        return (root, query, cb) -> value == null || value == 0 ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    /**
     * Create a specification for merchant level query.
     *
     * @param key   field name
     * @param value integer value
     * @param <T>   entity type
     * @return specification for merchant level
     */
    public static <T> Specification<T> hasValueLevelMerchant(String key, Integer value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    /**
     * Create a specification for integer value query including zero.
     *
     * @param key   field name
     * @param value integer value
     * @param <T>   entity type
     * @return specification for integer value including zero
     */
    public static <T> Specification<T> hasValueIncludeZero(String key, Integer value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    /**
     * Create a specification for case-insensitive string value query.
     *
     * @param key   field name
     * @param value string value
     * @param <T>   entity type
     * @return specification for case-insensitive string value
     */
    public static <T> Specification<T> hasValueIgnoreCase(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() : cb.equal(cb.lower(root.get(key)),
                value.toLowerCase());
    }

    /**
     * Create a specification for string value containing query (case-insensitive).
     *
     * @param key   field name
     * @param value string value
     * @param <T>   entity type
     * @return specification for string value containing
     */
    public static <T> Specification<T> contains(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() :
                cb.like(cb.lower(root.get(key)), "%" + value.toLowerCase() + "%");
    }

    /**
     * Create a specification for values in a collection.
     *
     * @param key    field name
     * @param values collection of values
     * @param <T>    entity type
     * @param <V>    value type
     * @return specification for values in collection
     */
    public static <T, V> Specification<T> in(String key, Collection<V> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(key).in(values);
        };
    }

    /**
     * Create a specification for join query.
     *
     * @param joinAttribute join attribute name
     * @param key          field name in joined entity
     * @param value        value to compare
     * @param <T>          entity type
     * @return specification for join query
     */
    public static <T> Specification<T> joinEqual(String joinAttribute, String key, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            Join<T, ?> join = root.join(joinAttribute, JoinType.INNER);
            return cb.equal(join.get(key), value);
        };
    }

    /**
     * Count entities within a date range.
     * Equivalent to: SELECT COUNT(a) FROM Entity a WHERE a.timestamp >= start AND a.timestamp <= end
     *
     * @param timestampField field name for timestamp
     * @param start start timestamp
     * @param end end timestamp
     * @param <T> entity type
     * @return specification for counting by date range
     */
//    public static <T> Specification<T> countByDateRange(String timestampField, Timestamp start, Timestamp end) {
//        return (root, query, cb) -> {
//            if (start == null || end == null) {
//                return cb.conjunction();
//            }
//
//            // Set count query
//            if (Long.class != query.getResultType()) {
//                query.select(cb.count(root));
//            }
//
//            return cb.and(
//                    cb.greaterThanOrEqualTo(root.get(timestampField), start),
//                    cb.lessThanOrEqualTo(root.get(timestampField), end)
//            );
//        };
//    }

    /**
     * Count entities within a date range and with endpoint containing a pattern.
     * Equivalent to: SELECT COUNT(a) FROM Entity a WHERE a.timestamp >= start AND a.timestamp <= end AND a.endpoint LIKE %pattern%
     *
     * @param timestampField field name for timestamp
     * @param endpointField field name for endpoint
     * @param start start timestamp
     * @param end end timestamp
     * @param endpointPattern pattern to search in endpoint
     * @param <T> entity type
     * @return specification for counting by date range and endpoint pattern
     */
//    public static <T> Specification<T> countByEndpointContaining(String timestampField, String endpointField,
//                                                                 Timestamp start, Timestamp end, String endpointPattern) {
//        return (root, query, cb) -> {
//            if (start == null || end == null || StringUtils.isBlank(endpointPattern)) {
//                return cb.conjunction();
//            }
//
//            // Set count query
//            if (Long.class != query.getResultType()) {
//                query.select(cb.count(root));
//            }
//
//            return cb.and(
//                    cb.greaterThanOrEqualTo(root.get(timestampField), start),
//                    cb.lessThanOrEqualTo(root.get(timestampField), end),
//                    cb.like(root.get(endpointField), "%" + endpointPattern + "%")
//            );
//        };
//    }

    /**
     * Count entities within a date range, with specific endpoint and status code.
     * Equivalent to: SELECT COUNT(a) FROM Entity a WHERE a.timestamp >= start AND a.timestamp <= end AND a.endpoint = endpoint AND a.statusCode = statusCode
     *
     * @param timestampField field name for timestamp
     * @param endpointField field name for endpoint
     * @param statusCodeField field name for status code
     * @param start start timestamp
     * @param end end timestamp
     * @param endpoint exact endpoint value
     * @param statusCode status code value
     * @param <T> entity type
     * @return specification for counting by date range, endpoint and status code
     */
//    public static <T> Specification<T> countByEndpointAndStatusCode(String timestampField, String endpointField,
//                                                                    String statusCodeField, Timestamp start, Timestamp end,
//                                                                    String endpoint, int statusCode) {
//        return (root, query, cb) -> {
//            if (start == null || end == null || StringUtils.isBlank(endpoint)) {
//                return cb.conjunction();
//            }
//
//            // Set count query
//            if (Long.class != query.getResultType()) {
//                query.select(cb.count(root));
//            }
//
//            return cb.and(
//                    cb.greaterThanOrEqualTo(root.get(timestampField), start),
//                    cb.lessThanOrEqualTo(root.get(timestampField), end),
//                    cb.equal(root.get(endpointField), endpoint),
//                    cb.equal(root.get(statusCodeField), statusCode)
//            );
//        };
//    }

    /**
     * Create a specification that applies a count transformation to the query.
     * This is useful when you want to count entities matching certain criteria.
     *
     * @param spec the specification containing the criteria
     * @param <T> entity type
     * @return a specification that counts entities matching the criteria
     */
//    public static <T> Specification<T> count(Specification<T> spec) {
//        return (root, query, cb) -> {
//            query.select(cb.count(root));
//            return spec.toPredicate(root, query, cb);
//        };
//    }

    /**
     * Create a specification for a general date range query with multiple conditions.
     * This is a more flexible version that allows you to specify multiple conditions within a date range.
     *
     * @param timestampField field name for timestamp
     * @param start start timestamp
     * @param end end timestamp
     * @param additionalSpec additional specification to apply within the date range
     * @param <T> entity type
     * @return specification for date range with additional conditions
     */
    public static <T> Specification<T> dateRangeWithCondition(String timestampField, Timestamp start, Timestamp end,
                                                              Specification<T> additionalSpec) {
        return (root, query, cb) -> {
            if (start == null || end == null) {
                return additionalSpec != null ? additionalSpec.toPredicate(root, query, cb) : cb.conjunction();
            }

            Predicate dateRangePredicate = cb.and(
                    cb.greaterThanOrEqualTo(root.get(timestampField), start),
                    cb.lessThanOrEqualTo(root.get(timestampField), end)
            );

            if (additionalSpec != null) {
                Predicate additionalPredicate = additionalSpec.toPredicate(root, query, cb);
                return cb.and(dateRangePredicate, additionalPredicate);
            }

            return dateRangePredicate;
        };
    }
}