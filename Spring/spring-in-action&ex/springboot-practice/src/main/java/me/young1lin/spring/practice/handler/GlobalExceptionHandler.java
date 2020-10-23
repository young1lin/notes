package me.young1lin.spring.practice.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/23 4:45 下午
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> defaultExceptionHandler(Exception ex) {
        log.info(Arrays.toString(ex.getStackTrace()));
        return new ResponseEntity<Object>("系统异常", HttpStatus.INTERNAL_SERVER_ERROR) ;
    }

}
