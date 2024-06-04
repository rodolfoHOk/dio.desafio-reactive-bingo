package me.dio.hiokdev.reactive_bingo.core.factory;

import com.github.javafaker.Faker;
import lombok.Getter;

import java.util.Locale;
import java.util.Random;

public class FakerData {

    @Getter
    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

    public static <T extends Enum<?>> T randomEnum(final Class<T> clazz) {
        var x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

}
