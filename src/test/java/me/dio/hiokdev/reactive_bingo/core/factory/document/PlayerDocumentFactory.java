package me.dio.hiokdev.reactive_bingo.core.factory.document;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.PlayerDocument;

import java.time.OffsetDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerDocumentFactory {

    public static PlayerDocumentFactoryBuilder builder(final Player player) {
        return new PlayerDocumentFactoryBuilder(player);
    }

    public static class PlayerDocumentFactoryBuilder {

        private String id;
        private String name;
        private String email;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        public PlayerDocumentFactoryBuilder(final Player player) {
            this.id = player.id();
            this.name = player.name();
            this.email = player.email();
            this.createdAt = player.createdAt();
            this.updatedAt = player.updatedAt();
        }

        public PlayerDocumentFactoryBuilder preInsert() {
            this.id = null;
            this.createdAt = null;
            this.updatedAt = null;
            return this;
        }

        public PlayerDocumentFactoryBuilder preUpdate(final String id) {
            this.id = id;
            return this;
        }

        public PlayerDocument build() {
            return PlayerDocument.builder()
                    .id(id)
                    .name(name)
                    .email(email)
                    .createdAt(createdAt)
                    .updatedAt(updatedAt)
                    .build();
        }

    }

}
