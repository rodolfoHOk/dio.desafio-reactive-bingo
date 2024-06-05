package me.dio.hiokdev.reactive_bingo.core.mongo;

import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

@Component("dateTimeProvider")
public class OffsetDateTimeProvider implements DateTimeProvider {

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(OffsetDateTime.now(ZoneOffset.UTC));
    }

}
