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

    public static <T> Specification<T> hasValue(String key, Boolean value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    public static <T> Specification<T> hasValue(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    public static <T> Specification<T> hasValue(String key, Integer value) {
        return (root, query, cb) -> value == null || value == 0 ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    public static <T> Specification<T> hasValueLevelMerchant(String key, Integer value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    public static <T> Specification<T> hasValueIncludeZero(String key, Integer value) {
        return (root, query, cb) -> value == null ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    public static <T> Specification<T> hasValueIgnoreCase(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() : cb.equal(cb.lower(root.get(key)),
                value.toLowerCase());
    }

    public static <T> Specification<T> contains(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() :
                cb.like(cb.lower(root.get(key)), "%" + value.toLowerCase() + "%");
    }

    public static <T, V> Specification<T> in(String key, Collection<V> values) {
        return (root, query, cb) -> {
            if (values == null || values.isEmpty()) {
                return cb.conjunction();
            }
            return root.get(key).in(values);
        };
    }

    public static <T> Specification<T> joinEqual(String joinAttribute, String key, Object value) {
        return (root, query, cb) -> {
            if (value == null) {
                return cb.conjunction();
            }
            Join<T, ?> join = root.join(joinAttribute, JoinType.INNER);
            return cb.equal(join.get(key), value);
        };
    }

    public static <T> Specification<T> hasValue(String key, Long value) {
        return (root, query, cb) -> value == null || value == 0 ? cb.conjunction() : cb.equal(root.get(key), value);
    }

    public static <T> Specification<T> hasValue(String parentName, String key, Long value) {
        return (root, query, cb) -> value == null || value == 0 ? cb.conjunction() : cb.equal(root.get(parentName).get(key), value);
    }

    public static <T> Specification<T> hasNotValue(String key, Long value) {
        return (root, query, cb) -> value == null || value == 0 ? cb.conjunction() : cb.notEqual(root.get(key), value);
    }

    public static <T> Specification<T> hasNotValue(String key, Integer value) {
        return (root, query, cb) -> value == null || value == 0 ? cb.conjunction() : cb.notEqual(root.get(key), value);
    }

    public static <T> Specification<T> hasLikeLowerCase(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() : cb.like(cb.lower(root.get(key)),
                "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> hasLikeIgnoreCase(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() : cb.like(cb.upper(root.get(key)),
                "%" + value.toUpperCase() + "%");
    }

    public static <T> Specification<T> hasLikeIgnoreCase(String key, String key2, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.conjunction() : cb.like(cb.upper(root.get(key).get(key2)),
                "%" + value.toUpperCase() + "%");
    }

    public static <T> Specification<T> hasLikeIgnoreCaseDisjunction(String key, String value) {
        return (root, query, cb) -> StringUtils.isBlank(value) ? cb.disjunction() : cb.like(cb.upper(root.get(key)),
                "%" + value.toUpperCase() + "%");
    }

    public static <T> Specification<T> hasValueIn(String key, Collection value) {
        return (root, query, cb) -> {
            if (null == value || value.size() == 0) {
                return cb.conjunction();
            }
            return root.get(key).in(value);
        };
    }

    public static <T> Specification<T> hasValueInDis(String key, Collection value) {
        return (root, query, cb) -> {
            if (null == value || value.size() == 0) {
                return cb.disjunction();
            }
            return root.get(key).in(value);
        };
    }

    public static <T, V> Specification<V> hasJoin(String key1Join, JoinType joinType, String key2Join, String value) {
        return (root, query, cb) -> {
            if (StringUtils.isBlank(value)) {
                return cb.conjunction();
            }
            final Join<T, V> join = root.join(key1Join, joinType);
            return cb.equal(join.get(key2Join), value);
        };
    }

    public static <T, V> Specification<V> hasJoin(String key1Join, JoinType joinType, String key2Join, Long value) {
        return (root, query, cb) -> {
            if (null == value || value == 0) {
                return cb.conjunction();
            }
            final Join<T, V> join = root.join(key1Join, joinType);
            return cb.equal(join.get(key2Join), value);
        };
    }

    public static <T, V, M> Specification<M> hasJoin(
            String key1Join, JoinType joinType, String key2Join, JoinType joinType2, String key, Long value
    ) {
        return (root, query, cb) -> {
            if (null == value || 0 == value) {
                return cb.conjunction();
            }
            final Join<T, V> join = root.join(key1Join, joinType);
            final Join<V, M> join2 = join.join(key2Join, joinType2);
            return cb.equal(join2.get(key), value);
        };
    }

    public static <T> Specification<T> greaterThan(String key, Timestamp fromDate) {
        return (root, query, cb) -> (fromDate == null) ? cb.conjunction() : cb.greaterThan(root.get(key), fromDate);
    }

    public static <T> Specification<T> lessThan(String key, Timestamp toDate) {
        return (root, query, cb) -> (toDate == null) ? cb.conjunction() : cb.lessThan(root.get(key), toDate);
    }

    public static <T> Specification<T> greaterThanOrEqualTo(String key, Timestamp fromDate) {
        return (root, query, cb) -> fromDate == null ? cb.conjunction() : cb.greaterThanOrEqualTo(root.get(key),
                fromDate);
    }

    public static <T> Specification<T> lessThanOrEqualTo(String key, Timestamp toDate) {
        return (root, query, cb) -> toDate == null ? cb.conjunction() : cb.lessThanOrEqualTo(root.get(key), toDate);
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
     * @param start          start timestamp
     * @param end            end timestamp
     * @param additionalSpec additional specification to apply within the date range
     * @param <T>            entity type
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