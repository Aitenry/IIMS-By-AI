package com.toryu.iims.integral.model.dto.admin;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AdminPageQueryDTO implements Serializable {

    //管理员真实姓名
    @ApiModelProperty("真实姓名")
    private String name;

    //管理员用户名
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("账户状态（1 禁用、0 正常）")
    private Boolean isDisable;

    //页码
    @ApiModelProperty("页码")
    private int page;

    //每页显示记录数
    @ApiModelProperty("每页显示记录数")
    private int pageSize;

}
