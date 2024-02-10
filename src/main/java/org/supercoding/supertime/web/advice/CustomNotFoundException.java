package org.supercoding.supertime.web.advice;

public class CustomNotFoundException extends RuntimeException{

    CustomNotFoundException(){

    }

    public CustomNotFoundException(String message) {
        super(message);
    }

}
