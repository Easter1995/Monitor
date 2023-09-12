package com.glimmer.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Entity 这里存放表对应的完整的实体类
 * Camera实体类
 * @Data 提供类的get,set,equals,hashCode,toString方法
 * @Builder 开启用build方式创建对象(可以为对象属性链式赋值)
 * @NoArgsConstructor 提供类的无参构造
 * @AllArgsConstructor 提供类的有参构造
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Camera implements Serializable {
    //表示序列化的版本UID
    private static final long serialVersionUID = 1L;
    /**
     * 下面是摄像头camera的相关属性
     * 分别与数据库表的字段相对应
     */
    private Integer caId;
    private String name;
    private String ip;
    private String port;
    private Long channel;
    private String user;
    private String passwd;
    private String area;
    private Long startTime;
    private Long endTime;
    private String inferClass;
}
