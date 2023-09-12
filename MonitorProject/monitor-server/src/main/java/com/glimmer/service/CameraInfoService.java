package com.glimmer.service;

import java.util.HashMap;

//摄像头检测信息service层接口,在这里定义相关的业务接口
public interface CameraInfoService {

    /**
     * 获取摄像头检测信息
     * @return
     */
    HashMap<String, Object> getInfo();
}
