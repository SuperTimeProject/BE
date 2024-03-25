package org.supercoding.supertime.golbal.web.advice;

public class CustomNotFoundException extends RuntimeException{

    CustomNotFoundException(){

    }

    public CustomNotFoundException(String message) {
        super(message);
    }

}
