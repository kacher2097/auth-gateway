package com.authenhub.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;

@Component
@WritingConverter
public class TimestampToDateConverter implements Converter<Timestamp, Date> {
    @Override
    public Date convert(Timestamp source) {
        return source != null ? new Date(source.getTime()) : null;
    }
}
