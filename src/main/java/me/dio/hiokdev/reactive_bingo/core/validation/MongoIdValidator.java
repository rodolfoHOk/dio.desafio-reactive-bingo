package me.dio.hiokdev.reactive_bingo.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

public class MongoIdValidator implements ConstraintValidator<MongoId, String> {

    @Override
    public void initialize(final MongoId constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext constraintValidatorContext) {
        return StringUtils.isNotBlank(value) && ObjectId.isValid(value);
    }

}
