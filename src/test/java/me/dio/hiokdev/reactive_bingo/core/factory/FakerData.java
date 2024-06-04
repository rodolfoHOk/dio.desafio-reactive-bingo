package me.dio.hiokdev.reactive_bingo.core.factory;

import com.github.javafaker.Faker;
import lombok.Getter;

import java.util.Locale;

public class FakerData {

    @Getter
    private static final Faker faker = new Faker(Locale.of("pt", "BR"));

}
