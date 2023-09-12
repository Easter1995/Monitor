package com.glimmer.handler;


import com.glimmer.constant.*;
import com.glimmer.exception.*;
import com.glimmer.result.Result;
import com.glimmer.vo.StatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常并进行处理
     * @param ex
     * @return
     */
    @ExceptionHandler
    public ResponseEntity exceptionHandler(BaseException ex){
        //处理用户登录账号不存在异常
        if (ex instanceof AccountNotFoundException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(LoginStatusConstant.LOGIN_FAILED_CAN_TRY)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.NOT_FOUND,statusVO,MessageConstant.ACCOUNT_NOT_FOUND), HttpStatus.NOT_FOUND);

        }
        //处理账号被锁定异常
        if(ex instanceof AccountLockedException){
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(LoginStatusConstant.LOGIN_FAILED_AND_STOP)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.FORBIDDEN,statusVO,MessageConstant.ACCOUNT_LOCKED),HttpStatus.FORBIDDEN);
        }
        //处理登录失败异常(不在登录时间段)
        if(ex instanceof LoginFailedException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(LoginStatusConstant.LOGIN_FAILED_CAN_TRY)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.FORBIDDEN,statusVO,MessageConstant.LOGIN_FAILED),HttpStatus.FORBIDDEN);
        }
        //处理密码错误异常
        if(ex instanceof PasswordErrorException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(LoginStatusConstant.LOGIN_FAILED_CAN_TRY)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.FORBIDDEN,statusVO,MessageConstant.PASSWORD_ERROR),HttpStatus.FORBIDDEN);
        }
        //处理密码错误导致账户冻结异常
        if(ex instanceof AccountFreezeException){
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(LoginStatusConstant.LOGIN_FAILED_AND_STOP)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.FORBIDDEN,statusVO,MessageConstant.PASSWORD_ERROR),HttpStatus.FORBIDDEN);
        }
        //处理用户权限不足(非管理员)导致的异常
        if(ex instanceof AuthorityLimitException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(RegisterStatusConstant.AUTHORITY_LIMIT)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.FORBIDDEN,statusVO,MessageConstant.AUTHORITY_LIMIT),HttpStatus.FORBIDDEN);
        }
        //处理各种格式错误造成的异常
        if (ex instanceof FormatException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(LoginStatusConstant.REQUEST_ERROR)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.BAD_REQUEST,statusVO,MessageConstant.FORMAT_ERROR_WARNING),HttpStatus.BAD_REQUEST);
        }
        //处理更新用户信息时用户名不存在造成的异常
        if(ex instanceof UserNameNotFoundException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(ChangeStatusConstant.FAILED)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.FORBIDDEN,statusVO,MessageConstant.USERNAME_NOT_EXIST),HttpStatus.FORBIDDEN);
        }
        //处理报警信息不存在异常
        if (ex instanceof AlertNotFoundException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(AlertStatusConstant.FAILED)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.BAD_REQUEST,statusVO,ex.getMessage()),HttpStatus.BAD_REQUEST);
        }
        //处理各种数据已存在异常
        if(ex instanceof ExistException) {
            log.error("异常信息：{}",ex.getMessage());
            StatusVO statusVO = StatusVO.builder()
                    .status(BoxStatusConstant.FAILED)
                    .message(ex.getMessage())
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.BAD_REQUEST,statusVO,ex.getMessage()),HttpStatus.BAD_REQUEST);
        }
        //这里续写其他异常处理

        //其他类型的业务异常统一处理
        log.error("异常信息：{}", ex.getMessage());
        StatusVO statusVO = StatusVO.builder()
                .status(GetStatusConstant.FAILURE)
                .message(ex.getMessage()).build();
        return new ResponseEntity(Result.error(HttpStatusConstant.SERVICE_UNAVAILABLE,statusVO,ex.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }


    /**
     * 统一处理SQL异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public ResponseEntity exceptionHandler(SQLException ex) {
        log.error("异常信息：{}",ex.getMessage());

        if (ex instanceof SQLIntegrityConstraintViolationException) {
            /**
             * 处理SQLIntegrityConstraintViolationException异常
             * 这里处理的是注册用户已存在而不满足数据库字段唯一性约束异常
             */
            String message = ex.getMessage();
            //根据信息判断是否是插入用户名字段相同而抛出的异常
            if(message.contains("Duplicate entry")) {
                String[] split = message.split(" ");
                String username = split[2];
                String msg = username + MessageConstant.ALREADY_EXIST;
                StatusVO statusVO = StatusVO.builder()
                        .status(RegisterStatusConstant.USER_EXIST)
                        .message(msg)
                        .build();
                return new ResponseEntity(Result.error(HttpStatusConstant.FORBIDDEN, statusVO,msg), HttpStatus.FORBIDDEN);
            }
        }
        //判断是否是SQLDataException异常，即在数据库层面校验请求数据是否有误
        if (ex instanceof SQLDataException) {
            StatusVO statusVO = StatusVO.builder()
                    .status(LoginStatusConstant.REQUEST_ERROR)
                    .message(MessageConstant.FORMAT_ERROR)
                    .build();
            return new ResponseEntity(Result.error(HttpStatusConstant.BAD_REQUEST,statusVO,MessageConstant.FORMAT_ERROR_WARNING),HttpStatus.BAD_REQUEST);
        }
        /**
         * 统一处理其他SQL异常
         */
        StatusVO statusVO = StatusVO.builder()
                .status(RegisterStatusConstant.REQUEST_ERROR)
                .message(MessageConstant.UNKNOWN_ERROR)
                .build();
        return new ResponseEntity(Result.error(HttpStatusConstant.SERVICE_UNAVAILABLE, statusVO,MessageConstant.UNKNOWN_ERROR),HttpStatus.SERVICE_UNAVAILABLE);
    }

}
