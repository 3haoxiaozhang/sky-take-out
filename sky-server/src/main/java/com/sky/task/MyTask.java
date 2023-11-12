package com.sky.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自定义定时任务类
 */


@Component   //当前类也需要实例化并且交给spring实例化管理
@Slf4j
public class MyTask {

    /**
     * 定时任务的处理逻辑就需要写在这个方法里面
     */
    //@Scheduled(cron="0/5 * * * * ?")
    public void executeTake(){
       log.info("定时任务开始执行：{}",new Date());
    }


}
