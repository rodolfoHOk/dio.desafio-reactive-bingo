package me.dio.hiokdev.reactive_bingo.core.factory.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.core.factory.FakerData;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import org.bson.types.ObjectId;

import java.time.OffsetDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerFactory {

    public static PlayerFactoryBuilder builder() {
        return new PlayerFactoryBuilder();
    }

    public static class PlayerFactoryBuilder {

        private String id;
        private String name;
        private String email;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public PlayerFactoryBuilder() {
            var faker = FakerData.getFaker();
            this.id = ObjectId.get().toString();
            this.name = faker.name().fullName();
            this.email = faker.internet().emailAddress();
            this.createdAt = OffsetDateTime.now();
            this.updatedAt = OffsetDateTime.now();
        }

        public PlayerFactoryBuilder preInsert() {
            this.id = null;
            this.createdAt = null;
            this.updatedAt = null;
            return this;
        }

        public PlayerFactoryBuilder preUpdate(final String id) {
            this.id = id;
            return this;
        }

        public Player build() {
            return Player.builder()
                    .id(id)
                    .name(name)
                    .email(email)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

    }

}
