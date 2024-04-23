package org.supercoding.supertime.golbal.web.advice;

public class CustomMissingFileException extends RuntimeException{

    public CustomMissingFileException(String message){
        super(message);
    }
}
