package org.supercoding.supertime.web.advice;

public class CustomNoSuchElementException extends RuntimeException{
    public CustomNoSuchElementException(String message){
        super(message);
    }
}
