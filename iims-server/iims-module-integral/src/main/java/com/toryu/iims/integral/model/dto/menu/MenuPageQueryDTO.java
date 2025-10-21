package com.toryu.iims.integral.model.dto.menu;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MenuPageQueryDTO implements Serializable {

    @ApiModelProperty("菜单id")
    private Long id;

    @ApiModelProperty("标题")
    private String title;

    //状态 0:禁用，1:启用
    @ApiModelProperty("状态 0:禁用，1:启用")
    private Integer status;

    @ApiModelProperty("页码")
    private int page;

    @ApiModelProperty("每页显示记录数")
    private int pageSize;
}
