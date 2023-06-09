package ru.veselov.websocketroomproject.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.veselov.websocketroomproject.annotation.SortBy;

import java.util.List;

public class SortByFieldValidator implements ConstraintValidator<SortBy, Object> {

    private List<String> availableSortFields;

    @Override
    public void initialize(SortBy constraintAnnotation) {
        availableSortFields = List.of("createdAt", "name", "ownerName", "changedAt", "playerType");
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String field = (String) value;
        return availableSortFields.contains(field);
    }
}
