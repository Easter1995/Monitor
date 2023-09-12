package com.glimmer.dto;

import lombok.Data;

/**
 * DTO 数据传输对象
 * 用于接受封装前端传输过来的数据
 * UserDeleteDTO 接受前端传过来的用户名数据
 * @Data 提供类的get,set,equals,hashCode,toString方法
 * implements Serializable 实现序列化接口
 */
@Data
public class UserDeleteDTO {
    private String[] username;
}
