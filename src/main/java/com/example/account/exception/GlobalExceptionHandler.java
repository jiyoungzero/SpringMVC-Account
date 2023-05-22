package com.example.account.exception;

import com.example.account.dto.ErrorResponse;
import com.example.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.account.type.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountException.class)
    public ErrorResponse handlerAccountException(AccountException e){
        log.error("{} is occurred.", e.getErrorMessage());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handlerDataIntegrityViolationException(DataIntegrityViolationException e){
        log.error("DataIntegrityViolationException is occurred", e);

        return new ErrorResponse(INVALID_REQUEST, INTERNAL_SERVER_ERROR.getDescription());
    }


    @ExceptionHandler(AccountException.class)
    public ErrorResponse handlerException(Exception e){
        log.error("Exception is occurred");

        return new ErrorResponse(INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription());
    }
}
