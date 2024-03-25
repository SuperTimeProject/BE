package org.supercoding.supertime.golbal.web.advice;

public class CustomNoSuchElementException extends RuntimeException{
    public CustomNoSuchElementException(String message){
        super(message);
    }
}
