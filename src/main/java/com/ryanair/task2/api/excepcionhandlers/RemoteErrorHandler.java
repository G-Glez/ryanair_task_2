package com.ryanair.task2.api.excepcionhandlers;

import com.ryanair.task2.datasource.exceptions.RemoteErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class RemoteErrorHandler {
    @ExceptionHandler(RemoteErrorException.class)
    public ResponseEntity<String> handleRemoteError(RemoteErrorException e) {
        log.error("Remote error", e);
        return ResponseEntity.status(e.getCode()).body(e.getMessage());
    }
}
