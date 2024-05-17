package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 统计指定时间内的营业额数据
     * @param begin
     * @param end
     * @return
     */
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> datalist = new ArrayList<>();
        datalist.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            datalist.add(begin);
        }

        List<Double> TurnoverList = new ArrayList<>();
        for (LocalDate date : datalist) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap();
            map.put("begin", beginTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);

            Double Turnover = orderMapper.sumByMap(map);
            Turnover = Turnover == null ? 0.0 : Turnover;
            TurnoverList.add(Turnover);
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(datalist, ","))
                .turnoverList(StringUtils.join(TurnoverList, ","))
                .build();
    }
}
