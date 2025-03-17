package com.ryanair.task2.api.excepcionhandlers;

import com.ryanair.task2.datasource.exceptions.RemoteErrorException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RemoteErrorHandler {
    @ExceptionHandler(RemoteErrorException.class)
    public ResponseEntity<String> handleRemoteError(RemoteErrorException e) {
        return ResponseEntity.status(e.getCode()).body(e.getMessage());
    }
}
