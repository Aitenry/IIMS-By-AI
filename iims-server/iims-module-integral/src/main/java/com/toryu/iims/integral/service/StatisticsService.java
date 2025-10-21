package com.toryu.iims.integral.service;

import com.toryu.iims.integral.model.vo.statistics.StatisticsDataVO;
import com.toryu.iims.integral.model.vo.statistics.StatisticsDayDataVO;

import java.time.LocalDate;

public interface StatisticsService {

    StatisticsDataVO getStatisticsData();

    StatisticsDayDataVO getStatisticsDayData();

    void generateDailyStatistics(LocalDate date);

}
