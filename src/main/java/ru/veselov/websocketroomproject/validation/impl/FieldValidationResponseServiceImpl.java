package ru.veselov.websocketroomproject.validation.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import ru.veselov.websocketroomproject.exception.CustomValidationException;
import ru.veselov.websocketroomproject.validation.FieldValidationResponseService;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class FieldValidationResponseServiceImpl implements FieldValidationResponseService {

    @Override
    public void validateFields(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            for (ObjectError error : bindingResult.getAllErrors()) {
                if (!(error instanceof FieldError)) {
                    errorMap.put(error.getCode(), error.getDefaultMessage());
                }
            }
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            }
            log.warn("Validation errors occurred [{}]", errorMap);
            throw new CustomValidationException("Wrong fields detected", errorMap);
        }
        log.info("Fields validated successfully");
    }

}
