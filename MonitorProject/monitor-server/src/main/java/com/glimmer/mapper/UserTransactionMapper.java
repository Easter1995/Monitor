package com.glimmer.mapper;

import com.glimmer.entity.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * DAO层，进行数据库访问操作
 * 整合了MyBatis持久层框架
 * UserTransactionMapper，对User表进行相关操作的接口
 */
@Mapper
public interface UserTransactionMapper {
    /**
     * 通过用户名查询用户
     * @param name
     * @return
     */
    @Select("select * from user where name = #{name}")
    User getByName(String name);

    /**
     * 动态更新用户表
     * @param user
     */
    void update(User user);

    /**
     * 插入用户数据
     * @param user
     */
    @Insert("insert into user(name,passwd,role,deadline_time,login_start,login_end) values (#{name},#{passwd},#{role},#{deadlineTime},#{loginStart},#{loginEnd})")
    void insert(User user);

    /**
     * 查询所有用户数据
     * @return
     */
    @Select("select * from user")
    List<User> list();

    /**
     * 根据用户名删除用户信息数据
     * @param name
     */
    @Delete("delete from user where name = #{name}")
    void deleteByName(String name);
}
