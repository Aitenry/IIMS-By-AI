package com.toryu.iims.common.model.entity.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasePageQuery {

    //页码
    @ApiModelProperty("页码")
    private int page;

    //每页显示记录数
    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
