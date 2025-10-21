package com.toryu.iims.integral.controller;

import com.toryu.iims.common.result.Result;
import com.toryu.iims.integral.model.vo.statistics.StatisticsDataVO;
import com.toryu.iims.integral.model.vo.statistics.StatisticsDayDataVO;
import com.toryu.iims.integral.service.StatisticsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/iims/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/data")
    @ApiOperation(value = "获取数据统计")
    public Result<StatisticsDataVO> getStatisticsData() {
        return Result.success(statisticsService.getStatisticsData());
    }

    @GetMapping("/day/data")
    public Result<StatisticsDayDataVO> getStatisticsDayData() {
        return Result.success(statisticsService.getStatisticsDayData());
    }

}
