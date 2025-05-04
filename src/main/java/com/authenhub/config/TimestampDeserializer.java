package com.authenhub.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Custom deserializer for java.sql.Timestamp
 * Supports multiple date formats
 */
public class TimestampDeserializer extends JsonDeserializer<Timestamp> {

    private static final List<String> DATE_FORMATS = Arrays.asList(
            "yyyy-MM-dd'T'HH:mm:ss.SSSX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd"
    );

    @Override
    public Timestamp deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
        String dateAsString = jsonParser.getText();
        if (dateAsString == null || dateAsString.trim().isEmpty()) {
            return null;
        }

        // Try each format until one works
        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format);
                dateFormat.setLenient(false);
                return new Timestamp(dateFormat.parse(dateAsString).getTime());
            } catch (ParseException e) {
                // Try the next format
            }
        }

        // If we get here, none of the formats worked
        throw new IOException("Cannot parse date '" + dateAsString + "': not in any of the supported formats: " + DATE_FORMATS);
    }
}
