package ru.veselov.websocketroomproject.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.websocketroomproject.validation.OrderDirectionValidator;

import java.lang.annotation.*;

/*
 *Annotation for constraint for checking soring order parameters
 */
@Target({ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderDirectionValidator.class)
@Documented
public @interface OrderDirection {
    String message() default "This value for parameter order not exists or unsupported for sorting order";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
