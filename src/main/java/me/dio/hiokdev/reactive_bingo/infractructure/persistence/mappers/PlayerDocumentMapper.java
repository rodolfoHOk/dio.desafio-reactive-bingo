package me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers;

import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.PlayerDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlayerDocumentMapper {

    Player toDomainModel(final PlayerDocument document);

    PlayerDocument toDocument(final Player domainModel);

}
