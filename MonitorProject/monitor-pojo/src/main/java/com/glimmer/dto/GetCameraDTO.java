package com.glimmer.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * DTO 数据传输对象
 * 用于接受封装前端传输过来的数据
 * GetCameraDTO 接受前端传过来的摄像头名称数据
 * @Data 提供类的get,set,equals,hashCode,toString方法
 * implements Serializable 实现序列化接口
 */
@Data
public class GetCameraDTO implements Serializable {
   String[] cam_names;
}
