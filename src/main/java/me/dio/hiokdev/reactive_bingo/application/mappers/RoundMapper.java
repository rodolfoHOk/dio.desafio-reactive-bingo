package me.dio.hiokdev.reactive_bingo.application.mappers;

import me.dio.hiokdev.reactive_bingo.application.dto.responses.PagedRoundsResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.RoundResponse;
import me.dio.hiokdev.reactive_bingo.domain.dto.PagedRounds;
import me.dio.hiokdev.reactive_bingo.domain.models.Round;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoundMapper {

    RoundResponse toResponse(final Round domainModel);

    PagedRoundsResponse toResponse(final PagedRounds domainDto);

}
