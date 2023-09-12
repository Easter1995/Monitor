package com.glimmer.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * DTO 数据传输对象
 * 用于接受封装前端传输过来的数据
 * DeleteCameraDTO 接受前端传过来的摄像头名称
 * @Data 提供类的get,set,equals,hashCode,toString方法
 * implements Serializable 实现序列化接口
 */
@Data
public class DeleteCameraDTO implements Serializable {
    //定义接受数据类型
    private String name;
}
