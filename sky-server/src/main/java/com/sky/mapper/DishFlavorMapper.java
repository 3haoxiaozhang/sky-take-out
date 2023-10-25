package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据菜品id删除对应的口味数据
     * @param dishId
     */
    @Delete("delete from sky_take_out.dish_flavor where dish_id=#{dishId}")
    void deleteByIDishId(Long dishId);

    /**
     * 根据菜品id集合批量删除口味数据
     * @param ids
     */
    void deleteByIDishIds(List<Long> dishIds);

    /**
     * 根据菜品ID查询对应的口味数据
     * @param dishId
     * @return
     */
    @Select("select * from sky_take_out.dish_flavor where dish_id=#{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

}