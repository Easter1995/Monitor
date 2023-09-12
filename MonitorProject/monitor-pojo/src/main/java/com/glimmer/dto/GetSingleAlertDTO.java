package com.glimmer.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 获取单个报警信息
 * 用于主动推送给用户新上传的报警信息
 */

@Data
@Builder
public class GetSingleAlertDTO implements Serializable {
    private Integer caId;
    private Integer alertTime;
    private String alertType;
}
