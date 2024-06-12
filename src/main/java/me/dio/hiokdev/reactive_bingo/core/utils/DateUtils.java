package me.dio.hiokdev.reactive_bingo.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    public static Date toDate(OffsetDateTime offsetDateTime) {
        return Date.from(offsetDateTime.toInstant());
    }

}
