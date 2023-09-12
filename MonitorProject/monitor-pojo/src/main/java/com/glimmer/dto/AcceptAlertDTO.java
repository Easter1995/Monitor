package com.glimmer.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 接受报警信息数据传输类
 */
@Data
public class AcceptAlertDTO implements Serializable {
    private String pathVideo;
    private String pathPhoto;
}
