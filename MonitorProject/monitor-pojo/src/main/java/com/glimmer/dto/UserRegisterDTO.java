package com.glimmer.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO 数据传输对象
 * 用于接受封装前端传输过来的数据
 * UserRegisterDTO 接受前端传过来的用户注册数据
 * @Data 提供类的get,set,equals,hashCode,toString方法
 */
@Data
public class UserRegisterDTO implements Serializable {
    private String name;
    private String passwd;
    private Integer role;
    private String deadlineTime;
    private String loginStart;
    private String loginEnd;
}
