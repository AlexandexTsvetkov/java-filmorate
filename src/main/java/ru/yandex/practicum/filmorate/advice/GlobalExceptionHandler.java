package ru.yandex.practicum.filmorate.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);

            log.debug("Ошибка валидации поля {}", fieldName);
        }));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", "Not found exception");
        errorMap.put("details", exception.getMessage());

        log.debug(exception.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException exception) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", "Validation exception");
        errorMap.put("details", exception.getMessage());

        log.debug(exception.getMessage());

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }
}
