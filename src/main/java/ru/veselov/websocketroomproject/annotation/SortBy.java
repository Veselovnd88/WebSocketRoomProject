package ru.veselov.websocketroomproject.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.websocketroomproject.validation.SortByFieldValidator;

import java.lang.annotation.*;

/**
 * Annotation for constraint checking sorting parameters
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SortByFieldValidator.class)
@Documented
public @interface SortBy {
    String message() default "This value for parameter sort not exists or unsupported for sorting";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
