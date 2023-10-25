package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {





    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    SetmealVO selectById(Long id);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void revise(SetmealDTO setmealDTO);

    /**
     * 起售，禁售套餐
     * @param status
     * @param id
     */
    void startORstop(Integer status, Long id);
}
