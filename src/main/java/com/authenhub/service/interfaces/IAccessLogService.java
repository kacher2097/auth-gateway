package com.authenhub.service.interfaces;

import com.authenhub.bean.statistic.StatisticGetResponse;
import com.authenhub.bean.statistic.StatisticSearchRequest;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Interface for access log service operations
 */
public interface IAccessLogService {

    /**
     * Get access stats
     *
     * @param request statistic search request
     * @return access stats as StatisticGetResponse
     */
    StatisticGetResponse getAccessStats(StatisticSearchRequest request);

    /**
     * Count total visits in date range
     *
     * @param start start date
     * @param end end date
     * @return total number of visits
     */
    long countTotalVisits(Timestamp start, Timestamp end);

    /**
     * Count by day
     *
     * @param start start date
     * @param end end date
     * @return daily counts
     */
    Long countByDay(Timestamp start, Timestamp end);

    /**
     * Count by browser
     *
     * @param start start date
     * @param end end date
     * @return browser counts
     */
    List<Map<String, Object>> countByBrowser(Timestamp start, Timestamp end);

    /**
     * Count by device type
     *
     * @param start start date
     * @param end end date
     * @return device type counts
     */
    List<Map<String, Object>> countByDeviceType(Timestamp start, Timestamp end);

    /**
     * Get top endpoints
     *
     * @param start start date
     * @param end end date
     * @return top endpoints
     */
    List<Map<String, Object>> getTopEndpoints(Timestamp start, Timestamp end);

    /**
     * Get top users
     *
     * @param start start date
     * @param end end date
     * @return top users
     */
    List<Map<String, Object>> getTopUsers(Timestamp start, Timestamp end);

    /**
     * Get login activity
     *
     * @param start start date
     * @param end end date
     * @return login activity
     */
    List<Map<String, Object>> getLoginActivity(Timestamp start, Timestamp end);
}
