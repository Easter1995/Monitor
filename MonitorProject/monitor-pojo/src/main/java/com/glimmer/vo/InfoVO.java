package com.glimmer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 获取摄像头信息返回模型类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfoVO implements Serializable {

    private String box;
    private Integer caId;
    private String[] inferClass;
    private String startTime;
    private String endTime;

}
