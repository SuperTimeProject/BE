package org.supercoding.supertime.golbal.web.advice;

public class CustomAccessDeniedException extends RuntimeException{

    public CustomAccessDeniedException(String message){
        super(message);
    }
}
