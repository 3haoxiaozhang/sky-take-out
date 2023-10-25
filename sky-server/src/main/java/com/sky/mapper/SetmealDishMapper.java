package com.sky.mapper;


import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    //Select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查找菜品
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.setmeal_dish where setmeal_id=#{setmeal_id}")
    List<SetmealDish> selectBysetmealId(Long setmeal_id);

    /**
     * 在套餐关系表中删除
     * @param id
     */
    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id=#{id}")
    void deleteBySetmealId(Long id);
}
