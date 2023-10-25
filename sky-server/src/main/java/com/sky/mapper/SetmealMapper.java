package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

import java.util.List;


@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from sky_take_out.setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据分类id查询菜品
     * @param category_id
     * @return
     */
    @Select("select * from sky_take_out.dish where category_id=#{category_id}")
    List<Dish> selectById(Long category_id);

    /**
     * 新增套餐  (1.往套餐表中插入数据)
     * @param setmeal
     */
    @AutoFill( value = OperationType.INSERT)
    void save(Setmeal setmeal);

    /**
     *  新增套餐  （2.往套餐关系表中 插入）
     * @param list
     */

    void insertBatch(List<SetmealDish> list);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    List<Setmeal> pageQurey(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 判断是否为起售状态
     * @param id
     * @return
     */
    @Select("select status from sky_take_out.setmeal where id=#{id}")
    Long judge(Long id);



    /**
     * 不是起售状态，删除
     * @param id
     * @return
     */
    @Delete("delete from sky_take_out.setmeal where id=#{id}")
    void delete(long id);

    /**
     * 删除套餐菜品关系表中
     * @param setmeal_id
     */
    @Delete("delete from sky_take_out.setmeal_dish where setmeal_id=#{setmeal_id}")
    void deleteSetmealDish(Long setmeal_id);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.setmeal where id=#{id}")
    Setmeal selectSetmealById(Long id);

    /**
     * 先在套餐表中完成update操作
     * @param id
     */
    @AutoFill(value=OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

    /**
     * 直接禁售
     * @param id
     */
    @Update("update sky_take_out.setmeal set status=#{status} where id=#{id}")
    void stop(Integer status ,Long id);

    /**
     * 显得到套餐下的菜品
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.setmeal_dish where setmeal_id=#{id}")
    List<Dish> selectDishById(Long id);

    /**
     * 起售
     * @param id
     */
    @Update("update sky_take_out.setmeal set status=#{status} where id=#{id} ")
    void start(Integer status,Long id);

    @Select("select * from sky_take_out.setmeal_dish where id=#{id}")
    Setmeal select(Long id);
}
