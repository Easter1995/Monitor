package com.glimmer.mapper;

import com.glimmer.entity.Alert;
import com.glimmer.entity.Camera;
import com.glimmer.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * mapper层接口，用于与数据库进行交互
 */
@Mapper
public interface AlertTransactionMapper {
    /**
    * 查询返回全部的报警信息
    * @return
    */
    @Select("select * from alert")
    List<Alert> list();


    /**
     * 查询报警视频信息
     * @param pathVideo
     * @return
     */
    @Select("select * from alert where path_video = #{pathVideo}")
    Alert getByVideoPath(String pathVideo);

    /**
     * 查询报警图片信息
     * @param pathPhoto
     * @return
     */
    @Select("select * from alert where path_photo = #{pathPhoto}")
    Alert getByPhotoPath(String pathPhoto);

    /**
     * 删除相应报警视频
     * @param pathVideo
     */
    @Delete("delete from alert where path_video = #{pathVideo}")
    void deleteByVideoPath(String pathVideo);

    /**
     *删除相应报警图片
     * @param pathPhoto
     */
    @Delete("delete from alert where path_photo = #{pathPhoto}")
    void deleteByPhotoPath(String pathPhoto);

    /**
     * 增加报警照片到alert表中
     * @param alert
     */
    @Insert("insert into alert(ca_id,alert_time,type,path_video,path_photo) values (#{caId},#{alertTime},#{type},#{pathVideo},#{pathPhoto})")
    void addAlert(Alert alert);

    /**
     * 动态修改警报数据
     * @param alert
     */@Update("update alert set path_video=#{pathVideo} where id=#{id}")//只修改一部分信息
    void updateAlert(Alert alert);

}
