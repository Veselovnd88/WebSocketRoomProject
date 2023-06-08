package ru.veselov.websocketroomproject.validation;

import org.springframework.validation.BindingResult;

public interface DtoFieldValidationResponseService {

    void validateFields(BindingResult bindingResult);

}
