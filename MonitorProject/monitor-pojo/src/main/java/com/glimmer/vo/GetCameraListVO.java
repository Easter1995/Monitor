package com.glimmer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 获取摄像头列表返回模型类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetCameraListVO implements Serializable {
    HashMap<String, Object> cameraInfo;
    String message;
    Integer status;
}
