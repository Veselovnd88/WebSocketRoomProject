package ru.veselov.websocketroomproject.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.veselov.websocketroomproject.dto.ExceptionResponse;
import ru.veselov.websocketroomproject.exception.NotCorrectOwnerException;
import ru.veselov.websocketroomproject.exception.RoomAlreadyExistsException;
import ru.veselov.websocketroomproject.exception.RoomNotFoundException;

@ControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler({RoomAlreadyExistsException.class})
    public ResponseEntity<ExceptionResponse> handleConflictException(RuntimeException e) {
        log.warn("[{}] handled", e.getClass());
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler({NotCorrectOwnerException.class})
    public ResponseEntity<ExceptionResponse> handleNotAuthorizedException(RuntimeException e) {
        log.warn("[{}] handled", e.getClass());
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({RoomNotFoundException.class})
    public ResponseEntity<ExceptionResponse> handleNoFoundException(RuntimeException e) {
        log.warn("[{}] handled", e.getClass());
        return new ResponseEntity<>(new ExceptionResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }
}
