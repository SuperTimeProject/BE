package org.supercoding.supertime.web.advice;

public class CustomAccessDeniedException extends RuntimeException{

    public CustomAccessDeniedException(String message){
        super(message);
    }
}
