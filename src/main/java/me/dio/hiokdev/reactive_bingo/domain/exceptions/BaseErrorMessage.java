package me.dio.hiokdev.reactive_bingo.domain.exceptions;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

@RequiredArgsConstructor
public class BaseErrorMessage {

    private static final String DEFAULT_RESOURCE = "messages";
    private final String key;
    private String[] params;

    public BaseErrorMessage params(final String... params) {
        this.params = ArrayUtils.clone(params);
        return this;
    }

    public String getMessage() {
        var message = getMessageFromBundle(key);
        if (ArrayUtils.isNotEmpty(params)) {
            var formatter = new MessageFormat(message);
            message = formatter.format(params);
        }
        return message;
    }

    private String getMessageFromBundle(final String key) {
        var bundle = ResourceBundle.getBundle(DEFAULT_RESOURCE);
        return bundle.getString(key);
    }

    public static final BaseErrorMessage GENERIC_EXCEPTION = new BaseErrorMessage("generic");
    public static final BaseErrorMessage GENERIC_NOT_FOUND = new BaseErrorMessage("generic.notFound");
    public static final BaseErrorMessage GENERIC_BAD_REQUEST = new BaseErrorMessage("generic.badRequest");
    public static final BaseErrorMessage GENERIC_METHOD_NOT_ALLOWED = new BaseErrorMessage("generic.methodNotAllowed");
    public static final BaseErrorMessage GENERIC_MAX_RETRIES = new BaseErrorMessage("generic.maxRetries");
    public static final BaseErrorMessage GENERIC_MAX_RECURSION = new BaseErrorMessage("generic.maxRecursionExceeded");
    public static final BaseErrorMessage PLAYER_NOT_FOUND_WITH_ID = new BaseErrorMessage("player.notFoundWithId");
    public static final BaseErrorMessage PLAYER_NOT_FOUND_WITH_EMAIL = new BaseErrorMessage("player.notFoundWithEmail");
    public static final BaseErrorMessage PLAYER_EMAIL_ALREADY_USED = new BaseErrorMessage("player.emailAlreadyUsed");
    public static final BaseErrorMessage ROUND_ALREADY_INITIATED = new BaseErrorMessage("round.alreadyInitiated");
    public static final BaseErrorMessage ROUND_ALREADY_FINISHED = new BaseErrorMessage("round.alreadyFinished");
    public static final BaseErrorMessage ROUND_NOT_INITIATED = new BaseErrorMessage("round.notInitiated");
    public static final BaseErrorMessage ROUND_NOT_FOUND = new BaseErrorMessage("round.notFound");
    public static final BaseErrorMessage BINGO_CARD_ALREADY_EXISTS = new BaseErrorMessage("bingoCard.alreadyExists");

}
