package me.dio.hiokdev.reactive_bingo.application.mappers;

import me.dio.hiokdev.reactive_bingo.application.dto.requests.PlayerRequest;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedPlayersResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.PlayerResponse;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedPlayers;
import me.dio.hiokdev.reactive_bingo.domain.models.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PlayerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Player toDomainModel(final PlayerRequest request);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Player toDomainModel(final PlayerRequest request, final String id);

    PlayerResponse toResponse(final Player domainModel);

    PagedPlayersResponse toResponse(final PagedPlayers domainDto);

}
