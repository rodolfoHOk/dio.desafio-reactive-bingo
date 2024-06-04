package me.dio.hiokdev.reactive_bingo.core.factory.request;

import com.github.javafaker.Faker;
import me.dio.hiokdev.reactive_bingo.application.dto.requests.PlayerRequest;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;

public class PlayerRequestFactory {

    public static PlayerRequestFactoryBuilder builder() {
        return new PlayerRequestFactoryBuilder();
    }

    public static class PlayerRequestFactoryBuilder {

        private String name;
        private String email;
        private final Faker faker = FakerData.getFaker();

        public PlayerRequestFactoryBuilder() {
            this.name = faker.name().fullName();
            this.email = faker.internet().emailAddress();
        }

        public PlayerRequestFactoryBuilder blankName() {
            this.name = " ";
            return this;
        }

        public PlayerRequestFactoryBuilder nullName() {
            this.name = null;
            return this;
        }

        public PlayerRequestFactoryBuilder shortName() {
            this.name = faker.lorem().characters(2);
            return this;
        }

        public PlayerRequestFactoryBuilder longName() {
            this.name = faker.lorem().characters(151);
            return this;
        }

        public PlayerRequestFactoryBuilder blankEmail() {
            this.email = " ";
            return this;
        }

        public PlayerRequestFactoryBuilder nullEmail() {
            this.email = null;
            return this;
        }

        public PlayerRequestFactoryBuilder shortEmail() {
            this.email = faker.lorem().characters(2);
            return this;
        }

        public PlayerRequestFactoryBuilder longEmail() {
            this.email = faker.lorem().characters(151);
            return this;
        }

        public PlayerRequestFactoryBuilder invalidEmail() {
            this.email = faker.lorem().word();
            return this;
        }

        public PlayerRequest build() {
            return PlayerRequest.builder()
                    .name(name)
                    .email(email)
                    .build();
        }

    }

}
