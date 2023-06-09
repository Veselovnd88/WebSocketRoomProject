package ru.veselov.websocketroomproject.validation;

import org.springframework.validation.BindingResult;

public interface FieldValidationResponseService {

    void validateFields(BindingResult bindingResult);

}
