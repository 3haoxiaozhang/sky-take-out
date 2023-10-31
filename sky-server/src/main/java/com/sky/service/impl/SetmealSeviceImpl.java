package com.sky.service.impl;


import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealSeviceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;

    /**
     * 新增套餐
     * 需保持套餐和菜品的关联关系
     * @param setmealDTO
     */

    @Transactional
    public void save(SetmealDTO setmealDTO) {


        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //套餐插进去会返回一个主键值
        setmealMapper.save(setmeal);
        //将套餐菜品表中的套餐 setmealId赋上值
        List<SetmealDish> SetmealDishes=setmealDTO.getSetmealDishes();
        SetmealDishes.forEach(lists->{
            lists.setSetmealId(setmeal.getId());
        });
       setmealMapper.insertBatch(SetmealDishes);

    }

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());

         List<Setmeal> list=setmealMapper.pageQurey(setmealPageQueryDTO);

         PageResult pageResult=new PageResult();
         pageResult.setTotal(list.size());
         pageResult.setRecords(list);

         return pageResult;
    }

    /**
     * 批量删除套餐
     * @param ids
     */
    @Transactional
    public void delete(List<Long> ids) {

        //先判断是否为起售状态，起售状态不删除，停售删除

        for (int id = 0; id < ids.size(); id++) {
           if(setmealMapper.judge(ids.get(id)).equals(0L))
               //删除套餐表中的数据，返回主键值
           {
               setmealMapper.delete(ids.get(id));

               //删除套餐菜品表中的数据
               setmealMapper.deleteSetmealDish(ids.get(id));
           }
           else throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);

        }

    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    public SetmealVO selectById(Long id) {
        //根据id查询套餐
        Setmeal setmeals=setmealMapper.selectSetmealById(id);
        //根据套餐id查询
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBysetmealId(id);
        //返回SetmealVO集合
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeals,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐
     * @param
     */
   @Transactional
    public void revise(SetmealDTO setmealDTO) {

        //套餐表中执行update操作
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateSetmeal(setmeal);


        //先到套餐菜品关系表中执行删除操作
        setmealDishMapper.deleteBySetmealId(setmeal.getId());

        //再重新插入
       List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
       setmealDishes.forEach(setmealDish -> {
           setmealDish.setSetmealId(setmeal.getId());
       });
         setmealMapper.insertBatch(setmealDishes);


    }

    /**
     * 起售禁售套餐
     * @param status
     * @param id
     */
    @Transactional
    public void startORstop(Integer status, Long id) {
        /*
           可以对状态为起售的套餐进行停售操作，可以对状态为停售的套餐进行起售操作
           起售的套餐可以展示在用户端，停售的套餐不能展示在用户端
           起售套餐时，如果套餐内包含停售的菜品，则不能起售
        */
        //直接禁售
        if(status==0){
            setmealMapper.stop(status,id);
        }
        //想要启售
        else{
            //先得到套餐下面的菜品
            List<Dish> lists =setmealMapper.selectDishById(id);

            //循环判断是否存在停售的菜品
            lists.forEach(list->{
                if(list.getStatus()==StatusConstant.DISABLE)
                     throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
            });
            setmealMapper.start(status,id);
        }


    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }


}
