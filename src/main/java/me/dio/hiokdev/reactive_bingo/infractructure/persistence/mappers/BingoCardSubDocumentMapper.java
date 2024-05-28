package me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers;

import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.BingoCardSubDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BingoCardSubDocumentMapper {


    @Mapping(target = "id", source = "bingoCardId")
    BingoCard toDomainModel(final BingoCardSubDocument document);

    @Mapping(target = "bingoCardId", source = "id")
    BingoCardSubDocument toDocument(final BingoCard domainModel);

}
