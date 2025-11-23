package com.toryu.iims.integral.model.vo.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 15:24
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LogVO {

    private Long id;

    private String level;

    private String threadName;

    private String loggerName;

    private String message;

    private LocalDateTime createTime;

}
