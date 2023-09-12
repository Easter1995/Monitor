package com.glimmer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 获取报警信息返回模型类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAlertVO implements Serializable {
    private String message;
    private Integer status;
    private List<AlertVO> infos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AlertVO{
        private Long alertTime;
        private Integer caId;
        private String photoPath;
        private String type;
        private String videoPath;
    }
}
