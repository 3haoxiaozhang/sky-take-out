package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from sky_take_out.dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     * @param dish
     */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据主键查询菜品
     * @param id
     * @return
     */
    @Select("select * from sky_take_out.dish where id=#{id}")
    Dish getById(Long id);

    /**
     * 根据主键删除菜品数据
     * @param id
     */
    @Delete("delete from sky_take_out.dish where id=#{id} ")
    void deleteById(Long id);

    /**
     * 根据菜品id集合批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id来动态修改我们的菜品
     * @param dish
     */
    @AutoFill(value=OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据id查询菜品
     * @param dish
     * @return
     */
    List<Dish> list(Dish dish);

    /**
     * 起售禁售菜品
     * @param status
     * @param id
     */
    @Update("update sky_take_out.dish set status=#{status} where id=#{id}")
    void startOrStop(Integer status, Long id);

    /**
     * 根据条件统计菜品数量
     * @param map
     * @return
     */
    Integer countByMap(Map map);
}
