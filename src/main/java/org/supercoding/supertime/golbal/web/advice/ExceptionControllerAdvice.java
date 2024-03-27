package org.supercoding.supertime.golbal.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.supercoding.supertime.golbal.web.dto.CommonResponseDto;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    public ResponseEntity<CommonResponseDto> handleNotFoundException(CustomNotFoundException e){
        log.error("[NOTFOUND] DB에 일치하는 결과가 없어 다음의 에러메시지를 출력합니다." + e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                CommonResponseDto.builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message(e.getMessage())
                        .success(false)
                        .build()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<CommonResponseDto> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("[CONFLICT] 데이터 무결성 에러로 다음의 에러메시지를 출력합니다." + e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                CommonResponseDto.builder()
                        .code(HttpStatus.CONFLICT.value())
                        .message(e.getMessage())
                        .success(false)
                        .build()
        );
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNoSuchElementException.class)
    public ResponseEntity<CommonResponseDto> handleNoSuchElementException(CustomNoSuchElementException e) {
        log.error("[NO_CONTENT] 데이터 무결성 에러로 다음의 에러메시지를 출력합니다." + e.getMessage());
        CommonResponseDto responseDto = CommonResponseDto.builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .success(false)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomAccessDeniedException.class)
    public ResponseEntity<CommonResponseDto> handleAccessDeniedException(CustomAccessDeniedException e) {
        log.error("[CONFLICT] 인증 에러로 다음의 에러메시지를 출력합니다." + e.getMessage());
        CommonResponseDto responseDto = CommonResponseDto.builder()
                .code(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .success(false)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseDto);
    }
}
