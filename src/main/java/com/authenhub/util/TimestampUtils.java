package com.authenhub.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Utility class for working with Timestamp objects
 */
public class TimestampUtils {
    
    /**
     * Get current timestamp
     * @return Current timestamp
     */
    public static Timestamp now() {
        return Timestamp.valueOf(LocalDateTime.now());
    }
    
    /**
     * Convert LocalDateTime to Timestamp
     * @param localDateTime LocalDateTime to convert
     * @return Timestamp
     */
    public static Timestamp fromLocalDateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Timestamp.valueOf(localDateTime);
    }
    
    /**
     * Convert Timestamp to LocalDateTime
     * @param timestamp Timestamp to convert
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }
    
    /**
     * Convert String to Timestamp
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
