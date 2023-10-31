package com.sky.controller.admin;


import com.sky.dto.DishDTO;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;



    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
   @GetMapping("/page")
   @ApiOperation("分页查询")
    public Result<PageResult> Page(SetmealPageQueryDTO setmealPageQueryDTO){
       log.info("开始分页查询");
        PageResult pageResult=setmealService.page(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 批量删除套餐
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result delete(@RequestParam  List<Long> ids){
        setmealService.delete(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO>  selectById(@PathVariable Long id){
        log.info("根据id查询套餐");
        SetmealVO setmealVO=setmealService.selectById(id);
        return Result.success(setmealVO);
    }

    @PutMapping
    @ApiOperation("修改套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result revise(@RequestBody SetmealDTO setmealDTO){
        setmealService.revise(setmealDTO);
        return Result.success();
    }

    /**
     * 起售，禁售套餐
     * @param status
     * @param id
     * @return
     */

    @PostMapping("status/{status}")
    @ApiOperation("起售，禁售套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startORstop(@PathVariable Integer status,Long id){
        setmealService.startORstop(status,id);
        return Result.success();
    }


}
