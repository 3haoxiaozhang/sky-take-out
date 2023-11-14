package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;


    /**
     * 统计指定时间区间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
      //当前集合用于存放从begin到end范围内的每天的集合
        List<LocalDate> dateList=new ArrayList<>();

        dateList.add(begin);
        while(!begin.equals(end)){
            //日期计算，计算指定日期的后一天对应的日期
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        String str1 = StringUtils.join(dateList, ",");

        List<Double> turnoverList=new ArrayList<>();
        for (LocalDate date : dateList) {
            //查询date日期对应的营业额数据，营业额是指：状态“已完成”的订单金额合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);  //例如2023年11月12日0时0分0秒
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //select sum(amount) from orders where orderTime > beginTime? and orderTime < endTime?  and status = 5

            Map map=new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);
            Double turnover=orderMapper.sumByMap(map);
            turnover=turnover==null? 0.0 :turnover;
            turnoverList.add(turnover);

        }
        String str2 = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO
                .builder()
                .dateList(str1)
                .turnoverList(str2)
                .build();
    }


    /**
     * 统计指定时间区间内的营用户数据
     * @param begin
     * @param end
     * @return
     */
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {

        //当前集合用于存放从begin到end范围内的每天的集合
        List<LocalDate> datelist=new ArrayList<>();

        datelist.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            datelist.add(begin);
        }

        //select count(id) from User where create_time < ? and create_time > ?
        //存放每天新增用户数量
        List<Integer> newUserList=new ArrayList<>();

        //select count(id) from User where crate_time < ?
        //存放每天的总用户数量
        List<Integer> totalUserList=new ArrayList<>();

        for (LocalDate date : datelist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map=new HashMap();
            map.put("end",endTime);

            //总的用户数量
            Integer totalUser = userMapper.countByMap(map);

            //新增用户数量
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);

            newUserList.add(newUser);
            totalUserList.add(totalUser);

        }
        //封装VO集合
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(datelist, ","))
                .totalUserList(StringUtils.join(newUserList, ","))
                .newUserList(StringUtils.join(totalUserList, ","))
                .build();

        return userReportVO;
    }

    /**
     * 统计指定时间区间内的订单数据
     * @param begin
     * @param end
     * @return
     */
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {

        //将日期封装到一个集合当中
        List<LocalDate> dateList=new ArrayList<>();

        dateList.add(begin);
        while(!begin.equals(end)){
            begin=begin.plusDays(1);
            dateList.add(begin);
        }
        //存放每天的订单总数
        List<Integer> orderCountList =new ArrayList<>();
        //存档每天有效订单数
        List<Integer> validOrderCountList=new ArrayList<>();

        //遍历dateList集合，查询每天有效订单数和订单总数
        for (LocalDate localDate : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(localDate, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(localDate, LocalTime.MAX);
            //查询每天订单总数  select count(id) from orders where order_time > ? and order_time < ?
            Integer orderCount = getOrderCount(beginTime, endTime, null);
            //查询每天有效订单数 select count(id) from orders where order_time > ? and order_time < ? and status = 5
            Integer validOrderCount = getOrderCount(beginTime, endTime, Orders.COMPLETED);

            orderCountList.add(orderCount);
            validOrderCountList.add(validOrderCount);
        }
        //时间区间内的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();

        //时间区间内的有效订单总数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        //计算订单完成率
        Double orderCompletionRate=0.0;
        if(totalOrderCount!=0){
            orderCompletionRate= validOrderCount.doubleValue()/totalOrderCount;
        }

        return  OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList,","))
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 根据条件统计订单数量
     * @param begin
     * @param end
     * @param status
     * @return
     */
    private Integer getOrderCount(LocalDateTime begin,LocalDateTime end,Integer status){
        Map map=new HashMap();
        map.put("begin",begin);
        map.put("end",end);
        map.put("status",status);
       return orderMapper.countByMap(map);
    }

    /**
     * 销量排名top10
     * @param begin
     * @param end
     * @return
     */
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
     //select od.name,sum(od.number) number from order_details od ,orders o where o.id=od.order_id and o.status = 5 and o.order_time > '?' and order_time < '?'
     //group by od.name   order by number desc   limit 0,10

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> saleTop10 = orderMapper.getSaleTop10(beginTime, endTime);

        String names=saleTop10.get(0).getName();
        String number=saleTop10.get(0).getNumber().toString();
        for (int i = 1; i < saleTop10.size(); i++) {
            names=names+","+saleTop10.get(i).getName();
        }
        for (int i = 1; i < saleTop10.size(); i++) {
            number=number+","+saleTop10.get(i).getNumber().toString();
        }

//        List<Integer> names = saleTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
//        StringUtils.join(names,",");
//        List<Integer> numbers = saleTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
//        StringUtils.join(numbers,",");

        //封装返回结果数据
          return SalesTop10ReportVO
                  .builder()
                  .nameList(names)
                  .numberList(number)
                  .build();

    }

    /**
     * 导出运营数据报表
     * @param response
     */
    public void exportBusinessData(HttpServletResponse response)  {

      //1.查询数据库，获取营业数据  ---  查询最近30天的运营数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);

        LocalDateTime beginTime = LocalDateTime.of(dateBegin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(dateEnd, LocalTime.MAX);

        //查询概览数据
        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);


      //2.通过POI将数据写入到Excel文件中
        InputStream in  = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");



        try {
            //基于模板文件创建一个新的文件
            XSSFWorkbook excel = new XSSFWorkbook(in);


            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充数据 -- 时间
            sheet.getRow(1).getCell(1).setCellValue("时间："+beginTime+"至"+endTime);

            //营业额
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());

            //订单完成率
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());

            //新增用户数
            row.getCell(6).setCellValue(businessData.getNewUsers());

            //有效订单
             row = sheet.getRow(4);
             row.getCell(2).setCellValue(businessData.getValidOrderCount());
             row.getCell(4).setCellValue(businessData.getUnitPrice());


             //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = dateBegin.plusDays(i);
                //查询某一天的营业数据
                BusinessDataVO businessData1 = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                //获得某一行
                row = sheet.getRow(7 + i);

                row.getCell(2).setCellValue(date.toString());
                row.getCell(2).setCellValue(businessData1.getTurnover());
                row.getCell(3).setCellValue(businessData1.getValidOrderCount());
                row.getCell(4).setCellValue(businessData1.getOrderCompletionRate());
                row.getCell(5).setCellValue(businessData1.getUnitPrice());
                row.getCell(6).setCellValue(businessData1.getNewUsers());



            }

            //3.通过输出流将Excel文件下载到客户端浏览器
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();

        } catch (IOException e) {
            e.printStackTrace();
        }



    }




}
