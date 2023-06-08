package ru.veselov.websocketroomproject.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.websocketroomproject.entity.PlayerType;
import ru.veselov.websocketroomproject.validation.PlayerTypeValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PlayerTypeValidator.class)
@Documented
public @interface SupportedPlayer {
    String message() default "This player type is not supported";

    Class<? extends PlayerType> playerType() default PlayerType.class;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
