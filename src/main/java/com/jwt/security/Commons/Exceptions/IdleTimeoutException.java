package com.jwt.security.Commons.Exceptions;

public class IdleTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IdleTimeoutException(String message)
    {
        super(message);

    }

}