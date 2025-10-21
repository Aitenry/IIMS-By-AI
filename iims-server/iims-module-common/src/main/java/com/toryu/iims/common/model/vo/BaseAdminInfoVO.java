package com.toryu.iims.common.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "管理员登录返回的数据格式")
@Builder
public class BaseAdminInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    //用户名
    @ApiModelProperty("用户名")
    private String username;

    //用户邮箱
    @ApiModelProperty("用户邮箱")
    private String email;

    //手机号
    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("头像地址")
    private String imageUrl;

    @ApiModelProperty("归属部门")
    private String department;

    @ApiModelProperty("简介")
    private String introduction;

}
