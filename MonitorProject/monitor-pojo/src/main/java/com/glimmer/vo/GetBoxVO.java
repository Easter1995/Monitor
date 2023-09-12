package com.glimmer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 获取检测框返回模型类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetBoxVO implements Serializable {
    private String box;
    private String message;
    private Integer status;
}
