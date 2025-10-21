package com.toryu.iims.common.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseAdminInfoQueryDTO {
    @ApiModelProperty("用户名")
    private String username;
}
