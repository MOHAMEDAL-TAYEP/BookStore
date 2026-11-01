package com.AL_Tayep.BookStore.Exceptions;





public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}