package me.dio.hiokdev.reactive_bingo.infractructure.persistence.mappers;

import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import me.dio.hiokdev.reactive_bingo.infractructure.persistence.documents.RoundDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoundDocumentMapper {

    @Mapping(target = "bingoCard", ignore = true)
    @Mapping(target = "sortedNumber", ignore = true)
    Round toDomainModel(final RoundDocument document);

    RoundDocument toDocument(final Round domainModel);

}
