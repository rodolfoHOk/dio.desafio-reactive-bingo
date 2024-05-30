package me.dio.hiokdev.reactive_bingo.application.mappers;

import me.dio.hiokdev.reactive_bingo.application.dto.responses.BingoCardResponse;
import me.dio.hiokdev.reactive_bingo.domain.models.BingoCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BingoCardMapper {

    @Mapping(target = "playerId", source = "player.id")
    BingoCardResponse toResponse(final BingoCard domaiModel);

}
