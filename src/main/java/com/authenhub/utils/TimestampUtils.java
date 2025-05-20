package com.authenhub.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Utility class for working with Timestamp objects
 */
public class TimestampUtils {

    private TimestampUtils() {
        // Private constructor to prevent instantiation
    }

    /**
     * Convert Date to Timestamp
     *
     * @param date the date to convert
     * @return the timestamp
     */
    public static Timestamp fromDate(Date date) {
        if (date == null) {
            return null;
        }
        return new Timestamp(date.getTime());
    }

    /**
     * Convert Timestamp to Date
     *
     * @param timestamp the timestamp to convert
     * @return the date
     */
    public static Date toDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new Date(timestamp.getTime());
    }

    /**
     * Convert LocalDateTime to Timestamp
     *
     * @param localDateTime the local date time to convert
     * @return the timestamp
     */
    public static Timestamp fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }

    /**
     * Convert Timestamp to LocalDateTime
     *
     * @param timestamp the timestamp to convert
     * @return the local date time
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

    /**
     * Convert Instant to Timestamp
     *
     * @param instant the instant to convert
     * @return the timestamp
     */
    public static Timestamp fromInstant(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }

    /**
     * Convert Timestamp to Instant
     *
     * @param timestamp the timestamp to convert
     * @return the instant
     */
    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }

    /**
     * Create a timestamp from milliseconds
     *
     * @param milliseconds the milliseconds
     * @return the timestamp
     */
    public static Timestamp fromMillis(long milliseconds) {
        return new Timestamp(milliseconds);
    }

    /**
     * Get milliseconds from timestamp
     *
     * @param timestamp the timestamp
     * @return the milliseconds
     */
    public static long toMillis(Timestamp timestamp) {
        if (timestamp == null) {
            return 0;
        }
        return timestamp.getTime();
    }

    /**
     * Add seconds to a timestamp
     *
     * @param timestamp the timestamp
     * @param seconds   the seconds to add
     * @return the new timestamp
     */
    public static Timestamp addSeconds(Timestamp timestamp, int seconds) {
        if (timestamp == null) {
            return null;
        }
        return new Timestamp(timestamp.getTime() + (seconds * 1000L));
    }

    /**
     * Add minutes to a timestamp
     *
     * @param timestamp the timestamp
     * @param minutes   the minutes to add
     * @return the new timestamp
     */
    public static Timestamp addMinutes(Timestamp timestamp, int minutes) {
        return addSeconds(timestamp, minutes * 60);
    }

    /**
     * Add hours to a timestamp
     *
     * @param timestamp the timestamp
     * @param hours     the hours to add
     * @return the new timestamp
     */
    public static Timestamp addHours(Timestamp timestamp, int hours) {
        return addMinutes(timestamp, hours * 60);
    }

    /**
     * Add days to a timestamp
     *
     * @param timestamp the timestamp
     * @param days      the days to add
     * @return the new timestamp
     */
    public static Timestamp addDays(Timestamp timestamp, int days) {
        return addHours(timestamp, days * 24);
    }

    /**
     * Get current timestamp
     *
     * @return Current timestamp
     */
    public static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now());
    }

    /**
     * Convert String to Timestamp
     *
     * @param dateTimeString String to convert (format: yyyy-MM-dd HH:mm:ss)
     * @return Timestamp
     */
    public static Timestamp fromString(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        return Timestamp.valueOf(dateTimeString);
    }

    /**
     * Convert Timestamp to String
     *
     * @param timestamp Timestamp to convert
     * @return String (format: yyyy-MM-dd HH:mm:ss)
     */
    public static String toString(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toString();
    }
}
