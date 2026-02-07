package com.toryu.iims.integral.model.vo.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticsDataVO {

    private Integer fileCount;

    private Integer logCount;

    private Integer userCount;

    private Integer articleCount;

    private Integer wikiCount;

}
