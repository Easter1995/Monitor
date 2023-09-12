package com.glimmer.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * Entity 这里存放表对应的完整的实体类
 * Alert实体类
 * @Data 提供类的get,set,equals,hashCode,toString方法
 * @Builder 开启用build方式创建对象(可以为对象属性链式赋值)
 * @NoArgsConstructor 提供类的无参构造
 * @AllArgsConstructor 提供类的有参构造
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert implements Serializable {
    private Integer id;
    private Integer caId;
    private Long alertTime;
    private String type;
    private String pathVideo;
    private String pathPhoto;
}
