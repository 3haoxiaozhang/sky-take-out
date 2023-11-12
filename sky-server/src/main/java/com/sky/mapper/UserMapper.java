package com.sky.mapper;


import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     *根据openid来查询用户
     * @param openid
     * @return
     */
    @Select("select * from sky_take_out.user where openid=#{openid}")
    User getByOpenid(String openid);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    @Select("select * from sky_take_out.user where id =#{id}")
    User getById(Long userId);

    /**
     * 根据动态条件来统计用户数量
     * @return
     */
    Integer countByMap(Map map);
}
