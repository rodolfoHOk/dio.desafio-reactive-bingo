package me.dio.hiokdev.reactive_bingo.core.mongo;

import org.springframework.core.convert.converter.Converter;

import java.time.OffsetDateTime;
import java.util.Date;

public class OffsetDateTimeToDateConverter implements Converter<OffsetDateTime, Date> {

    @Override
    public Date convert(OffsetDateTime source) {
        return Date.from(source.toInstant());
    }

}
