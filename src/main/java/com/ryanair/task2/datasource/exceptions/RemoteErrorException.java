package com.ryanair.task2.datasource.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class RemoteErrorException extends RuntimeException {
    private final HttpStatusCode code;

    public RemoteErrorException(String message, HttpStatusCode code) {
        super(message);
        this.code = code;
    }
}
