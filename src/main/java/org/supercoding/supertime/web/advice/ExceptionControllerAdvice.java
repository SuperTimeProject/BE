package org.supercoding.supertime.web.advice;

import com.amazonaws.services.kms.model.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.supercoding.supertime.web.dto.common.CommonResponseDto;

@RestControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CommonResponseDto> handleNotFoundException(NotFoundException e){
        log.error("[NOTFOUND] DB 검색 에러로 다음의 에러메시지를 출력합니다." + e.getMessage());
        return ResponseEntity.ok().body(
                CommonResponseDto.builder()
                        .code(404)
                        .message(e.getMessage())
                        .success(false)
                        .build()
        );
    }
}
