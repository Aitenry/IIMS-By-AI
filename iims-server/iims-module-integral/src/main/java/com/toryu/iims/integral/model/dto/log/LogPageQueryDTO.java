package com.toryu.iims.integral.model.dto.log;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: Aitenry
 * @Date: 2025/11/23 14:25
 * @Version: v1.0.0
 * @Description: TODO
 **/
@Data
public class LogPageQueryDTO {

    private String level;

    private String loggerName;

    //页码
    @ApiModelProperty("页码")
    private int page;

    //每页显示记录数
    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
