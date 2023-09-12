package com.glimmer.mapper;

import com.glimmer.entity.Camera;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * mapper层接口，用于与数据库进行交互
 */
@Mapper
public interface CameraTransactionMapper {

    /**
     * 通过用户名获取摄像头信息
     * @param name
     * @return
     */


    /**
     * 插入摄像头数据
     * @param camera
     */
    @Insert("insert into camera(name,ip,port,channel,user,passwd,area,start_time,end_time,infer_class) values (#{name},#{ip},#{port},#{channel},#{user},#{passwd},#{area},#{startTime},#{endTime},#{inferClass})")
    void add(Camera camera);



    /**
     * 按caId查询摄像头数据
     * @param caId
     * @return
     */
    @Select("select * from camera where ca_id = #{caId}")
    List<Camera> getByCaId(Integer caId);



    /**
     * 查询返回所有摄像头数据
     * @return
     */
    @Select("select * from camera")
    List<Camera> list();



    /**
     * 动态修改摄像头数据
     * @param camera
     */
    @Update("update camera set start_time=#{startTime},end_time=#{endTime} where name=#{name}")//只修改一部分信息
    void update(Camera camera);



    /**
     * 根据名称删除摄像头
     * @param name
     */
    @Delete("delete from camera where name = #{name}")
    void deleteByName(String name);
}
