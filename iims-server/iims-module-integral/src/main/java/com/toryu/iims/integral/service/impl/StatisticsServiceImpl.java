package com.toryu.iims.integral.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.toryu.iims.integral.mapper.StatisticsMapper;
import com.toryu.iims.integral.model.entity.StatisticsEntity;
import com.toryu.iims.integral.model.vo.statistics.StatisticsDataVO;
import com.toryu.iims.integral.model.vo.statistics.StatisticsDayDataVO;
import com.toryu.iims.integral.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final StatisticsMapper statisticsMapper;

    public StatisticsServiceImpl(StatisticsMapper statisticsMapper) {
        this.statisticsMapper = statisticsMapper;
    }

    @Override
    public StatisticsDataVO getStatisticsData() {
        return statisticsMapper.getStatisticsData();
    }

    @Override
    public StatisticsDayDataVO getStatisticsDayData() {
        List<StatisticsEntity> statisticsAllData = statisticsMapper.getStatisticsAllData();

        // 预先解析所有数据，避免重复解析
        List<StatisticsDataVO> dataVOList = statisticsAllData.stream()
                .map(statisticsEntity -> {
                    try {
                        return JSONObject.parseObject(statisticsEntity.getStatisticalData(), StatisticsDataVO.class);
                    } catch (Exception e) {
                        // 如果解析失败，返回默认值
                        return StatisticsDataVO.builder()
                                .fileCount(0)
                                .logCount(0)
                                .userCount(0)
                                .articleCount(0)
                                .wikiCount(0)
                                .build();
                    }
                }).toList();

        return StatisticsDayDataVO.builder()
                .statisticsDays(statisticsAllData.stream()
                        .map(StatisticsEntity::getStatisticalTime)
                        .map(LocalDateTime::toLocalDate)
                        .collect(Collectors.toList()))
                .fileCounts(dataVOList.stream()
                        .map(dataVO -> dataVO.getFileCount() != null ? dataVO.getFileCount() : 0)
                        .collect(Collectors.toList()))
                .logCounts(dataVOList.stream()
                        .map(dataVO -> dataVO.getLogCount() != null ? dataVO.getLogCount() : 0)
                        .collect(Collectors.toList()))
                .userCounts(dataVOList.stream()
                        .map(dataVO -> dataVO.getUserCount() != null ? dataVO.getUserCount() : 0)
                        .collect(Collectors.toList()))
                .articleCounts(dataVOList.stream()
                        .map(dataVO -> dataVO.getArticleCount() != null ? dataVO.getArticleCount() : 0)
                        .collect(Collectors.toList()))
                .wikiCounts(dataVOList.stream()
                        .map(dataVO -> dataVO.getWikiCount() != null ? dataVO.getWikiCount() : 0)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public void generateDailyStatistics(LocalDate date) {
        // 生成指定日期的统计数据
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(23, 59, 59);
        StatisticsDataVO dailyStats = statisticsMapper.getStatisticsDataByDay(startDate, endDate);

        if (dailyStats != null) {
            try {
                // 检查是否已存在该日期的统计数据
                StatisticsEntity existing = statisticsMapper.getStatisticsDataByDate(date);

                StatisticsEntity entity = new StatisticsEntity();
                entity.setStatisticalData(JSONObject.toJSONString(dailyStats));
                entity.setStatisticalTime(startDate);

                if (existing != null) {
                    statisticsMapper.updateStatisticsData(entity);
                } else {
                    statisticsMapper.insertStatisticsData(entity);
                }
            } catch (Exception e) {
                throw new RuntimeException("生成统计数据失败", e);
            }
        }
    }

}
