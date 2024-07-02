package ru.yandex.practicum.filmorate.exception;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ValidationException extends MethodArgumentNotValidException {
    public ValidationException(MethodParameter parameter, BindingResult bindingResult) {
        super(parameter, bindingResult);
    }
}