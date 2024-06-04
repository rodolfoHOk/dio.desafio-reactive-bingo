package me.dio.hiokdev.reactive_bingo.infractructure.web.expectionhandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.FieldErrorResponse;
import me.dio.hiokdev.reactive_bingo.application.dto.responses.ProblemResponse;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.BaseErrorMessage;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.BingoCardAlreadyExistsException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.EmailAlreadyUsedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.NotFoundException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.ReactiveBingoException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RecursionException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyFinishedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundAlreadyInitiatedException;
import me.dio.hiokdev.reactive_bingo.domain.exceptions.RoundNotInitiatedException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ApiExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable throwable) {
        return Mono.error(throwable)
                .onErrorResume(RoundNotInitiatedException.class, e -> handleRoundNotInitiatedException(exchange, e))
                .onErrorResume(RoundAlreadyFinishedException.class, e -> handleRoundAlreadyFinishedException(exchange, e))
                .onErrorResume(RoundAlreadyInitiatedException.class, e -> handleRoundAlreadyInitiatedException(exchange, e))
                .onErrorResume(BingoCardAlreadyExistsException.class, e -> handleBingoCardAlreadyExistsException(exchange, e))
                .onErrorResume(EmailAlreadyUsedException.class, e -> handleEmailAlreadyUsedException(exchange, e))
                .onErrorResume(NotFoundException.class, e -> handleNotFoundException(exchange, e))
                .onErrorResume(RecursionException.class, e -> handleRecursionException(exchange, e))
                .onErrorResume(ConstraintViolationException.class, e -> handleConstraintViolationException(exchange, e))
                .onErrorResume(WebExchangeBindException.class, e -> handleWebExchangeBindException(exchange, e))
                .onErrorResume(MethodNotAllowedException.class, e -> handleMethodNotAllowedException(exchange, e))
                .onErrorResume(ResponseStatusException.class, e -> handleResponseStatusException(exchange, e))
                .onErrorResume(ReactiveBingoException.class, e -> handleReactiveBingoException(exchange, e))
                .onErrorResume(JsonProcessingException.class, e -> handleJsonProcessingException(exchange, e))
                .onErrorResume(Exception.class, e -> handleException(exchange, e))
                .then();
    }

    private Mono<Void> handleRoundNotInitiatedException(ServerWebExchange exchange, RoundNotInitiatedException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), ex.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleRoundAlreadyFinishedException(ServerWebExchange exchange, RoundAlreadyFinishedException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), ex.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleRoundAlreadyInitiatedException(ServerWebExchange exchange, RoundAlreadyInitiatedException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), ex.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleBingoCardAlreadyExistsException(ServerWebExchange exchange, BingoCardAlreadyExistsException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), ex.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleEmailAlreadyUsedException(ServerWebExchange exchange, EmailAlreadyUsedException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), ex.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleNotFoundException(ServerWebExchange exchange, NotFoundException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.NOT_FOUND.value(), ex.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleRecursionException(ServerWebExchange exchange, RecursionException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.LOOP_DETECTED.value(), ex.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleConstraintViolationException(ServerWebExchange exchange, ConstraintViolationException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), BaseErrorMessage
                        .GENERIC_BAD_REQUEST.getMessage()).build())
                .flatMap(problemResponse -> addFieldsErrors(problemResponse, ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleWebExchangeBindException(ServerWebExchange exchange, WebExchangeBindException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), BaseErrorMessage
                        .GENERIC_BAD_REQUEST.getMessage()).build())
                .flatMap(problemResponse -> addFieldsErrors(problemResponse, ex))
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleMethodNotAllowedException(ServerWebExchange exchange, MethodNotAllowedException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.METHOD_NOT_ALLOWED.value(), BaseErrorMessage
                        .GENERIC_METHOD_NOT_ALLOWED.params(exchange.getRequest().getMethod().name())
                        .getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleResponseStatusException(ServerWebExchange exchange, ResponseStatusException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.NOT_FOUND.value(), BaseErrorMessage
                        .GENERIC_NOT_FOUND.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleReactiveBingoException(ServerWebExchange exchange, ReactiveBingoException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.INTERNAL_SERVER_ERROR.value(), BaseErrorMessage
                        .GENERIC_EXCEPTION.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleJsonProcessingException(ServerWebExchange exchange, JsonProcessingException ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.BAD_REQUEST.value(), BaseErrorMessage
                        .GENERIC_BAD_REQUEST.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<Void> handleException(ServerWebExchange exchange, Exception ex) {
        return Mono.just(ProblemResponse.builder().create(HttpStatus.INTERNAL_SERVER_ERROR.value(), BaseErrorMessage
                        .GENERIC_EXCEPTION.getMessage()).build())
                .flatMap(problemResponse -> writeResponse(exchange, problemResponse));
    }

    private Mono<ProblemResponse> addFieldsErrors(final ProblemResponse problemResponse, ConstraintViolationException ex) {
        return Flux.fromIterable(ex.getConstraintViolations())
                .map(constraintViolation -> FieldErrorResponse.builder()
                        .name(((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().toString())
                        .message(constraintViolation.getMessage())
                        .build())
                .collectList()
                .map(fieldErrorResponses -> problemResponse.toBuilder().fields(fieldErrorResponses).build());
    }

    private Mono<ProblemResponse> addFieldsErrors(final ProblemResponse problemResponse, WebExchangeBindException ex) {
        return Flux.fromIterable(ex.getFieldErrors())
                .map(fieldError -> FieldErrorResponse.builder()
                        .name(fieldError instanceof FieldError error ? error.getField() : fieldError.getObjectName())
                        .message(messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()))
                        .build())
                .collectList()
                .map(fieldErrorResponses -> problemResponse.toBuilder().fields(fieldErrorResponses).build());
    }

    private Mono<Void> writeResponse(final ServerWebExchange exchange, ProblemResponse problemResponse) {
        return Mono.fromCallable(() -> {
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            exchange.getResponse().setStatusCode(HttpStatusCode.valueOf(problemResponse.status()));
            return new DefaultDataBufferFactory().wrap(objectMapper.writeValueAsBytes(problemResponse));
        }).flatMap(buffer -> exchange.getResponse().writeWith(Mono.just(buffer)));
    }

}
