package ru.veselov.websocketroomproject.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ru.veselov.websocketroomproject.validation.OrderDirectionValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 *Annotation for constraint for checking soring order parameters
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderDirectionValidator.class)
@Documented
public @interface OrderDirection {

    String message() default "This value for parameter order not exists or unsupported for sorting order";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
