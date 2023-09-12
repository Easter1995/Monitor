package com.glimmer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * VO 视图模型
 * 用于返回给前端封装好的数据模型
 * UserLoginVO 返回响应的信息，状态码，jwt授权的token
 * @Data 提供类的get,set,equals,hashCode,toString方法
 * @Builder 开启用build方式创建对象(可以为对象属性链式赋值)
 * @NoArgsConstructor 提供类的无参构造
 * @AllArgsConstructor 提供类的有参构造
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginVO implements Serializable {
    private String message;
    private Integer status;
    private String authorization;
}
